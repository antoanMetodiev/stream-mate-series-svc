package com.example.streammateseriessvc;

import com.example.streammateseriessvc.app.commonData.models.dtos.CinemaRecordResponse;
import com.example.streammateseriessvc.app.commonData.models.enums.ImageType;
import com.example.streammateseriessvc.app.commonData.repositories.ActorRepository;
import com.example.streammateseriessvc.app.feather.models.Series;
import com.example.streammateseriessvc.app.feather.models.SeriesComment;
import com.example.streammateseriessvc.app.feather.models.SeriesImage;
import com.example.streammateseriessvc.app.feather.repositories.SeriesCommentsRepository;
import com.example.streammateseriessvc.app.feather.repositories.SeriesRepository;
import com.example.streammateseriessvc.app.feather.services.SeriesService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private SeriesCommentsRepository seriesCommentRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private SeriesService seriesService;

    @Test
    void testPostComment() {
        // Arrange
        String authorUsername = "testUser";
        String authorFullName = "Test User";
        String authorImgURL = "http://example.com/image.jpg";
        String commentText = "Great series!";
        double rating = 4.5;
        String createdAt = "2025-03-25";
        String authorId = UUID.randomUUID().toString();
        String seriesId = UUID.randomUUID().toString();

        UUID seriesUUID = UUID.fromString(seriesId);
        Series series = new Series();
        series.setSeriesComments(new ArrayList<>());

        when(seriesRepository.findById(seriesUUID)).thenReturn(Optional.of(series));

        // Act
        seriesService.postComment(authorUsername, authorFullName, authorImgURL, commentText, rating, createdAt, authorId, seriesId);

        // Assert
        assertEquals(1, series.getSeriesComments().size());
        verify(seriesRepository, times(1)).findById(seriesUUID);
        verify(seriesRepository, times(1)).save(series);
    }

    @Test
    public void testUrlGeneration() {
        String seriesId = "12345";
        String searchQuery = "https://api.themoviedb.org/3/tv/" + seriesId + "?api_key=YOUR_API_KEY";

        assertEquals("https://api.themoviedb.org/3/tv/12345?api_key=YOUR_API_KEY", searchQuery);
    }

    @Test
    void testExtractDetailsImages() {
        // Arrange
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < 3; i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("file_path", "http://example.com/image" + i + ".jpg");
            jsonArray.add(jsonObject);
        }

        int limit = 2;

        // Act
        CompletableFuture<List<SeriesImage>> futureResult = seriesService.extractDetailsImages(jsonArray, ImageType.BACKDROP, limit);
        List<SeriesImage> result = futureResult.join();

        // Assert
        assertNotNull(result);
        assertEquals("http://example.com/image0.jpg", result.get(0).getImageURL());
        assertEquals("http://example.com/image1.jpg", result.get(1).getImageURL());
        assertEquals(ImageType.BACKDROP, result.get(0).getImageType());
        assertEquals(ImageType.BACKDROP, result.get(1).getImageType());
    }

    @Test
    void testGetNext10Comments() {
        // Arrange
        int order = 2;
        UUID seriesId = UUID.randomUUID();
        List<SeriesComment> mockComments = List.of(new SeriesComment(), new SeriesComment());

        when(seriesRepository.getNext10Comments(10, seriesId)).thenReturn(mockComments);

        // Act
        List<SeriesComment> result = seriesService.getNext10Comments(order, seriesId);

        // Assert
        assertEquals(mockComments.size(), result.size());
        verify(seriesRepository, times(1)).getNext10Comments(10, seriesId);
    }

    @Test
    void testDeleteSeriesComment() {
        // Arrange
        UUID seriesId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        Series series = new Series();
        SeriesComment comment = new SeriesComment();
        comment.setId(commentId);
        series.setSeriesComments(new ArrayList<>(List.of(comment)));

        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));

        // Act
        seriesService.deleteSeriesComment(commentId.toString(), seriesId.toString());

        // Assert
        assertTrue(series.getSeriesComments().isEmpty());
        verify(seriesCommentRepository, times(1)).delete(comment);
        verify(seriesRepository, times(1)).save(series);
    }

    @Test
    void testGetNextTwentySeriesByGenre() {
        // Arrange
        String genre = "Drama";
        UUID seriesId = UUID.randomUUID();
        String title = "Breaking Bad";
        String posterUrl = "http://example.com/breakingbad.jpg";
        String releaseDate = "2008-01-20";
        List<Object[]> mockRawData = new ArrayList<>();
        mockRawData.add(new Object[]{seriesId, title, posterUrl, releaseDate});

        Pageable pageable = PageRequest.of(0, 20);
        when(seriesRepository.findByGenreNextTwentySeries(genre, 20, 0)).thenReturn(mockRawData);

        // Act
        List<CinemaRecordResponse> result = seriesService.getNextTwentySeriesByGenre(genre, pageable);

        // Assert
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(posterUrl, result.get(0).getPosterImgURL());
        assertEquals(releaseDate, result.get(0).getReleaseDate());
        verify(seriesRepository, times(1)).findByGenreNextTwentySeries(genre, 20, 0);
    }

    @Test
    void testGetSearchedSeriesCount() {
        // Arrange
        String title = "Breaking Bad";
        long expectedCount = 10L;
        when(seriesRepository.findSeriesCountByTitleOrSearchTagContainingIgnoreCase(title)).thenReturn(expectedCount);

        // Act
        long result = seriesService.getSearchedSeriesCount(title);

        // Assert
        assertEquals(expectedCount, result);
        verify(seriesRepository, times(1)).findSeriesCountByTitleOrSearchTagContainingIgnoreCase(title);
    }

    @Test
    void testGetSeriesByTitle() {
        String title = "Breaking Bad";
        List<Series> mockSeries = List.of(new Series(), new Series());
        when(seriesRepository.findByTitleOrSearchTagContainingIgnoreCase(title)).thenReturn(mockSeries);

        List<Series> result = seriesService.getSeriesByTitle(title);

        assertEquals(mockSeries.size(), result.size());
        verify(seriesRepository, times(1)).findByTitleOrSearchTagContainingIgnoreCase(title);
    }

    @Test
    void testFindSeriesCountByGenre() {
        String genre = "Drama";
        long expectedCount = 50L;
        when(seriesRepository.findSeriesCountByGenre(genre)).thenReturn(expectedCount);

        long result = seriesService.findSeriesCountByGenre(genre);
        assertEquals(expectedCount, result);
        verify(seriesRepository, times(1)).findSeriesCountByGenre(genre);
    }

    @Test
    void testGetAllSeriesCount() {
        long expectedCount = 100L;
        when(seriesRepository.count()).thenReturn(expectedCount);

        long result = seriesService.getAllSeriesCount();

        assertEquals(expectedCount, result);
        verify(seriesRepository, times(1)).count();
    }

    @Test
    void testGetConcreteSeriesDetails_Found() {
        UUID seriesId = UUID.randomUUID();
        Series mockSeries = new Series();
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(mockSeries));

        Series result = seriesService.getConcreteSeriesDetails(seriesId);

        assertEquals(mockSeries, result);
        verify(seriesRepository, times(1)).findById(seriesId);
    }

    @Test
    void testGetConcreteSeriesDetails_NotFound() {
        UUID seriesId = UUID.randomUUID();
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> seriesService.getConcreteSeriesDetails(seriesId));
        verify(seriesRepository, times(1)).findById(seriesId);
    }

    @Test
    void testGetEveryThirtySeries() {
        UUID seriesId = UUID.randomUUID();
        String title = "Breaking Bad";
        String posterUrl = "http://example.com/poster.jpg";
        String releaseDate = "2008-01-20";

        List<Object[]> mockRawData = new ArrayList<>();
        mockRawData.add(new Object[]{seriesId, title, posterUrl, releaseDate});

        Pageable pageable = PageRequest.of(0, 30);
        when(seriesRepository.getThirthySeriesRawData(pageable)).thenReturn(mockRawData);
        List<CinemaRecordResponse> result = seriesService.getEveryThirtySeries(pageable);

        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(posterUrl, result.get(0).getPosterImgURL());
        assertEquals(releaseDate, result.get(0).getReleaseDate());

        verify(seriesRepository, times(1)).getThirthySeriesRawData(pageable);
    }
}
