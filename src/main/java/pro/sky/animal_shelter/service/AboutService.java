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
     * Метод проверяет существование описания в базе данных, если существует,
     * то собирает из таблиц базы данных сообщение
     * @return строку для вывода пользователю информации о приюте
     */
    public String about(){
        StringBuilder message = new StringBuilder();
        var about = aboutRepository.findAll();
        if(about.isEmpty()){
            message.append("Описание пока отсутствует");
        } else {
            for (About value : about) {
                message.append(value.getShelterName()).append("\n");
                message.append(value.getSchedule()).append("\n");
                message.append(value.getSecurityContacts()).append("\n");
            }
        }
        return message.toString();
    }

    /**
     * Метод для сохранения названия приюта
     * @param text текст из чата с телеграмм ботом
     */
    public void addShelterName(String text){
        About about = aboutRepository.findAbout();
        about.setShelterName(text);
        aboutRepository.save(about);
    }
    /**
     * Метод для сохранения графика приюта
     * @param text текст из чата с телеграмм ботом
     */
    public void addSchedule(String text){
        About about = aboutRepository.findAbout();
        about.setSchedule(text);
        aboutRepository.save(about);
    }
    /**
     * Метод для сохранения контактов охраны
     * @param text текст из чата с телеграмм ботом
     */
    public void addSecurityContacts(String text){
        About about = aboutRepository.findAbout();
        about.setSecurityContacts(text);
        aboutRepository.save(about);
    }
}
