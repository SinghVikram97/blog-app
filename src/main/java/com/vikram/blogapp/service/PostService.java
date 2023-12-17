package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Post;

import java.util.List;

public interface PostService {
    // Create Post
    PostDTO createPost(PostDTO postDTO);

    // Update Post
    PostDTO updatePost(PostDTO postDTO, long postId);

    // Delete Post
    PostDTO deletePost(long postId);

    // Get all Posts
    List<PostDTO> getAllPosts(int pageNumber, int pageSize);

    // Get single post
    PostDTO getPostById(long postId);

    // Search Posts
    List<PostDTO> searchPosts(String keyword);
}
