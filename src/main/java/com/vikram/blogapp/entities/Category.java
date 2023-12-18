package com.vikram.blogapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="title")
    private String categoryTitle;

    @Column(name="description")
    private String categoryDescription;

    @OneToMany(
            mappedBy = "category" // Don't remove category if post deleted
    )
    private List<Post> posts = new ArrayList<>();

    public void addPost(Post newPost) {
        posts.add(newPost);
        newPost.setCategory(this);
    }

    public void removePost(Post post){
        posts.remove(post);
        post.setCategory(null);
    }
}
