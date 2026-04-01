package com.oems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    private String name;
    private String description;

    @DBRef
    private User teacher;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @DBRef
    private Set<User> students = new HashSet<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
