package com.vikram.blogapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    private String about;

    // One user can have multiple post
    // One to Many by default lazy fetch type
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Post> posts = new ArrayList<>();

    public void addPost(Post newPost) {
        posts.add(newPost);
        newPost.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post); // implement equals in Post
        post.setUser(null);
    }

}
