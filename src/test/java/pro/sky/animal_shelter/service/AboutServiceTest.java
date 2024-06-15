package pro.sky.animal_shelter.service;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.sky.animal_shelter.model.About;
import pro.sky.animal_shelter.model.Repositories.AboutRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AboutServiceTest {

    @MockBean
    AboutRepository aboutRepository;
    @Autowired
    AboutService aboutService;
    @Test
    void aboutPositive() {
        List<About> aboutList = new ArrayList<>();
        aboutList.add(getAbout());
        when(aboutRepository.findAll()).thenReturn(aboutList);
        StringBuilder message = new StringBuilder();
        for (About value : aboutList) {
            message.append(value.getShelterName()).append("\n");
            message.append(value.getSchedule()).append("\n");
            message.append(value.getSecurityContacts()).append("\n");
        }
        assertEquals(aboutService.about(),message.toString());
    }
    @Test
    void aboutNegative() {
        when(aboutRepository.findAll()).thenReturn(List.of());
        assertEquals(aboutService.about(), "Описание пока отсутствует");
    }
    @Test
    void addShelterNamePositive() {
        when(aboutRepository.findAll()).thenReturn(List.of(getAbout()));
        when(aboutRepository.findAbout()).thenReturn(getAbout());
        About about = getAbout();
        about.setShelterName("text");
        aboutService.addShelterName("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    @Test
    void addShelterNameNegative() {
        when(aboutRepository.findAll()).thenReturn(List.of());
        About about = new About();
        about.setShelterName("text");
        aboutService.addShelterName("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    @Test
    void addSchedulePositive() {
        when(aboutRepository.findAll()).thenReturn(List.of(getAbout()));
        when(aboutRepository.findAbout()).thenReturn(getAbout());
        About about = getAbout();
        about.setSchedule("text");
        aboutService.addSchedule("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    @Test
    void addScheduleNegative() {
        when(aboutRepository.findAll()).thenReturn(List.of());
        About about = new About();
        about.setSchedule("text");
        aboutService.addSchedule("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    @Test
    void addSecurityContactsPositive() {
        when(aboutRepository.findAll()).thenReturn(List.of(getAbout()));
        when(aboutRepository.findAbout()).thenReturn(getAbout());
        About about = getAbout();
        about.setSecurityContacts("text");
        aboutService.addSecurityContacts("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    @Test
    void addSecurityContactsNegative() {
        when(aboutRepository.findAll()).thenReturn(List.of());
        About about = new About();
        about.setSecurityContacts("text");
        aboutService.addSecurityContacts("text");
        ArgumentCaptor<About> captor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepository).save(captor.capture());
        assertEquals(captor.getValue(), about);
    }
    About getAbout(){
        About about = new About();
        about.setId(1L);
        about.setShelterName("1");
        about.setSchedule("1");
        about.setSecurityContacts("1");
        return about;
    }
}