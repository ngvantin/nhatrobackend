package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.Post;
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
    private final PostConverter postConverter; // Inject PostConverter

    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        // Lấy tất cả các bài viết với phân trang
        Page<Post> postPage = postRepository.findAll(pageable);

        // Chuyển đổi danh sách Post thành danh sách PostResponseDTO
        List<PostResponseDTO> postResponseDTOs = postPage.getContent().stream()
                .map(postConverter::convertToDTO) // Sử dụng PostConverter để chuyển đổi
                .collect(Collectors.toList());

        // Tạo Page<PostResponseDTO> và trả về
        return new PageImpl<>(postResponseDTOs, pageable, postPage.getTotalElements());
    }

    @Override
    public PostDetailResponseDTO getPostById(Integer postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // Chuyển đổi bài viết sang PostDetailResponseDTO
            PostDetailResponseDTO postDetailResponseDTO = postConverter.convertToDetailDTO(post);

            return postDetailResponseDTO;
            // Tiếp tục xử lý post
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postId);
        }

    }

}
