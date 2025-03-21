package com.example.streammateseriessvc.app.feather.repositories;

import com.example.streammateseriessvc.app.feather.models.SeriesImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeriesImageRepository extends JpaRepository<SeriesImage, UUID> {
}
