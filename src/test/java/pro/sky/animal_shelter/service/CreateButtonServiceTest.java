package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.enums.AdminButtonMenuEnum;
import pro.sky.animal_shelter.enums.PetButtonEnum;
import pro.sky.animal_shelter.enums.UserButtonEnum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateButtonServiceTest {
    private final static int COUNT_BUTTON_ADMIN_ON_SCREEN = 2;
    private final static int COUNT_BUTTON_USER_ON_SCREEN = 2;

    @Test
    void createButtonToMainMenuAdmin() {
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        CreateButtonService createButtonService = new CreateButtonService();
        for (AdminButtonMenuEnum adminButtonMenuEnum : AdminButtonMenuEnum.values()){
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(adminButtonMenuEnum.getText());
            inlineKeyboardButton.setCallbackData(adminButtonMenuEnum.getCommand());
            rowInLine.add(inlineKeyboardButton);
        }
        rowsInLine = List.of(createButtonService.partition(rowInLine, COUNT_BUTTON_ADMIN_ON_SCREEN));
        assertEquals(rowsInLine,createButtonService.createButtonToMainMenuAdmin());
    }

    @Test
    void createButtonToUser() {
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        CreateButtonService createButtonService = new CreateButtonService();
        for (UserButtonEnum userButtonEnum : UserButtonEnum.values()){
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(userButtonEnum.getText());
            inlineKeyboardButton.setCallbackData(userButtonEnum.getCommand());
            rowInLine.add(inlineKeyboardButton);
        }
        rowsInLine = List.of(createButtonService.partition(rowInLine, COUNT_BUTTON_USER_ON_SCREEN));
        assertEquals(rowsInLine,createButtonService.createButtonToMainMenuAdmin());
    }

    @Test
    void partition() {
        List list = List.of(1,2,3,4);
        int n = 2;
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
        CreateButtonService createButtonService = new CreateButtonService();
        assertEquals(partition.length,createButtonService.partition(list,n).length);
        assertEquals(partition[0],createButtonService.partition(list,n)[0]);
        assertEquals(partition[1],createButtonService.partition(list,n)[1]);
    }

    @Test
    void createButtonToViewPetList() {
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(PetButtonEnum.PET_BUTTON_PREV.getText());
        inlineKeyboardButton.setCallbackData(PetButtonEnum.PET_BUTTON_PREV.getCommand());
        rowInLine.add(inlineKeyboardButton);
        inlineKeyboardButton.setText(PetButtonEnum.PET_BUTTON_NEXT.getText());
        inlineKeyboardButton.setCallbackData(PetButtonEnum.PET_BUTTON_NEXT.getCommand());
        rowInLine.add(inlineKeyboardButton);
        rowsInLine = List.of(rowInLine);
        CreateButtonService createButtonService = new CreateButtonService();
        assertEquals(rowsInLine,createButtonService.createButtonToViewPetList());
    }

    @Test
    void callToUser() {
        List<List<InlineKeyboardButton>> rowsInLine;
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        String text = "text";
        String close_call = "close_call";
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(close_call);
        rowInLine.add(inlineKeyboardButton);
        rowsInLine = List.of(rowInLine);
        CreateButtonService createButtonService = new CreateButtonService();
        assertEquals(rowsInLine,createButtonService.callToUser(text));
    }
}