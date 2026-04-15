package com.vshevchenko.resaleplatform.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность объявления.
 * Содержит информацию об объявлении: название, описание, цену, изображение и автора.
 */
@Entity
@Table(name = "ads")
@Data
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;
}
