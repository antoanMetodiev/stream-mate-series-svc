package com.example.streammateseriessvc.app.commonData.models.entities;

import com.example.streammateseriessvc.app.commonData.models.enums.ImageType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "movies_images")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class MovieImage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "image_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column(name = "image_url", nullable = false)
    @Size(min = 5)
    private String imageURL;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonBackReference
    private Movie movie;  // Или Series, в зависимост от контекста
}
