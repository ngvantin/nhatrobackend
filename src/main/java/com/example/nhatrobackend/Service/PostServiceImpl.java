package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.*;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Mapper.PostImageMapper;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.RoomMapper;
import com.example.nhatrobackend.Mapper.UserMapper;
import com.example.nhatrobackend.Responsitory.FavoritePostRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final UserService userService;
    private final RoomService roomService;
    private final PostImageMapper postImageMapper;
    private final FavoritePostRepository favoritePostRepository;
    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        // Lấy tất cả các Post từ cơ sở dữ liệu dưới dạng Page
        Page<Post> postPage = postRepository.findAll(pageable);

        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }


    // Xem chi tiết bài viết
    @Override
    public PostDetailResponseDTO getPostById(String postUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);

        // Nếu tìm thấy Post, chuyển đổi sang PostDetailResponseDTO
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            return postMapper.toPostDetailResponseDTO(post);
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postUuid);
        }
    }

    @Override
    public Page<PostResponseDTO> filterPosts(
            Double minPrice,
            Double maxPrice,
            Double minArea,
            Double maxArea,
            FurnitureStatus furnitureStatus,
            String city,
            String district,
            String ward,
            Pageable pageable) {

        // Gọi repository với các tham số trực tiếp
        Page<Post> postPage = postRepository.findPostsByRoomCriteria(
                minPrice,
                maxPrice,
                minArea,
                maxArea,
                furnitureStatus,
                city,
                district,
                ward,
                pageable);
        // Kiểm tra nếu không có bài viết nào được tìm thấy, ném ngoại lệ
        if (!postPage.hasContent()) {
            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
        }

        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }


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
//        // Kiểm tra nếu không có bài viết nào được tìm thấy, ném ngoại lệ
//        if (!postPage.hasContent()) {
//            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
//        }
//
//        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
//        return postPage.map(postMapper::toPostResponseDTO);
//    }

    @Override
    public UserDetailDTO getUserByPostUuid(String postUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            return userMapper.toUserDetailDTO(post.getUser());
        }
        else{
            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
        }
    }

    @Override
    public PostDetailResponseDTO createPost(PostRequestDTO postRequestDTO, String userUuid) {
        User user = userService.findByUserUuid(userUuid);
        if(!userService.getApprovedUserByUuid(userUuid)){
            throw new IllegalArgumentException("Chưa đăng kí tài khoản Chủ cho thuê, không thể đăng bài.");
        }
        Room room = roomMapper.toRoom(postRequestDTO);
        Post post = postMapper.toPostWithImages(postRequestDTO,user);
        roomService.saveRoom(room);
        post.setRoom(room);
        post.setUser(user);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(LocalDateTime.now());
        Post savePost = postRepository.save(post);
        return postMapper.toPostDetailResponseDTO(savePost);
    }

//    @Override
//    public PostDetailResponseDTO updatePost(String postUuid, PostRequestDTO postRequestDTO, String userUuid) {
//        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
//        if(optionalPost.isPresent()){
//            Post post = optionalPost.get();
//            User user = userService.findByUserUuid(userUuid);
//            if(!post.getUser().equals(user)){
//                throw new IllegalArgumentException("Bạn không có quyền cho bài viết này");
//            }
//            Post updatePost = postMapper.toPostWithImages(postRequestDTO,user);
//            updatePost.setPostId(post.getPostId());
//            Post savePost = postRepository.save(updatePost);
//            return postMapper.toPostDetailResponseDTO(savePost);
//        }
//        else{
//            throw new EntityNotFoundException("Không tìm thấy bài viết.");
//        }
//    }

