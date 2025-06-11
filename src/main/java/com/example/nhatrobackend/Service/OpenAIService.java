package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.ChatRequest;
import com.example.nhatrobackend.DTO.request.MessageCreateRequestDto;
import com.example.nhatrobackend.DTO.request.RoomSearchDto;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.RoleChat;
import com.example.nhatrobackend.Entity.MessageChatBot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {
    @Value("${openai.azure.api-key}")
    private String azureApiKey;

    @Value("${openai.azure.endpoint}")
    private String azureEndpoint;

    @Value("${openai.azure.deployment-name}")
    private String deploymentName;

    private final PostService postService;
    private final ChatBotService chatBotService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("Azure OpenAI Config - Endpoint: {}, Deployment: {}, API Key length: {}", 
            azureEndpoint, 
            deploymentName,
            azureApiKey != null ? azureApiKey.length() : 0);
    }

    public String chatWithToolCalls(ChatRequest request) throws Exception {
        var messageList = chatBotService.getMessages(request.getConversationId());
        List<Map<String, Object>> messages = new ArrayList<>();

        // Add system prompt
        messages.add(Map.of(
                "role", RoleChat.system,
                "content", systemPrompt()
        ));

        // Add message history
        if (messageList != null) {
            for (var message : messageList) {
                messages.add(Map.of(
                        "role", message.getRole().toString(),
                        "content", message.getContent()
                ));
            }
        }

        // Add user message
        messages.add(Map.of(
                "role", RoleChat.user.toString(),
                "content", request.getContent()
        ));

        List<Map<String, Object>> functions = getDefinedFunctions();

        boolean hasToolCalls = true;
        String responseBody = null;
        JsonNode responseJson = null;

        while (hasToolCalls) {
            responseBody = callOpenAI(messages, functions);
            responseJson = mapper.readTree(responseBody);

            JsonNode toolCall = responseJson.at("/choices/0/message/function_call");

            if (toolCall != null && !toolCall.isMissingNode() && toolCall.isObject()) {
                String functionName = toolCall.get("name").asText();
                JsonNode argumentsNode = toolCall.get("arguments");

                String toolResponse = processToolCall(functionName, argumentsNode);

                // Add assistant message with function_call
                Map<String, Object> assistantMsg = new HashMap<>();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", null);
                assistantMsg.put("function_call", Map.of(
                        "name", functionName,
                        "arguments", argumentsNode
                ));
                messages.add(assistantMsg);

                // Add tool message with function name and response
                Map<String, Object> toolMsg = new HashMap<>();
                toolMsg.put("role", "function");
                toolMsg.put("name", functionName);
                toolMsg.put("content", toolResponse);
                messages.add(toolMsg);
            } else {
                hasToolCalls = false;
            }
        }

        JsonNode finalMessageNode = responseJson.at("/choices/0/message/content");
        if (!finalMessageNode.isMissingNode()) {
            addMessage(request.getConversationId(), RoleChat.user, request.getContent());
            addMessage(request.getConversationId(), RoleChat.assistant, finalMessageNode.asText());
            return finalMessageNode.asText();
        } else {
            return "[Không có nội dung trả về từ assistant]";
        }
    }

    private String callOpenAI(List<Map<String, Object>> messages, List<Map<String, Object>> functions) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("functions", functions);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 800);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", azureApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = String.format(
                "%s/openai/deployments/%s/chat/completions?api-version=2024-02-15-preview",
                azureEndpoint, deploymentName
        );

        log.info("Calling Azure OpenAI API - URL: {}", url);
        log.info("Request Headers: {}", headers);
        log.info("Request Body: {}", requestBody);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Azure OpenAI API Response: {}", response.getBody());
                return response.getBody();
            } else {
                log.error("Error calling OpenAI API. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Error calling OpenAI API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception calling OpenAI API", e);
            throw new RuntimeException("Error calling OpenAI API: " + e.getMessage());
        }
    }

    private String processToolCall(String functionName, JsonNode argumentsNode) throws Exception {
        if (argumentsNode.isTextual()) {
            argumentsNode = mapper.readTree(argumentsNode.asText());
        }

        switch (functionName) {
            case "SearchRooms":
                RoomSearchDto args = mapper.treeToValue(argumentsNode, RoomSearchDto.class);
                Double minPrice = args.getMinPrice();
                Double maxPrice = args.getMaxPrice();
                Double minArea = args.getMinArea();
                Double maxArea = args.getMaxArea();
                String city = args.getCity();
                String district = args.getDistrict();
                String ward = args.getWard();
                int pageNumber = args.getPageNumber() != null ? args.getPageNumber() : 1;
                int pageSize = args.getPageSize() != null ? args.getPageSize() : 10;

                log.info("[Chatbot] Calling searchRoomsFlexible with: minPrice={}, maxPrice={}, minArea={}, maxArea={}, city={}, district={}, ward={}, pageNumber={}, pageSize={}",
                        minPrice, maxPrice, minArea, maxArea, city, district, ward, pageNumber, pageSize
                );
                
                var result = postService.searchRoomsFlexible(
                        minPrice,
                        maxPrice,
                        minArea,
                        maxArea,
                        city,
                        district,
                        ward,
                        PageRequest.of(pageNumber - 1, pageSize)
                );
                
                // Convert the response to JSON string
                String jsonResponse = mapper.writeValueAsString(result);
                log.info("[Chatbot] Response from searchRoomsFlexible: {}", jsonResponse);

                return jsonResponse;
            default:
                throw new IllegalArgumentException("Function not supported: " + functionName);
        }
    }

    private void addMessage(String conversationId, RoleChat role, String content) {
        chatBotService.createMessage(new MessageCreateRequestDto(conversationId, role, content));
    }

    private String systemPrompt() {
        var systemTime = LocalDateTime.now();
        return "Bạn là một trợ lý thông minh chuyên hỗ trợ tìm kiếm nhà trọ. Bạn có thể trả lời câu hỏi của người dùng bằng cách sử dụng các công cụ tìm kiếm nhà trọ. Hãy lịch sự, súc tích và chính xác.\n" +
                "\n" +
                "<general_guidelines>\n" +
                "- Luôn trả lời bằng tiếng Việt\n" +
                "- Trả về kết quả dưới định dạng HTML có styling cơ bản (in đậm, danh sách, tiêu đề) phù hợp hiển thị web\n" +
                "- Sử dụng thẻ <ul><li> cho danh sách, <strong> cho text quan trọng, <h3> cho tiêu đề\n" +
                "- Phân tích ý định của người dùng và quyết định có cần gọi API tìm kiếm hay không\n" +
                "- Nếu API có sẵn, map các giá trị người dùng cung cấp vào parameters API\n" +
                "- Nếu API thất bại, thông báo người dùng thử lại sau\n" +
                "- Trả lời một cách rõ ràng và hữu ích\n" +
                "</general_guidelines>\n" +
                "\n" +
                "<room_search_guidelines>\n" +
                "- Khi người dùng hỏi về nhà trọ mà không chỉ định bộ lọc, sử dụng giá trị mặc định cho các tham số không được chỉ định\n" +
                "- Khi hiển thị thông tin nhà trọ, luôn bao gồm các trường: Tiêu đề, Giá thuê, Diện tích, Địa chỉ, Trạng thái nội thất\n" +
                "- Định dạng giá tiền theo VND với dấu phẩy phân cách\n" +
                "- Hiển thị kết quả theo định dạng:\n" +
                "  🏠 **[Tiêu đề]**\n" +
                "  - **Giá thuê**: [Giá] VND/tháng\n" +
                "  - **Diện tích**: [Diện tích] m²\n" +
                "  - **Địa chỉ**: [Địa chỉ]\n" +
                "  - **Nội thất**: [Trạng thái nội thất]\n" +
                "</room_search_guidelines>\n" +
                "<url_guidelines>\n" +
                "  - Khi trả về kết quả tìm kiếm, luôn bao gồm URL đến trang FE với các tham số tìm kiếm\n" +
                "  - Định dạng URL: https://your-frontend-url/search?minPrice=...&maxPrice=...&minArea=...&maxArea=...&furnitureStatus=...&city=...&district=...&ward=...&keyword=...\n" +
                "</url_guidelines>\n" +
                "\nThời gian hiện tại: " + systemTime + "\n";
    }

    private List<Map<String, Object>> getDefinedFunctions() {
        List<Map<String, Object>> functions = new ArrayList<>();

        Map<String, Object> searchRoomsTool = Map.of(
                "name", "SearchRooms",
                "description", "Tìm kiếm nhà trọ dựa trên các tiêu chí như giá thuê, diện tích, địa chỉ và trạng thái nội thất",
                "parameters", Map.of(
                        "type", "object",
                        "properties", getSearchRoomsProperties(),
                        "required", List.of()
                )
        );

        functions.add(searchRoomsTool);
        return functions;
    }

    private Map<String, Object> getSearchRoomsProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("minPrice", Map.of(
                "type", "number",
                "description", "Giá thuê tối thiểu (VND/tháng). Tùy chọn."
        ));
        properties.put("maxPrice", Map.of(
                "type", "number",
                "description", "Giá thuê tối đa (VND/tháng). Tùy chọn."
        ));
        properties.put("minArea", Map.of(
                "type", "number",
                "description", "Diện tích tối thiểu (m²). Tùy chọn."
        ));
        properties.put("maxArea", Map.of(
                "type", "number",
                "description", "Diện tích tối đa (m²). Tùy chọn."
        ));
        properties.put("furnitureStatus", Map.of(
                "type", "string",
                "description", "Trạng thái nội thất (FULLY_FURNISHED, PARTIALLY_FURNISHED, UNFURNISHED). Tùy chọn."
        ));
        properties.put("city", Map.of(
                "type", "string",
                "description", "Thành phố. Tùy chọn."
        ));
        properties.put("district", Map.of(
                "type", "string",
                "description", "Quận/Huyện. Tùy chọn."
        ));
        properties.put("ward", Map.of(
                "type", "string",
                "description", "Phường/Xã. Tùy chọn."
        ));
        properties.put("keyword", Map.of(
                "type", "string",
                "description", "Từ khóa tìm kiếm. Tùy chọn."
        ));
        properties.put("pageNumber", Map.of(
                "type", "integer",
                "description", "Số trang (bắt đầu từ 1). Mặc định là 1.",
                "default", 1
        ));
        properties.put("pageSize", Map.of(
                "type", "integer",
                "description", "Số kết quả mỗi trang. Mặc định là 10.",
                "default", 10
        ));
        return properties;
    }
}
