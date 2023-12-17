package com.vikram.blogapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String imageName;

    private Date addedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post )) return false;
        return id == ((Post) o).getId();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