//    @Transactional(readOnly = true)
    @Override
    public PostRequestDTO getPostForEdit(String postUuid, String userUuid) {
        System.out.printf("hi");
        // Lấy bài viết dựa trên postUuid
        Post post = postRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết."));

        // Kiểm tra quyền của người dùng
        User user = userService.findByUserUuid(userUuid);
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("Bạn không có quyền truy cập bài viết này.");
        }

        // Chuyển đổi Post thành PostRequestDTO
        PostRequestDTO postRequestDTO = postMapper.toPostRequestDTO(post);

        // Thêm thông tin room từ RoomService
        RoomDTO roomDTO = roomMapper.toRoomDTO(post.getRoom());
        postMapper.updatePostRequestFromRoomDTO(roomDTO, postRequestDTO);
        postRequestDTO.setPostImages(postMapper.mapImagesToUrls(post.getPostImages()));
        return postRequestDTO;
    }


    @Override
    @Transactional
    public PostDetailResponseDTO updatePost(String postUuid, PostRequestDTO postRequestDTO, String userUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy bài viết.");
        }

        Post post = optionalPost.get();
        User user = userService.findByUserUuid(userUuid);
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật bài viết này.");
        }

        // **Cập nhật các trường của Post**
        postMapper.updatePostFromDTO(postRequestDTO, post); // Tận dụng mapper (cần bổ sung)

        // **Cập nhật Room**
        if (post.getRoom() != null) {
            roomMapper.updateRoomFromDTO(postRequestDTO, post.getRoom()); // Cập nhật thông tin Room
        }

        // **Cập nhật PostImage**
        if (postRequestDTO.getPostImages() != null) {
            List<PostImage> updatedImages = postImageMapper.toPostImage(postRequestDTO.getPostImages(), post);
            post.getPostImages().clear(); // Xóa ảnh cũ
            post.getPostImages().addAll(updatedImages); // Thêm ảnh mới
        }

        // **Lưu các thay đổi**
        Post updatedPost = postRepository.save(post);

        return postMapper.toPostDetailResponseDTO(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(String postUuid, String userUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            User user = userService.findByUserUuid(userUuid);
            if(!post.getUser().equals(user)){
                throw new IllegalArgumentException("Bạn không có quyền cho bài viết này");
            }
            postRepository.delete(post);
        }
        else{
            throw new EntityNotFoundException("Không tìm thấy bài viết.");
        }
    }

    @Override
    public Post getPostByUuid(String postUuid) {
        return postRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new RuntimeException("Post not found with UUID: " + postUuid));
    }

    @Override
    public Page<PostResponseDTO> getPostsByStatusAndUser(PostStatus status, String userUuid, Pageable pageable) {
        // Lấy userId từ userUuid
        User user = userService.getUserByUuid(userUuid);
        Integer userId = user.getUserId(); // Lấy userId từ User

        // Lọc các bài post có trạng thái APPROVED và userId tương ứng
        Page<Post> postPage = postRepository.findByStatusAndUser_UserId(status, userId, pageable);

        // Chuyển đổi từ Post sang PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

    @Override
    public Page<PostResponseDTO> getFavoritePostsByUser(String userUuid, Pageable pageable) {
        // Lấy danh sách FavoritePost của User
        Page<FavoritePost> favoritePostsPage = favoritePostRepository.findByUser_UserUuid(userUuid, pageable);

        // Lấy danh sách Post từ FavoritePost và chuyển đổi sang PostResponseDTO
        return favoritePostsPage.map(favoritePost -> postMapper.toPostResponseDTO(favoritePost.getPost()));
    }

    public PostDetailResponseDTO approvePost(String postUuid) {
        // Tìm bài viết theo postUuid
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);

        // Kiểm tra bài viết có tồn tại hay không
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Cập nhật trạng thái bài viết
            post.setStatus(PostStatus.APPROVED);
            post.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian sửa

            // Lưu lại bài viết đã cập nhật
            postRepository.save(post);

            // Trả về response DTO
            return postMapper.toPostDetailResponseDTO(post);  // Chuyển đổi thành DTO nếu cần
        } else {
            throw new EntityNotFoundException("Post not found with UUID: " + postUuid);
        }
    }

    // Phương thức từ chối bài viết
    public PostDetailResponseDTO rejectPost(String postUuid) {
        // Tìm bài viết theo postUuid
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);

        // Kiểm tra bài viết có tồn tại hay không
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Cập nhật trạng thái bài viết thành REJECTED
            post.setStatus(PostStatus.REJECTED);
            post.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian sửa

            // Lưu lại bài viết đã cập nhật
            postRepository.save(post);

            // Trả về response DTO
            return postMapper.toPostDetailResponseDTO(post);  // Chuyển đổi thành DTO nếu cần
        } else {
            throw new EntityNotFoundException("Post not found with UUID: " + postUuid);
        }
    }

    @Override
    public Page<PostResponseDTO> searchPostsByKeyword(String keyword, Pageable pageable) {

        // Gọi repository với từ khóa tìm kiếm
        Page<Post> postPage = postRepository.findPostsByKeyword(keyword, pageable);

        // Nếu không có bài viết nào tìm thấy, ném ngoại lệ
        if (!postPage.hasContent()) {
            throw new EntityNotFoundException("Không tìm thấy bài viết nào với từ khóa: " + keyword);
        }

        // Chuyển đổi sang PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

}
