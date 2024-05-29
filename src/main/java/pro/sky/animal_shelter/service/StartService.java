package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

@Slf4j
@Service
public class StartService {
    /**
     *Создаем поле userRepository для дальнейшей инициализации с помощью конструктора
     *и записи в поле userRepository всех методов класса UserRepository
     */
    private final UserRepository userRepository;

    /**
     * Конструктор класса StartService, в котиором инициализирууем поле (внедряем зависимость)
     * @param userRepository для использования методов класса userRepository
     */
    public StartService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Инициализируем строковую переменную с текстом для стартового сообщения, содержащего
     * информацию о командах бота
     */
    private final static String MESSAGE =      "- Узнать информацию о приюте /about\n" +
                                        "- Как взять животное из приюта /info\n" +
                                        "- Просмотреть форму отчета /pet_report_form\n" +
                                        "- Позвать волонтера /to_call_a_volunteer\n" +
                                        "- Если надо отправить отчет то просто ответьте боту";

    /**
     * Метод создаёт экземпляр класса User и инициализирует его поля chatId, firstName, lastName и userName
     * данными, содержащимися в одноимённыз полях message. Сохраняет готовую сущность user в БД.
     * @return значение строковой переменной MESSAGE
     */
    public String start(){
        return MESSAGE;
    }
    public void register(Message message){
        long chatId = message.getChatId();
        User user = new User();
        if (userRepository.findById(chatId).isEmpty()){
            user.setChatId(message.getChatId());
            user.setFirstName(message.getChat().getFirstName());
            user.setLastName(message.getChat().getLastName());
            user.setUserName(message.getChat().getUserName());
            user.setUserName(message.getChat().getUserName());
        } else {
            user = userRepository.findById(chatId).get();
        }
        userRepository.save(user);
        log.info("user saved: " + user);
    }
}
