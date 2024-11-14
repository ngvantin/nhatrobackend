package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.RoomMapper;
import com.example.nhatrobackend.Mapper.UserMapper;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable) {
        // Lấy danh sách bài viết đã lọc với phân trang
        Page<Post> postPage = postRepository.findPostsByRoomCriteria(
                roomRequestDTO.getMinPrice(),
                roomRequestDTO.getMaxPrice(),
                roomRequestDTO.getMinArea(),
                roomRequestDTO.getMaxArea(),
                roomRequestDTO.getFurnitureStatus(),
                roomRequestDTO.getCity(),
                roomRequestDTO.getDistrict(),
                roomRequestDTO.getWard(),
                pageable);

        // Kiểm tra nếu không có bài viết nào được tìm thấy, ném ngoại lệ
        if (!postPage.hasContent()) {
            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
        }

        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

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

    @Override
    public PostDetailResponseDTO updatepost(String postUuid, PostRequestDTO postRequestDTO, String userUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            User user = userService.findByUserUuid(userUuid);
            if(!post.getUser().equals(user)){
                throw new IllegalArgumentException("Bạn không có quyền cho bài viết này");
            }
            Post updatePost = postMapper.toPostWithImages(postRequestDTO,user);
            updatePost.setPostId(post.getPostId());
            Post savePost = postRepository.save(updatePost);
            return postMapper.toPostDetailResponseDTO(savePost);
        }
        else{
            throw new EntityNotFoundException("Không tìm thấy bài viết.");
        }
    }

    @Override
    public void deletepost(String postUuid, String userUuid) {
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


}
