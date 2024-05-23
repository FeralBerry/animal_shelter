package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.enums.AdminButtonMenuEnum;
import pro.sky.animal_shelter.enums.UserButtonEnum;
import pro.sky.animal_shelter.model.PetRepository;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateButtonService {
    /**
     *
     */
    private final PetRepository petRepository;
    /**
     *
     */
    private final UserRepository userRepository;

    /**
     *
     * @param petRepository
     * @param userRepository
     */
    public CreateButtonService(PetRepository petRepository,UserRepository userRepository){
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    /**
     *
     */
    private final int COUNT_BUTTON_USER_ON_SCREEN = 2;
    /**
     *
     */
    private final int COUNT_BUTTON_ADMIN_ON_SCREEN = 2;

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */

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

    /**
     *
     * @param buttonText
     * @param buttonCommand
     * @return
     */
    private InlineKeyboardButton createButton(String buttonText, String buttonCommand){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }
    // метод для разбиения листа на подлисты

    /**
     *
     * @param list
     * @param n
     * @return
     * @param <T>
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
     *
     * @param chatId
     * @return
     */
    public List createButtonToViewPetList(long chatId){
        // создаем ряды кнопок
        List<Object> rowsInLine;
        // список кнопок 1 ряда
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        User user = new User();
        if(userRepository.findById(chatId).isPresent()){
            user = userRepository.findById(chatId).get();
        }
        long k = 0;
        long countPets = petRepository.count();
        for (int i = 0; i < countPets; i++){
            if(petRepository.findById(user.getPetId()).isPresent() && petRepository.findById(user.getPetId()).get().getId() == user.getPetId()){
                k = i + 1;
            }
        }
        rowInLine.add(createButton("Назад","prev_pet"));
        if(k == 0) {
            rowInLine.add(createButton("1/" + countPets ,""));
        } else {
            rowInLine.add(createButton(k + "/" + countPets,""));
        }
        rowInLine.add(createButton("Вперед","next_pet"));
        rowsInLine = List.of(rowInLine);
        return rowsInLine;
    }

    /**
     *
     * @param text
     * @return
     */
    public List callToUser(String text){
        List<Object> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(createButton(text, "close_call"));
        rowsInLine = List.of(rowInLine);
        return rowsInLine;
    }
}
