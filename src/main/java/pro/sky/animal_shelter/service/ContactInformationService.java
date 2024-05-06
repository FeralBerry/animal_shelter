package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.ContactInformationRepository;

import java.util.List;

@Slf4j
@Service
public class ContactInformationService {
    private String MESSAGE= "Введите номер телефона в формате: +7-9**-***-**-** ФИО";
    private final ContactInformationRepository contactInformationRepository;
    public ContactInformationService(ContactInformationRepository contactInformationRepository){
        this.contactInformationRepository = contactInformationRepository;
    }
    public String getContactInformation(){
        // Выдавать сообщение с типом как написать данные +7-9**-***-**-** ФИО.
        return MESSAGE;
    }
    public String addContactInformation(){
        // сохраняем в базу данных и возвращаем строку
        return "";
    }
    public List<ContactInformation> getAllContactInformation(){
        return contactInformationRepository.findAll();
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
