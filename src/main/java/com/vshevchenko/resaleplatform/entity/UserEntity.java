package com.vshevchenko.resaleplatform.entity;

import lombok.Data;
import com.vshevchenko.resaleplatform.dto.Role;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность пользователя.
 * Хранит информацию о пользователе: email, пароль, имя, фамилию, телефон, роль и аватар.
 */
@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String image;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdEntity> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;
}
