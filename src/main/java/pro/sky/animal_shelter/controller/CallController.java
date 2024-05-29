package pro.sky.animal_shelter.controller;

import org.springframework.stereotype.Controller;
import pro.sky.animal_shelter.service.CallService;

@Controller
public class CallController {
    /**
     * Создаем поле callService для дальнейшей инициализации с помощью конструктора
     * и записи в поле callService всех методов класса CallService
     */
    private final CallService callService;

    /**
     * Конструктор класс CallController в котором инициализируем
     * @param callService методы для обработки соединения между администратором и пользователем бота
     */
    public CallController(CallService callService){
        this.callService = callService;
    }

    /**
     * @param chatId id пользователя написавшего для обратной связи
     * @return возвращает id свободного администратора или 0 если свободного администратора сейчас нет
     */

    public long sendMessageToChat(long chatId){
        return callService.sendMessageChat(chatId);
    }
}
