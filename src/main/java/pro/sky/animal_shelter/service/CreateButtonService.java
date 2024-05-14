package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.enums.AdminButtonMenuEnum;
import pro.sky.animal_shelter.enums.UserButtonEnum;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateButtonService {
    private final int COUNT_BUTTON_USER_ON_SCREEN = 2;
    private final int COUNT_BUTTON_ADMIN_ON_SCREEN = 2;
    public List createButtonToMainMenuAdmin(){
        // создаем ряды кнопок
        List<Object> rowsInLine;
        // список кнопок 1 ряда
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        // обходим массив enum кнопок для стартового меню админа
        for (AdminButtonMenuEnum adminButtonMenuEnum : AdminButtonMenuEnum.values()){
            rowInLine.add(createButton(adminButtonMenuEnum.getText(), adminButtonMenuEnum.getCommand()));
        }
        // разделяем кнопки на строки
        rowsInLine = List.of(partition(rowInLine, COUNT_BUTTON_ADMIN_ON_SCREEN));
        return rowsInLine;
    }
    public List createButtonToUser(){
        // создаем ряды кнопок
        List<Object> rowsInLine;
        // список кнопок 1 ряда
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        // добавляем кнопки в строку
        for (UserButtonEnum userButtonEnum : UserButtonEnum.values()){
            rowInLine.add(createButton(userButtonEnum.getText(),userButtonEnum.getCommand()));
        }
        // добавляем кнопки в столбец
        rowsInLine = List.of(partition(rowInLine, COUNT_BUTTON_USER_ON_SCREEN));
        return rowsInLine;
    }
    private InlineKeyboardButton createButton(String buttonText, String buttonCommand){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }
    // метод для разбиения листа на подлисты
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
    public List createPaginationButton(){
        return null;
    }
}
