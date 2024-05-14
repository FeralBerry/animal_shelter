package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.ContactInformationRepository;

import java.util.List;

@Slf4j
@Service
public class ContactInformationService {
    /**
     *
     */
    private String MESSAGE= "Введите номер телефона в формате: +7-9**-***-**-** ФИО";
    /**
     *
     */
    private final ContactInformationRepository contactInformationRepository;

    /**
     *
     * @param contactInformationRepository
     */
    public ContactInformationService(ContactInformationRepository contactInformationRepository){
        this.contactInformationRepository = contactInformationRepository;
    }

    /**
     *
     * @return
     */
    public String getContactInformation(){
        // Выдавать сообщение с типом как написать данные +7-9**-***-**-** ФИО.
        return MESSAGE;
    }

    /**
     *
     * @param message
     * @return
     */
    public boolean addContactInformation(String message){
        // парсим сообщение, сохраняем в базу данных и возвращаем true, если не правильно прислано сообщение, то присылаем false
        return false;
    }

    /**
     *
     * @return
     */
    public List<ContactInformation> getAllContactInformation(){
        return contactInformationRepository.findAll();
    }
    /**
     *
     * @param id
     * @return
     */
    public String deleteContactInformationById(long id){
        if (contactInformationRepository.findById(id).isPresent()){
            contactInformationRepository.deleteById(id);
            return "Обратная связь под id: " + id + " успешно удалена";
        } else {
            return "Обратная связь под id не найдена";
        }
    }
}
