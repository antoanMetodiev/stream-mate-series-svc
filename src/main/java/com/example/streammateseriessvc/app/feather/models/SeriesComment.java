package com.example.streammateseriessvc.app.feather.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.util.UUID;

@Entity
@Table(name = "series_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SeriesComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "comment_text")
    private String commentText;

    @Column(nullable = false, name = "author_username")
    private String authorUsername;

    @Column(nullable = false, name = "author_full_name")
    private String authorFullName;

    @Column(name = "author_img_url")
    private String authorImgURL;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    @Min(1)
    private double rating;

    @Column(nullable = false, name = "created_at")
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    @JsonBackReference
    private Series series;
}
