package com.example.streammateseriessvc.app.feather.repositories;

import com.example.streammateseriessvc.app.feather.models.Series;
import com.example.streammateseriessvc.app.feather.models.SeriesComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriesRepository extends JpaRepository<Series, UUID> {
    Optional<Series> findByTitle(String movieName);

    Optional<Series> findByTitleAndPosterImgURL(String cinemaRecTitle, String cinemaRecPosterImage);

    @Query(value = "SELECT id, title, poster_img_url, release_date FROM series", nativeQuery = true)
    List<Object[]> getThirthySeriesRawData(Pageable pageable);


    @Query(value = "SELECT * FROM series WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))" +
            " OR LOWER(search_tag) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Series> findByTitleOrSearchTagContainingIgnoreCase(@Param("title") String title);

    @Query(value = "SELECT count(*) FROM series WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))" +
            " OR LOWER(search_tag) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    long findSeriesCountByTitleOrSearchTagContainingIgnoreCase(@Param("title") String title);

    @Query(value = "SELECT COUNT(*) FROM series WHERE LOWER(genres) LIKE LOWER(CONCAT('%', :genres, '%'))", nativeQuery = true)
    long findSeriesCountByGenre(@Param("genres") String genres);

    @Query(value = "SELECT id, title, poster_img_url, release_date FROM series WHERE LOWER(genres) LIKE LOWER(CONCAT('%', :genre, '%')) ORDER BY created_at DESC LIMIT :size OFFSET :offset", nativeQuery = true)
    List<Object[]> findByGenreNextTwentySeries(@Param("genre") String genre, @Param("size") int size, @Param("offset") int offset);


    @Query(value =
            "SELECT id, comment_text, author_username, author_full_name, author_img_url, " +
                    "author_id, rating, created_at " +
                    "FROM series_comments " +
                    "WHERE series_id = :currentCinemaRecordId " +
                    "ORDER BY created_at DESC " +
                    "LIMIT 10 OFFSET :offset",
            nativeQuery = true)
    List<Object[]> getNext10Comments(@Param("offset") int offset,
                                     @Param("currentCinemaRecordId") UUID currentCinemaRecordId);
}
