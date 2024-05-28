package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.UserRepository;


@Slf4j
@Service
public class UserStatusService {
    private final UserRepository userRepository;
    public UserStatusService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     *
     * @param chatId
     * @param newStatus
     */
    public void changeUserStatus(long chatId, String newStatus){
        User user = new User();
        /**
         * 1. Находит объект пользователя по идентификатору chatId с помощью метода findById().
         * 2. Если пользователь найден, то устанавливает для него роль, идентификатор чата, имя, фамилию и имя пользователя с помощью
         * методов setRole(), setChatId(), setFirstName(), setLastName() и setUserName().
         * 3. Устанавливает местоположение пользователя в приложении с помощью метода setLocationUserOnApp().
         * 4. Сохраняет изменённого пользователя в базе данных с помощью метода save().
         * 5. Выводит информацию о том, что статус пользователя был изменён, с помощью метода log.info().
         */
        userRepository.findById(chatId)
                .ifPresent(i -> {
                    user.setRole(i.getRole());
                    user.setChatId(chatId);
                    user.setFirstName(i.getFirstName());
                    user.setLastName(i.getLastName());
                    user.setUserName(i.getUserName());
                    user.setAddedPetId(i.getAddedPetId());
                    user.setLocationUserOnApp(newStatus);
                });
        userRepository.save(user);
        log.info("user " + user +" changed status: " + newStatus);
    }

    /**
     * 1. Создаёт экземпляр класса StringBuilder для формирования строки с данными о местоположении пользователя.
     * 2. Находит объект пользователя по идентификатору chatId с помощью метода findById().
     * 3. Если пользователь найден, то добавляет в созданную строку данные о его местоположении в приложении с помощью метода append().
     * 4. Выводит информацию о местоположении пользователя в консоль с помощью метода log.info().
     * 5. Возвращает сформированную строку как результат работы метода getUserStatus().
     * @param chatId - идентификатор пользователя
     */
    public String getUserStatus(long chatId){
        StringBuilder loc = new StringBuilder();
        userRepository.findById(chatId)
                .ifPresent(i -> loc.append(i.getLocationUserOnApp()));
        log.info("user loc: " + loc);
        return loc.toString();
    }
}
