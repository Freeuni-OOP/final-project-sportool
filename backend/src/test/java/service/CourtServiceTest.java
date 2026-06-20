package service;

import dao.CourtDaoSql;
import model.Court;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CourtServiceTest {
    @InjectMocks
    private CourtService service;

    @Mock
    private CourtDaoSql courtDao;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);

        Field field = CourtService.class.getDeclaredField("courtDao");
        field.setAccessible(true);
        field.set(service, courtDao);
    }

    @Test
    public void testGetCourtsWhenTypeIsAll() {

        List<Court> courts = new ArrayList<>();
        courts.add(new Court(1, "Maracana", "FOOTBALL", "Tbilisi", 50.0));
        courts.add(new Court(2, "Wimbledon", "TENNIS", "Batumi", 40.0));


        when(courtDao.getAllCourts()).thenReturn(courts);

        List<Court> result = service.getCourts("ALL");

        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    public void testGetCourtsWhenTypeIsNull() {
        List<Court> courts = new ArrayList<>();
        courts.add(new Court(1, "Maracana", "FOOTBALL", "Tbilisi", 50.0));

        when(courtDao.getAllCourts()).thenReturn(courts);

        List<Court> result = service.getCourts(null);

        assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    public void testGetCourtsWithSpecificType() {
        List<Court> courts = new ArrayList<>();
        courts.add(new Court(3, "Padel Central", "PADEL", "Tbilisi", 60.0));

        when(courtDao.getCourtsByType("PADEL")).thenReturn(courts);

        List<Court> result = service.getCourts("PADEL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Padel Central", result.get(0).getName());
    }
}
