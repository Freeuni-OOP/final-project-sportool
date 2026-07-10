package service;

import dao.TrainerDao;
import dao.TrainerReviewDao;
import model.Trainer;
import model.TrainerReview;
import model.TrainerReviewView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerReviewServiceTest {

    @Mock private TrainerReviewDao reviewDao;
    @Mock private TrainerDao trainerDao;
    @InjectMocks private TrainerReviewService service;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);
        for (String f : new String[]{"reviewDao", "trainerDao"}) {
            Field field = TrainerReviewService.class.getDeclaredField(f);
            field.setAccessible(true);
            field.set(service, f.equals("reviewDao") ? reviewDao : trainerDao);
        }
    }

    @Test
    void testGetReviews() {
        assertEquals(List.of(), service.getReviews(0));
        List<TrainerReviewView> mockList = List.of(mock(TrainerReviewView.class));
        when(reviewDao.getReviewsForTrainer(1)).thenReturn(mockList);
        assertEquals(mockList, service.getReviews(1));
    }

    @Test
    void testCreateReviewFailures() {
        TrainerReview r = mock(TrainerReview.class);

        assertEquals("Authentication required.", service.createReview(r, 0));
        when(r.getTrainerId()).thenReturn(0);
        assertEquals("trainerId is required.", service.createReview(r, 1));

        when(r.getTrainerId()).thenReturn(5);
        when(r.getRating()).thenReturn(0);
        assertEquals("Rating must be between 1 and 5.", service.createReview(r, 1));

        when(r.getRating()).thenReturn(5);
        when(r.getComment()).thenReturn("a".repeat(2001));
        assertEquals("Review comment is too long (max 2000 characters).", service.createReview(r, 1));

        when(r.getComment()).thenReturn("good");
        when(reviewDao.trainerHasReviewFromPlayer(5, 1)).thenReturn(true);
        assertEquals("You have already reviewed this coach.", service.createReview(r, 1));

        when(reviewDao.trainerHasReviewFromPlayer(5, 1)).thenReturn(false);
        Trainer t = mock(Trainer.class);
        when(t.getUserId()).thenReturn(1);
        when(trainerDao.getTrainerById(5)).thenReturn(t);
        assertEquals("You cannot review your own coach profile.", service.createReview(r, 1));

        when(t.getUserId()).thenReturn(2);
        when(reviewDao.createReview(r)).thenReturn(0);
        assertEquals("Review could not be saved.", service.createReview(r, 1));
    }

    @Test
    void testCreateReviewSuccess() {
        TrainerReview r = mock(TrainerReview.class);
        when(r.getTrainerId()).thenReturn(5);
        when(r.getRating()).thenReturn(5);
        when(reviewDao.trainerHasReviewFromPlayer(5, 1)).thenReturn(false);
        when(trainerDao.getTrainerById(5)).thenReturn(null);
        when(reviewDao.createReview(r)).thenReturn(1);

        assertNull(service.createReview(r, 1));
    }
}