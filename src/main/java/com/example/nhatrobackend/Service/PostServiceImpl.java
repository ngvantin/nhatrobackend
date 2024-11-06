package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.DTO.RoomRequestDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.ModelMapperUtil.PostConverter;
import com.example.nhatrobackend.Responsitory.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PostMapper postMapper; // Thay PostConverter bằng PostMapper

    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        // Lấy tất cả các Post từ cơ sở dữ liệu dưới dạng Page
        Page<Post> postPage = postRepository.findAll(pageable);

        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }



    @Override
    public Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable) {
        return null;
    }

    // Xem chi tiết bài viết
    @Override
    public PostDetailResponseDTO getPostById(String postUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // Chuyển đổi bài viết sang PostDetailResponseDTO
            PostDetailResponseDTO postDetailResponseDTO = postMapper.convertToDetailDTO(post);

            return postDetailResponseDTO;
            // Tiếp tục xử lý post
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postUuid);
        }

    }
//
//    @Override
//    public Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable) {
//        // Lấy danh sách bài viết đã lọc với phân trang
//        Page<Post> postPage = postRepository.findPostsByRoomCriteria(
//                roomRequestDTO.getMinPrice(),
//                roomRequestDTO.getMaxPrice(),
//                roomRequestDTO.getMinArea(),
//                roomRequestDTO.getMaxArea(),
//                roomRequestDTO.getFurnitureStatus(),
//                roomRequestDTO.getCity(),
//                roomRequestDTO.getDistrict(),
//                roomRequestDTO.getWard(),
//                pageable);
//
//        // Kiểm tra nếu không có bài viết nào được tìm thấy
//        if (!postPage.hasContent()) {
//            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
//        }
//
//        // Chuyển đổi danh sách Post thành danh sách PostResponseDTO
//        List<PostResponseDTO> postResponseDTOs = postPage.getContent().stream()
//                .map(postConverter::convertToDTO)
//                .collect(Collectors.toList());
//
//        // Tạo Page<PostResponseDTO> và trả về
//        return new PageImpl<>(postResponseDTOs, pageable, postPage.getTotalElements());
//    }


    // Lấy tất cả các bài viết
//    @Override
//    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
//        // Lấy tất cả các bài viết với phân trang
//        Page<Post> postPage = postRepository.findAll(pageable);
//
////        // Chuyển đổi danh sách Post thành danh sách PostResponseDTO
////        Page<PostResponseDTO> postResponseDTOs = postPage
////                .map(postConverter::convertToDTO);// Sử dụng PostConverter để chuyển đổi
//
//        // Tạo Page<PostResponseDTO> và trả về
//        return postPage
//                .map(postConverter::convertToDTO);// Sử dụng PostConverter để chuyển đổi;
//    }
}
