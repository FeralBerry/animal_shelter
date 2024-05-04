package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.UserRepository;

@Slf4j
@Service
public class StartService {
    @Autowired
    private UserRepository userRepository;
    private final String MESSAGE =      "- Узнать информацию о приюте /about\n" +
                                        "- Как взять животное из приюта /info\n" +
                                        "- Просмотреть форму отчета /pet-report-form\n" +
                                        "- Позвать волонтера /to_call_a_volunteer\n" +
                                        "- Если надо отправить отчет то просто ответьте боту";
    public String start(Message message){
        // сохраняем или изменяем данные пользователя и сохраняем их в БД
        User user = new User();
        user.setChatId(message.getChatId());
        user.setFirstName(message.getChat().getFirstName());
        user.setLastName(message.getChat().getLastName());
        user.setUserName(message.getChat().getUserName());
        userRepository.save(user);
        log.info("user saved: " + user);
        return MESSAGE;
    }
}
