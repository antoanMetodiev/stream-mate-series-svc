package com.example.streammateseriessvc;

import com.example.streammateseriessvc.app.commonData.models.dtos.CinemaRecordResponse;
import com.example.streammateseriessvc.app.feather.controllers.SeriesController;
import com.example.streammateseriessvc.app.feather.models.Series;
import com.example.streammateseriessvc.app.feather.repositories.SeriesRepository;
import com.example.streammateseriessvc.app.feather.services.SeriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeriesController.class)
public class SeriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeriesRepository seriesRepository;

    @MockBean
    private SeriesService seriesService;

    @Autowired
    private SeriesController seriesController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(seriesController).build();
    }

    @Test
    public void testGetAllSeriesCount() throws Exception {
        long expectedCount = 100L;

        when(seriesService.getAllSeriesCount()).thenReturn(expectedCount);
        mockMvc.perform(get("/get-all-series-count"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));
    }

    @Test
    public void testGetEveryThirtySeries() throws Exception {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        UUID seriesId1 = UUID.randomUUID();
        UUID seriesId2 = UUID.randomUUID();
        String title1 = "Series 1";
        String title2 = "Series 2";
        String posterUrl1 = "http://example.com/poster1.jpg";
        String posterUrl2 = "http://example.com/poster2.jpg";
        String releaseDate1 = "2021-01-01";
        String releaseDate2 = "2021-02-01";

        List<CinemaRecordResponse> mockResponse = Arrays.asList(
                new CinemaRecordResponse(seriesId1, title1, posterUrl1, releaseDate1),
                new CinemaRecordResponse(seriesId2, title2, posterUrl2, releaseDate2)
        );

        when(seriesService.getEveryThirtySeries(pageable)).thenReturn(mockResponse);

        mockMvc.perform(get("/get-next-thirty-series")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(seriesId1.toString()))
                .andExpect(jsonPath("$[0].title").value(title1))
                .andExpect(jsonPath("$[0].posterImgURL").value(posterUrl1))
                .andExpect(jsonPath("$[0].releaseDate").value(releaseDate1))
                .andExpect(jsonPath("$[1].id").value(seriesId2.toString()))
                .andExpect(jsonPath("$[1].title").value(title2))
                .andExpect(jsonPath("$[1].posterImgURL").value(posterUrl2))
                .andExpect(jsonPath("$[1].releaseDate").value(releaseDate2));
    }

    @Test
    public void testGetConcreteSeriesDetails() throws Exception {
        String seriesId = "123e4567-e89b-12d3-a456-426614174000";
        Series mockSeries = new Series();
        mockSeries.setId(UUID.fromString(seriesId));
        mockSeries.setTitle("Breaking Bad");
        when(seriesService.getConcreteSeriesDetails(UUID.fromString(seriesId))).thenReturn(mockSeries);

        mockMvc.perform(get("/get-series-details")
                        .param("id", seriesId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Breaking Bad"));
    }

    @Test
    public void testGetNextTwentySeriesByGenre() throws Exception {
        String genre = "Drama";
        int page = 0;
        int size = 20;

        CinemaRecordResponse series1 = new CinemaRecordResponse();
        series1.setTitle("Drama Series 1");
        CinemaRecordResponse series2 = new CinemaRecordResponse();
        series2.setTitle("Drama Series 2");

        List<CinemaRecordResponse> mockSeries = Arrays.asList(series1, series2);
        when(seriesService.getNextTwentySeriesByGenre(genre, PageRequest.of(page, size))).thenReturn(mockSeries);

        mockMvc.perform(get("/get-next-twenty-series-by-genre")
                        .param("receivedGenre", genre)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Drama Series 1"))
                .andExpect(jsonPath("$[1].title").value("Drama Series 2"));
    }

    @Test
    public void testFindSeriesCountByGenre() throws Exception {
        String genre = "Drama";
        long expectedCount = 10L;

        when(seriesService.findSeriesCountByGenre(genre)).thenReturn(expectedCount);
        mockMvc.perform(get("/get-series-count-by-genre")
                        .param("genres", genre))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));
    }

    @Test
    public void testGetSeriesByTitle() throws Exception {
        String title = "Breaking Bad";
        Series series1 = new Series();
        series1.setTitle("Breaking Bad");
        Series series2 = new Series();
        series2.setTitle("Breaking Bad");

        List<Series> mockSeries = Arrays.asList(series1, series2);
        when(seriesService.getSeriesByTitle(title)).thenReturn(mockSeries);

        mockMvc.perform(get("/get-series-by-title")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Breaking Bad"))
                .andExpect(jsonPath("$[1].title").value("Breaking Bad"));
    }

    @Test
    public void testGetSearchedSeriesCount() throws Exception {
        String title = "Breaking Bad";
        long expectedCount = 5L;

        when(seriesService.getSearchedSeriesCount(title)).thenReturn(expectedCount);
        mockMvc.perform(get("/get-searched-series-count")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));
    }
}
