package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

class AboutServiceTest {
    AboutService aboutService = Mockito.mock(AboutService.class);

    @Test
    void about() {
        String expected = "about";
        doReturn(expected)
                .when(aboutService)
                .about();
        assertEquals(aboutService.about(),expected);
    }

    @Test
    void addShelterName() {
        String text = "text";
        doNothing()
                .when(aboutService)
                .addShelterName(text);
    }

    @Test
    void addSchedule() {
        String text = "text";
        doNothing()
                .when(aboutService)
                .addSchedule(text);
    }

    @Test
    void addSecurityContacts() {
        String text = "text";
        doNothing()
                .when(aboutService)
                .addSecurityContacts(text);
    }
}