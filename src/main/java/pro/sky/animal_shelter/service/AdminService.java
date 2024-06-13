package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;

@Slf4j
@Service
public class AdminService {
    private final UserRepository userRepository;
    public AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Проверка является ли пользователь администратором
     * @param chatId принимает id пользователя
     * @return возвращаем true, если пользователь администратор, false, если пользователь не является администратором
     */
    public boolean checkAdmin(long chatId){
        var user = userRepository.findById(chatId);
        StringBuilder role = new StringBuilder();
        if(user.isEmpty()) {
            return false;
        } else{
            user.ifPresent(item->role.append(item.getRole()));
        }
        return role.toString().equals("admin");
    }

    /**
     * Метод изменения статуса пользователя на администратора
     * @param message получаем всю информацию о пользователе, что прислал сообщение,
     *                и сохраняем пользователя с базу данных с новой ролью
     */
    public void setRole(Message message){
        User user = new User();
        user.setChatId(message.getChatId());
        user.setFirstName(message.getChat().getFirstName());
        user.setLastName(message.getChat().getLastName());
        user.setUserName(message.getChat().getUserName());
        user.setRole("admin");
        userRepository.save(user);
        log.info("user saved: " + user);
    }
}
