package com.spring.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import static com.spring.blog.utils.Constants.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = COMMENTS_TABLE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_generator")
    @SequenceGenerator(name = "comment_id_generator", sequenceName = "comment_id_sequence",
            initialValue = COMMENT_ID_INITIAL_VALUE, allocationSize = COMMENT_ID_INCREMENT_VALUE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}
