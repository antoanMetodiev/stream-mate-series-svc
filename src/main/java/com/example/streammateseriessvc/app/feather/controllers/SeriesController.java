package com.example.streammateseriessvc.app.feather.controllers;

import com.example.streammateseriessvc.app.commonData.models.dtos.CinemaRecordResponse;
import com.example.streammateseriessvc.app.feather.models.Series;
import com.example.streammateseriessvc.app.feather.models.SeriesComment;
import com.example.streammateseriessvc.app.feather.services.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class SeriesController {
    private final SeriesService seriesService;

    @Autowired
    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @GetMapping("/get-searched-series-count")
    public long getSearchedSeriesCount(@RequestParam String title) {
        return this.seriesService.getSearchedSeriesCount(title);
    }

    @GetMapping("/get-series-by-title")
    public List<Series> getSeriesByTitle(@RequestParam String title) {
        List<Series> moviesByTitle = this.seriesService.getSeriesByTitle(title);
        return moviesByTitle;
    }

    @GetMapping("/get-series-count-by-genre")
    public long findSeriesCountByGenre(@RequestParam String genres) {
        String receivedGenre = genres;
        return this.seriesService.findSeriesCountByGenre(genres);
    }

    @GetMapping("/get-next-twenty-series-by-genre")
    public List<CinemaRecordResponse> getNextTwentySeriesByGenre(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size,
                                                                 @RequestParam String receivedGenre) {

        Pageable pageable = PageRequest.of(page, size);  // Стандартен Pageable
        return this.seriesService.getNextTwentySeriesByGenre(receivedGenre, pageable);  // Предаваме жанра и Pageable на сървиса
    }

    @GetMapping("/get-next-thirty-series")
    public List<CinemaRecordResponse>  getEveryThirtySeries(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Requested page: " + page + ", size: " + size);
        return seriesService.getEveryThirtySeries(pageable);
    }

    @GetMapping("/get-series-details")
    public Series getConcreteSeriesDetails(@RequestParam String id) {
        Series series = this.seriesService.getConcreteSeriesDetails(UUID.fromString(id));
        return series;
    }

    @GetMapping("/get-all-series-count")
    public long getAllSeriesCount()  {
        return this.seriesService.getAllSeriesCount();
    }

    @PostMapping("/search-series")
    public void searchSeries(@RequestParam String title) throws IOException, InterruptedException {
        this.seriesService.searchForSeries(title);
    }

    @PostMapping("/post-series-comment")
    public void postComment(@RequestParam String authorUsername,
                            @RequestParam String authorFullName,
                            @RequestParam String authorImgURL,
                            @RequestParam String commentText,
                            @RequestParam double rating,
                            @RequestParam String createdAt,
                            @RequestParam String authorId,
                            @RequestParam String movieId) {

        this.seriesService.postComment(authorUsername, authorFullName, authorImgURL,
                commentText, rating, createdAt, authorId, movieId);
    }

    @DeleteMapping("/delete-series-comment")
    public void deleteSeriesComment(@RequestParam String commentId,
                                    @RequestParam String movieId) {

        this.seriesService.deleteSeriesComment(commentId, movieId);
    }

    @GetMapping("/get-next-10-series-comments")
    public List<SeriesComment> getNext10SeriesComments(@RequestParam int order,
                                                       @RequestParam String currentCinemaRecordId) {

        return this.seriesService.getNext10Comments(order, UUID.fromString(currentCinemaRecordId));
    }
}
