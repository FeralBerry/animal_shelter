package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.enums.AdminButtonMenuEnum;
import pro.sky.animal_shelter.enums.PetButtonEnum;
import pro.sky.animal_shelter.enums.UserButtonEnum;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CreateButtonService {

    public CreateButtonService(){

    }
    private final static int COUNT_BUTTON_USER_ON_SCREEN = 2;
    private final static int COUNT_BUTTON_ADMIN_ON_SCREEN = 2;

    /**
     *
     * @return
     */
    public List createButtonToMainMenuAdmin(){
        List<Object> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        for (AdminButtonMenuEnum adminButtonMenuEnum : AdminButtonMenuEnum.values()){
            rowInLine.add(createButton(adminButtonMenuEnum.getText(), adminButtonMenuEnum.getCommand()));
        }
        rowsInLine = List.of(partition(rowInLine, COUNT_BUTTON_ADMIN_ON_SCREEN));
        return rowsInLine;
    }

    /**
     *
     * @return
     */

    public List createButtonToUser(){
        List<Object> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        for (UserButtonEnum userButtonEnum : UserButtonEnum.values()){
            rowInLine.add(createButton(userButtonEnum.getText(),userButtonEnum.getCommand()));
        }
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
     * @return
     */
    public List createButtonToViewPetList(){
        List<Object> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(createButton(PetButtonEnum.PET_BUTTON_PREV.getText(), PetButtonEnum.PET_BUTTON_PREV.getCommand()));
        rowInLine.add(createButton(PetButtonEnum.PET_BUTTON_NEXT.getText(),PetButtonEnum.PET_BUTTON_NEXT.getCommand()));
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
