package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.enums.AdminButtonMenuEnum;
import pro.sky.animal_shelter.enums.PetButtonEnum;
import pro.sky.animal_shelter.enums.UserButtonEnum;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateButtonService {

    private final static int COUNT_BUTTON_USER_ON_SCREEN = 2;
    private final static int COUNT_BUTTON_ADMIN_ON_SCREEN = 2;

    /**
     * Метод создание списка кнопок главного меню администратора
     * @return список кнопок администратора сгруппированных по COUNT_BUTTON_ADMIN_ON_SCREEN
     */
    public List<List<InlineKeyboardButton>> createButtonToMainMenuAdmin(){
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        for (AdminButtonMenuEnum adminButtonMenuEnum : AdminButtonMenuEnum.values()){
            rowInLine.add(createButton(adminButtonMenuEnum.getText(), adminButtonMenuEnum.getCommand()));
        }
        rowsInLine = List.of(partition(rowInLine, COUNT_BUTTON_ADMIN_ON_SCREEN));
        return rowsInLine;
    }

    /**
     * Метод создание списка кнопок меню пользователя который ввел сообщение боту
     * @return список кнопок пользователя сгруппированных по COUNT_BUTTON_USER_ON_SCREEN
     */

    public List<List<InlineKeyboardButton>> createButtonToUser(){
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        for (UserButtonEnum userButtonEnum : UserButtonEnum.values()){
            rowInLine.add(createButton(userButtonEnum.getText(),userButtonEnum.getCommand()));
        }
        rowsInLine = List.of(partition(rowInLine, COUNT_BUTTON_USER_ON_SCREEN));
        return rowsInLine;
    }

    /**
     * Метод генерирующий кнопку
     * @param buttonText текст кнопки
     * @param buttonCommand команда кнопки для дальнейшей обработки callback
     * @return объект содержащий информацию о виде кнопки
     */
    private InlineKeyboardButton createButton(String buttonText, String buttonCommand){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }

    /**
     * Метод для разбиения списка на несколько
     * @param list список который надо разбить на подсписки
     * @param n число по сколько разбивать список
     * @return возвращает список разбитый по n элементов
     * @param <T> передаваемый объект внутри списка
     */
    public static<T> List[] partition(List<T> list, int n)
    {
        int size = list.size();
        int m = size / n;
        if (size % n != 0) {
            m++;
        }
        ArrayList[] partition = new ArrayList[m];
        for (int i = 0; i < m; i++)
        {
            int fromIndex = i*n;
            int toIndex = Math.min(i * n + n, size);

            partition[i] = new ArrayList<>(list.subList(fromIndex, toIndex));
        }
        return partition;
    }

    /**
     * Метод создающий кнопки для просмотра животных (Вперед, Назад)
     * @return возвращает список объектов визуальной клавиатуры с 2 кнопками
     */
    public List<List<InlineKeyboardButton>> createButtonToViewPetList(){
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(createButton(PetButtonEnum.PET_BUTTON_PREV.getText(), PetButtonEnum.PET_BUTTON_PREV.getCommand()));
        rowInLine.add(createButton(PetButtonEnum.PET_BUTTON_NEXT.getText(),PetButtonEnum.PET_BUTTON_NEXT.getCommand()));
        rowsInLine = List.of(rowInLine);
        return rowsInLine;
    }

    /**
     * Создание кнопки закончить разговор у пользователя
     * @param text текст кнопки
     * @return возвращает список объектов визуальной клавиатуры
     */
    public List<List<InlineKeyboardButton>> callToUser(String text){
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(createButton(text, "close_call"));
        rowsInLine = List.of(rowInLine);
        return rowsInLine;
    }
}
