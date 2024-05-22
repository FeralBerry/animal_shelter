package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import pro.sky.animal_shelter.model.About;
import pro.sky.animal_shelter.model.AboutRepository;

@Slf4j
@Service
public class AboutService {

    private final AboutRepository aboutRepository;

    public AboutService(AboutRepository aboutRepository){
        this.aboutRepository = aboutRepository;
    }

    /**
     * Метод берет информацию о приюте из БД и генерирует сообщение пользователю
     * @return возвращает строку для вывода ее пользователю
     */
    public String about(){
        StringBuilder message = new StringBuilder();
        var about = aboutRepository.findAll();
        if(about.isEmpty()){
            message.append("Описание пока отсутствует");
        } else {
            for (About about1 : about) {
                message.append(about1.getShelterName()).append("\n");
                message.append(about1.getSchedule()).append("\n");
                message.append(about1.getSecurityContacts()).append("\n");
                message.append(about1.getSafetyPrecautions()).append("\n");
            }
        }
        return message.toString();
    }
}
