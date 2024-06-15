package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.sky.animal_shelter.model.Info;
import pro.sky.animal_shelter.model.Repositories.InfoRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@AutoConfigureMockMvc
class InfoServiceTest {
    @MockBean
    InfoRepository infoRepository;
    @Autowired
    InfoService infoService;
    @Test
    void infoPositive() {
        Info info = new Info();
        List<Info> infoList = new ArrayList<>();
        info.setId(1L);
        info.setRules("1L");
        info.setDocuments("1L");
        info.setTransportation("1L");
        infoList.add(info);
        when(infoRepository.findAll()).thenReturn(infoList);
        StringBuilder message = new StringBuilder();
        for (Info info1 : infoList) {
            message.append(info1.getDocuments())
                    .append(" ")
                    .append(info1.getRules())
                    .append(" ")
                    .append(info1.getTransportation())
                    .append(" ")
                    .append(info1.getHouseForAPuppy())
                    .append(" ")
                    .append(info1.getHomeForAnAdultAnimal())
                    .append(" ")
                    .append(info1.getHomeForAnAnimalWithDisabilities())
                    .append(" ")
                    .append(info1.getTipsFromADogHandler())
                    .append(" ")
                    .append(info1.getRecommendationsOfADogHandler())
                    .append(" ")
                    .append(info1.getReasonsForRefusal());
        }
        assertEquals(infoService.info(),message.toString());
    }
    @Test
    void infoNegative() {
        when(infoRepository.findAll()).thenReturn(List.of());
        assertEquals(infoService.info(), "Описание пока отсутствует");
    }

    @Test
    void addRulesPositive() {
        when(infoRepository.findAll()).thenReturn(List.of(getInfo()));
        when(infoRepository.findInfo()).thenReturn(getInfo());
        Info info = getInfo();
        info.setRules("text");
        infoService.addRules("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    @Test
    void addRulesNegative() {
        when(infoRepository.findAll()).thenReturn(List.of());
        Info info = new Info();
        info.setRules("text");
        infoService.addRules("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    @Test
    void addDocumentsPositive() {
        when(infoRepository.findAll()).thenReturn(List.of(getInfo()));
        when(infoRepository.findInfo()).thenReturn(getInfo());
        Info info = getInfo();
        info.setDocuments("text");
        infoService.addDocuments("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    @Test
    void addDocumentsNegative() {
        when(infoRepository.findAll()).thenReturn(List.of());
        Info info = new Info();
        info.setDocuments("text");
        infoService.addDocuments("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    @Test
    void addTransportationPositive() {
        when(infoRepository.findAll()).thenReturn(List.of(getInfo()));
        when(infoRepository.findInfo()).thenReturn(getInfo());
        Info info = getInfo();
        info.setTransportation("text");
        infoService.addTransportation("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    @Test
    void addTransportationNegative() {
        when(infoRepository.findAll()).thenReturn(List.of());
        Info info = new Info();
        info.setTransportation("text");
        infoService.addTransportation("text");
        ArgumentCaptor<Info> captor = ArgumentCaptor.forClass(Info.class);
        verify(infoRepository).save(captor.capture());
        assertEquals(captor.getValue(), info);
    }
    Info getInfo(){
        Info info = new Info();
        info.setId(1L);
        info.setDocuments("1L");
        info.setTransportation("1L");
        info.setRules("1L");
        return info;
    }
}