package com.spring.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

import static com.spring.blog.utils.Constants.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = ROLES_TABLE)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_generator")
    @SequenceGenerator(name = "role_id_generator", sequenceName = "role_id_sequence",
            initialValue = ROLE_ID_INITIAL_VALUE, allocationSize = ROLE_ID_INCREMENT_VALUE)
    private long id;

    @Column(nullable = false)
    private String name;

}
