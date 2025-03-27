package com.example.streammateseriessvc.app.feather.repositories;

import com.example.streammateseriessvc.app.feather.models.SeriesComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeriesCommentsRepository extends JpaRepository<SeriesComment, UUID> {
}
