package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import pro.sky.animal_shelter.model.About;
import pro.sky.animal_shelter.model.Repositories.AboutRepository;

@Slf4j
@Service
public class AboutService {
   
    private final AboutRepository aboutRepository;

    public AboutService(AboutRepository aboutRepository){
        this.aboutRepository = aboutRepository;
    }

    /**
     * 1. Создаёт экземпляр класса StringBuilder для формирования строки с информацией о данных объекта About.
     * 2. Находит все объекты типа About с помощью метода findAll().
     * 3. Если список объектов About пуст, то в строку добавляется сообщение «Описание пока отсутствует».
     * 4. В противном случае происходит перебор всех найденных объектов и добавление их свойств в формируемую строку.
     * 5. Возвращает сформированную строку как результат работы метода about().
     */
    public String about(){
        StringBuilder message = new StringBuilder();
        var about = aboutRepository.findAll();
        if(about.isEmpty()){
            message.append("Описание пока отсутствует");
        } else {
            for (int i = 0; i < about.size();i++) {
                message.append(about.get(i).getShelterName()).append("\n");
                message.append(about.get(i).getSchedule()).append("\n");
                message.append(about.get(i).getSecurityContacts()).append("\n");
            }
        }
        return message.toString();
    }
    public void addShelterName(String text){
        About about = aboutRepository.findAbout();
        about.setShelterName(text);
        aboutRepository.save(about);
    }
    public void addSchedule(String text){
        About about = aboutRepository.findAbout();
        about.setSchedule(text);
        aboutRepository.save(about);
    }
    public void addSecurityContacts(String text){
        About about = aboutRepository.findAbout();
        about.setSecurityContacts(text);
        aboutRepository.save(about);
    }
}
