package pro.sky.animal_shelter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.service.*;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.AdminStatusEnum.PET_ADD_IMG;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.PET_ADD_NAME;
import static pro.sky.animal_shelter.enums.BotCommandEnum.PET_LIST;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;

@Controller
@Slf4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UrlService urlService;
    private final TextService textService;
    private final AdminService adminService;
    private final UserStatusService userStatusService;
    private final ButtonService buttonService;
    private final PetService petService;
    private final ReportService reportService;
    public UpdateController(MessageUtils messageUtils, UrlService urlService, TextService textService, AdminService adminService, UserStatusService userStatusService, ButtonService buttonService, PetService petService, ReportService reportService){
        this.messageUtils = messageUtils;
        this.urlService = urlService;
        this.textService = textService;
        this.adminService = adminService;
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
        } else if(message.getDocument() != null) {
            processDocumentMessage(update);
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
    public void setView(EditMessageText editMessageText){
        telegramBot.sendAnswerMessage(editMessageText);
    }

    private void processDocumentMessage(Update update) {

    }

    private void processPhotoMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        if (userStatusService.getUserStatus(chatId).equals(PET_ADD_NAME.getStatus()) ||
                userStatusService.getUserStatus(chatId).equals(PET_ADD_IMG.getStatus())) {
            List<String> photos = telegramBot.downloadPhotos(update,"src/main/resources/img/pets");
            adminService.addPetPhotos(update,photos);
            userStatusService.changeUserStatus(chatId,PET_ADD_IMG.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PET_REPORT_IMG.getStatus())) {
            List<String> photos = telegramBot.downloadPhotos(update,"src/main/resources/img/report/"+ chatId);
            reportService.addImgReport(update,photos);
            userStatusService.changeUserStatus(chatId,ADD_PET_REPORT_TEXT.getStatus());
            setView(messageUtils.generateSendMessage(update,"Отлично фото уже есть осталось прислать:\n" +
                    "- Рацион животного" +"\n" +
                    "- Общее самочувствие и привыкание к новому месту" +"\n" +
                    "- Изменения в поведении: отказ от старых привычек, приобретение новых"));
        }
    }

    private void processTextMessage(Update update) {
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if(message.charAt(0) == '/'){
            if(message.equals(PET_LIST.toString())){
                if(petService.getPetImages(chatId).size() > 1){
                    List <SendMediaGroup> sendMediaGroups = new ArrayList<>();
                    sendMediaGroups.add(messageUtils.sendMediaGroup(chatId, petService.getPetImages(chatId)));
                    for (SendMediaGroup msg : sendMediaGroups){
                        setView(msg);
                    }
                } else {

                }
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
        for (SendMessage msg : buttonService.defineCommand(update)){
            setView(msg);
        }
    }

}
