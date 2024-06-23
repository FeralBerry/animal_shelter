package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Repositories.ContactInformationRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ContactInformationService {
    private final UserRepository userRepository;
    private final static String MESSAGE= "Введите номер телефона в формате: +7 9** ***-**-**";
    private final ContactInformationRepository contactInformationRepository;
    public ContactInformationService(UserRepository userRepository, ContactInformationRepository contactInformationRepository){
        this.userRepository = userRepository;
        this.contactInformationRepository = contactInformationRepository;
    }
    /**
     * @return возвращает строку с информацией о формате сообщения для обратной связи
     */
    public String getContactInformation(){
        return MESSAGE;
    }

    /** Добавляет телефон пользователя в таблицу ContactInformation. Входной параметр - Update
     * @return возвращаем true сообщение отправлено по формату или false, если не по формату
     */
    public boolean addContactPhone(Update update){
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        Pattern pattern = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$");
        Matcher matcher = pattern.matcher(message);
        ContactInformation contactInformation = new ContactInformation();
        Optional<User> opUser = userRepository.findById(chatId);
        User user;
        var contactInformationById = contactInformationRepository.findByUserId(chatId);
        if(opUser.isPresent()){
            user = opUser.get();
        } else {
            throw new RuntimeException("Пользователя с таким id не существует");
        }
        if(contactInformationById.isEmpty()){
            if(matcher.matches()){
                contactInformation.setPhone(message);
                contactInformation.setChatId(user);
                contactInformationRepository.save(contactInformation);
                return true;
            }
        } else {
            if(matcher.matches()){
                contactInformationById.get().setPhone(message);
                contactInformationById.get().setChatId(user);
                contactInformationRepository.save(contactInformationById.get());
                return true;
            }
        }
        return false;
    }

    /**Добавляет имя пользователя в таблицу ContactInformation. На вход принимает Update
     * @return возвращаем true, если сообщение не пустое, или false, если отправлено пустое сообщение
     */
    public boolean addContactName(Update update){
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        ContactInformation contactInformation;
        if(message.isEmpty()){
            return false;
        } else {
            var contactInformationById = contactInformationRepository.findByUserId(chatId);
            if(contactInformationById.isPresent()){
                contactInformation = contactInformationById.get();
                contactInformation.setName(message);
                contactInformationRepository.save(contactInformation);
                return true;
            } else {
                throw new NoSuchElementException("Контактной информации от пользователя " + chatId + " не найдена");
            }
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
