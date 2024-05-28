package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Repositories.ContactInformationRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ContactInformationService {
    private final String MESSAGE= "Введите номер телефона в формате: +7-9**-***-**-**";
    private final ContactInformationRepository contactInformationRepository;
    public ContactInformationService(ContactInformationRepository contactInformationRepository){
        this.contactInformationRepository = contactInformationRepository;
    }
    /**
     * @return возвращает строку с информацией о формате сообщения для обратной связи
     */
    public String getContactInformation(){
        return MESSAGE;
    }

    /**
     * @param message получаем строку, написанную пользователем в чате с ботом проверяем телефон это или нет
     * @param chatId id чата из которого отправлено сообщение
     * @return возвращаем true сообщение отправлено по формату или false, если не по формату
     */
    public boolean addContactPhone(String message, long chatId){
        Pattern pattern = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$");
        Matcher matcher = pattern.matcher(message);
        ContactInformation contactInformation = new ContactInformation();
        if(matcher.matches()){
            contactInformation.setPhone(message);
            contactInformation.setChatId(chatId);
            contactInformationRepository.save(contactInformation);
            return true;
        }
        return false;
    }

    /**
     * @param message сообщение пользователя с именем
     * @param chatId id чата из которого прислали сообщение
     * @return возвращаем true, если сообщение не пустое, или false, если отправлено пустое сообщение
     */
    public boolean addContactName(String message,long chatId){
        if(message.isEmpty()){
            return false;
        } else {
            ContactInformation contactInformation = contactInformationRepository.findById(chatId).get();
            contactInformation.setName(message);
            contactInformationRepository.save(contactInformation);
            return true;
        }
    }
    /**
     * @return возвращает список всех пользователей запросивших обратной связи
     */
    public List<ContactInformation> getAllContactInformation(){
        return contactInformationRepository.findAll();
    }
    /**
     * @param id id сообщения для удаления из списка запросивших обратной связи
     * @return возвращает строку с состоянием удаления записи
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
