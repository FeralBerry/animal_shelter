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
    private final String MESSAGE =    "- Узнать информацию о приюте /about\n" +
                                "- Как взять животное из приюта /info\n" +
                                "- Просмотреть форму отчета /pet-report-form\n" +
                                "- Позвать волонтера /to-call-a-volunteer\n" +
                                "- Если надо отправить отчет то просто ответьте боту";
    public String start(Message message){
        // Проверяем был ли раньше этот пользователь зарегистрирован в боте
        var userVar = userRepository.findById(message.getChatId());
        if(userVar.isEmpty()){
            User user = new User();
            user.setChatId(message.getChatId());
            user.setFirstName(message.getChat().getFirstName());
            user.setLastName(message.getChat().getLastName());
            user.setUserName(message.getChat().getUserName());
            userRepository.save(user);
            log.info("user saved: " + user);
        } else {
            // проверяем не менял ли пользователь данные, если да то меняем в БД
            User user = new User();
            for (User user1 : userVar.stream().toList()) {
                if(!user1.getUserName().equals(message.getChat().getUserName())){
                    user.setUserName(message.getChat().getUserName());
                }
                if(!user1.getFirstName().equals(message.getChat().getFirstName())){
                    user.setFirstName(message.getChat().getFirstName());
                }
                if(!user1.getLastName().equals(message.getChat().getLastName())){
                    user.setLastName(message.getChat().getLastName());
                }
            }
            if (user == null){
                userRepository.save(user);
            }
            log.info("user changed: " + user);
        }
        return MESSAGE;
    }
}
