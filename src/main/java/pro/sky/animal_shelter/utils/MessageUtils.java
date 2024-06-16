package pro.sky.animal_shelter.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.service.AdminService;
import pro.sky.animal_shelter.service.CreateButtonService;
import pro.sky.animal_shelter.service.UserStatusService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.UserSatausEnum.*;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;

@Component
public class MessageUtils {
    private final AdminService adminService;
    private final UserStatusService userStatusService;
    private final CreateButtonService createButtonService;

    public MessageUtils(AdminService adminService, UserStatusService userStatusService, CreateButtonService createButtonService) {
        this.adminService = adminService;
        this.userStatusService = userStatusService;
        this.createButtonService = createButtonService;
    }
    /**
     * Отправка текстового сообщения пользователю
     * @param update объект чата в который надо отправить текстовое сообщение
     * @param text текст, отправляемый пользователю
     */
    public SendMessage generateSendMessage(Update update, String text){
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        return sendMessage;
    }
    public SendMessage generateSendMessage(long chatId, String text){
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }
    /**
     * Метод отправки всплывающих кнопок пользователю
     * @param chatId id чата куда отправить кнопки
     * @param text текст сообщения пользователю перед кнопками, если текст не нужен нужно передать ""
     */
    public SendMessage generateSendButton(long chatId, String text){
        SendMessage message = new SendMessage();
        if(text.equals("")){
            message.setChatId(chatId);
        } else {
            message.setChatId(chatId);
            message.setText(text);
        }
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        String userStatus = userStatusService.getUserStatus(chatId);
        if (adminService.checkAdmin(chatId)){
            if(userStatus.equals(NO_STATUS.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToMainMenuAdmin());
            } else if(userStatus.equals(CALL.getStatus())){
                markupInLine.setKeyboard(createButtonService.callToUser(text));
            } else if (userStatus.equals(VIEW_PET_LIST.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToViewPetList());
            }
        } else {
            if(userStatus.equals(NO_STATUS.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToUser());
            } else if (userStatus.equals(VIEW_PET_LIST.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToViewPetList());
            }
        }
        message.setReplyMarkup(markupInLine);
        return message;
    }
    /**
     * Метод редактирования сообщения в чате
     * @param update объект чата в котором надо отредактировать сообщение
     * @param newMessage сообщение на которое надо заменить
     */

    public EditMessageText generateEditMessage(Update update, String newMessage){
        EditMessageText message = new EditMessageText();
        long chatId = update.getMessage().getChatId();
        int messageId = update.getMessage().getMessageId();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(newMessage);
        return message;
    }
    /**
     * Метод отправки изображений в чат с ботом
     * @param chatId id чата куда отправлять фото
     */
    public SendPhoto sendPhoto(long chatId,String image){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File file = new File(image);
        InputFile inputFile = new InputFile(file,file.getName());
        sendPhoto.setPhoto(inputFile);
        return sendPhoto;
    }
    public SendMediaGroup sendMediaGroup(long chatId, List<String> images) {
        List<InputMedia> list = new ArrayList<>();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        if (images.size() > 1){
            for (int i = 0; i < images.size(); i++) {
                if(i < 10){
                    InputMedia photo = new InputMediaPhoto();
                    InputFile file = new InputFile(new java.io.File("src/main/resources/img/pets/" + images.get(i) + ".png"), images.get(i));
                    photo.setMedia(file.getNewMediaFile(),file.getMediaName());
                    list.add(photo);
                }
            }
            sendMediaGroup.setChatId(chatId);
            sendMediaGroup.setMedias(list);
        }
        return sendMediaGroup;
    }
    /**
     * Метод отправки фотографии с подписью
     * @param chatId id чата куда отправить фото
     * @param image url изображения
     * @param caption подпись которую нужно отправить
     */

    public SendPhoto sendPhoto(long chatId, String image, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File file = new File(image);
        InputFile inputFile = new InputFile(file,file.getName());
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }

}
