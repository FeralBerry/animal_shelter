package pro.sky.animal_shelter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.service.*;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.List;

import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;
import static pro.sky.animal_shelter.enums.BotCommandEnum.PET_LIST;
import static pro.sky.animal_shelter.enums.PetButtonEnum.PET_BUTTON_NEXT;
import static pro.sky.animal_shelter.enums.PetButtonEnum.PET_BUTTON_PREV;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;

@Controller
@Slf4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UrlService urlService;
    private final TextService textService;
    private final UserStatusService userStatusService;
    private final ButtonService buttonService;
    private final PetService petService;
    private final ReportService reportService;
    public UpdateController(MessageUtils messageUtils, UrlService urlService, TextService textService, UserStatusService userStatusService, ButtonService buttonService, PetService petService, ReportService reportService){
        this.messageUtils = messageUtils;
        this.urlService = urlService;
        this.textService = textService;
        this.userStatusService = userStatusService;
        this.buttonService = buttonService;
        this.petService = petService;
        this.reportService = reportService;
    }
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }
    public void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }
        if (update.getMessage() != null){
            distributeMessageByType(update);
        } else {
            log.error("Received unsupported message type" + update);
        }
    }

    void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if(update.hasCallbackQuery()){
            processButtonMessage(update);
        } else if(message.getText() != null){
            processTextMessage(update);
        } else if(message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessage(update,"Не поддерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
    public void setView(SendMediaGroup sendMediaGroup) {
        telegramBot.sendAnswerMessage(sendMediaGroup);
    }
    public void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
    public void setView(EditMessageText editMessageText){
        telegramBot.sendAnswerMessage(editMessageText);
    }

    private void processPhotoMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        if (userStatusService.getUserStatus(chatId).equals(PET_ADD_NAME.getStatus()) ||
                userStatusService.getUserStatus(chatId).equals(PET_ADD_IMG.getStatus())) {
            List<String> photos = telegramBot.downloadPhotos(update,"src/main/resources/img/pets");
            petService.addPetImages(chatId,photos);
            userStatusService.changeUserStatus(chatId,PET_ADD_IMG.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PET_REPORT_IMG.getStatus())) {
            List<String> photos = telegramBot.downloadPhotos(update,"src/main/resources/img/report/"+ chatId);
            reportService.addImgReport(update,photos);
            userStatusService.changeUserStatus(chatId,ADD_PET_REPORT_TEXT.getStatus());
            setView(messageUtils.generateSendMessage(update, """
                    Отлично фото уже есть осталось прислать:
                    - Рацион животного
                    - Общее самочувствие и привыкание к новому месту
                    - Изменения в поведении: отказ от старых привычек, приобретение новых"""));
        }
    }

    private void processTextMessage(Update update) {
        String message = update.getMessage().getText();
        if(message.charAt(0) == '/'){
            if(message.equals(PET_LIST.toString())){
                petListPhotos(update);
            }
            for(SendMessage msg : urlService.defineACommand(update)){
                setView(msg);
            }
        } else {
            for(SendMessage msg : textService.defineMethod(update)){
                setView(msg);
            }
        }
    }
    private void processButtonMessage(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getFrom().getId();
        if(callBackData.equals(PET_BUTTON_PREV.getCommand())) {
            petService.changePrevPetView(chatId);
            petListPhotos(update);
        } else if(callBackData.equals(PET_BUTTON_NEXT.getCommand())) {
            petService.changeNextPetView(chatId);
            petListPhotos(update);
        }
        for (SendMessage msg : buttonService.defineCommand(update)){
            setView(msg);
        }
    }
    private void petListPhotos(Update update){
        if(petService.getPetImages(update).size() > 1){
            SendMediaGroup sendMediaGroups = messageUtils.sendMediaGroup(update.getMessage().getChatId(), petService.getPetImages(update));
            setView(sendMediaGroups);
        } else {
            SendPhoto sendPhoto = messageUtils.sendPhoto(update.getMessage().getChatId(),petService.getPetImages(update).get(0));
            setView(sendPhoto);
        }
    }
}
