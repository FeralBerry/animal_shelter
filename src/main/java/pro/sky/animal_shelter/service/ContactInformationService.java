package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.ContactInformationRepository;

@Slf4j
@Service
public class ContactInformationService {
    @Autowired
    private ContactInformationRepository contactInformationRepository;
    public String addContactInformation(){
        // сохраняем в базу данных и возвращаем строку
        return "";
    }
    public String getAllContactInformation(){
        return contactInformationRepository.findAll().toString();
    }
    public String deleteContactInformationById(long id){
        if (contactInformationRepository.findById(id).isPresent()){
            contactInformationRepository.deleteByIdContactInformation(id);
            return "Обратная связь под id: " + id + " успешно удалена";
        } else {
            return "Обратная связь под id не найдена";
        }
    }
}
