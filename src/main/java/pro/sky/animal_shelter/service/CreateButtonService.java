package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateButtonService {
    public List<List<InlineKeyboardButton>> createButtonToAdmin(){
        // создаем ряды кнопок
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        // список кнопок 1 ряда
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        // добавляем кнопки в строку
        rowInLine.add(createButton("Добавить питомца","pet_list_add"));
        rowInLine.add(createButton("Просмотреть обратную связь","view_contact_information"));
        // добавляем кнопки в столбец
        rowsInLine.add(rowInLine);
        return rowsInLine;
    }
    public List<List<InlineKeyboardButton>> createButtonToUser(){
        // создаем ряды кнопок
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        // список кнопок 1 ряда
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        // добавляем кнопки в строку
        rowInLine.add(createButton("Отправить отчет","pet_report"));
        rowInLine.add(createButton("Записать контактные данные","contact_information_add"));
        // добавляем кнопки в столбец
        rowsInLine.add(rowInLine);
        return rowsInLine;
    }
    private InlineKeyboardButton createButton(String buttonText, String buttonCommand){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }
}
