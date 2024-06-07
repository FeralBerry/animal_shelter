package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InfoServiceTest {
    InfoService infoService = Mockito.mock(InfoService.class);

    @Test
    void info() {
        String expected = "info";
        doReturn(expected)
                .when(infoService)
                .info();
        assertEquals(infoService.info(),expected);
    }

    @Test
    void addRules() {
        String text = "text";
        doNothing()
                .when(infoService)
                .addRules(text);
    }

    @Test
    void addDocuments() {
        String text = "text";
        doNothing()
                .when(infoService)
                .addDocuments(text);
    }

    @Test
    void addTransportation() {
        String text = "text";
        doNothing()
                .when(infoService)
                .addTransportation(text);
    }
}