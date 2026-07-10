package service;

import dao.TrainerDao;
import dao.TrainerProfileDao;
import model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerProfileServiceTest {

    @Mock private TrainerDao trainerDao;
    @Mock private TrainerProfileDao profileDao;
    @InjectMocks private TrainerProfileService service;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);

        setField("trainerDao", trainerDao);
        setField("profileDao", profileDao);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = TrainerProfileService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    void testGetDescriptionForTrainer() {
        assertNull(service.getDescriptionForTrainer(0));

        when(profileDao.getDescription(1)).thenReturn("bio");
        assertEquals("bio", service.getDescriptionForTrainer(1));
    }

    @Test
    void testUpsertMyDescriptionFailures() {
        when(trainerDao.getTrainerByUserId(1)).thenReturn(null);
        assertEquals("Only coaches can edit their description.", service.upsertMyDescription(1, "bio"));

        var trainer = mock(Trainer.class);
        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);
        assertEquals("Description is too long (max 1500 characters).", service.upsertMyDescription(1, "a".repeat(1501)));

        when(trainer.getId()).thenReturn(10);
        when(profileDao.upsertDescription(10, "bio")).thenReturn(false);
        assertEquals("Description could not be saved.", service.upsertMyDescription(1, "bio"));
    }

    @Test
    void testUpsertMyDescriptionSuccess() {
        var trainer = mock(Trainer.class);
        when(trainer.getId()).thenReturn(10);
        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);
        when(profileDao.upsertDescription(10, "bio")).thenReturn(true);

        assertNull(service.upsertMyDescription(1, "bio"));
    }
}