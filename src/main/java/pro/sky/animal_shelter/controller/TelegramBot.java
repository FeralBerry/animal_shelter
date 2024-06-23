package pro.sky.animal_shelter.controller;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.animal_shelter.enums.BotCommandEnum;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.service.*;

import static pro.sky.animal_shelter.enums.UserSatausEnum.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class  TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.bot.name}")
    private String botName;
    private final UpdateController updateController;
    private final AdminReportController adminReportController;
    private final CallRepository callRepository;
    private final UserStatusService userStatusService;
    private final ReportService reportService;
    private final UrlService urlService;
    private final PetService petService;
    private final ButtonService buttonService;
    public TelegramBot(UpdateController updateController, AdminReportController adminReportController,
                       CallRepository callRepository, UserStatusService userStatusService, ReportService reportService, UrlService urlService, PetService petService, ButtonService buttonService){
        this.updateController = updateController;
        this.adminReportController = adminReportController;
        this.callRepository = callRepository;
        this.userStatusService = userStatusService;
        this.reportService = reportService;
        this.urlService = urlService;
        this.petService = petService;
        this.buttonService = buttonService;
        List<BotCommand> botCommandList = new ArrayList<>();
        BotCommandEnum[] botCommandEnums = BotCommandEnum.values();
        for (BotCommandEnum botCommandEnum : botCommandEnums) {
            botCommandList.add(new BotCommand(botCommandEnum.url(), botCommandEnum.description()));
        }
        try{
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }
    @PostConstruct
    public void init(){
        updateController.registerBot(this);
        adminReportController.registerBot(this);
        reportService.registerBot(this);
        urlService.registerBot(this);
        petService.registerBot(this);
        buttonService.registerBot(this);
    }
    @Override
    public String getBotUsername(){
        return botName;
    }
    @Override
    public String getBotToken(){
        return token;
    }
    @Override
    public void onUpdateReceived(Update update) {
        updateController.distributeMessageByType(update);
    }
    public void sendAnswerMessage(SendMessage message){
        if (message != null){
            try {
                execute(message);
            } catch (TelegramApiException e){
                log.error("Error occurred: " + e.getMessage());
            }
        }
    }
    public void sendAnswerMessage(SendPhoto sendPhoto){
        if (sendPhoto != null){
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e){
                log.error("Error occurred: " + e.getMessage());
            }
        }
    }
    public void sendAnswerMessage(SendMediaGroup sendPhoto){
        if (sendPhoto != null){
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e){
                log.error("Error occurred: " + e.getMessage());
            }
        }
    }
    public void sendAnswerMessage(EditMessageText editMessageText){
        if (editMessageText != null){
            try {
                execute(editMessageText);
            } catch (TelegramApiException e){
                log.error("Error occurred: " + e.getMessage());
            }
        }
    }
    public List<String> downloadPhotos(Update update, String url){
        GetFile getFile = new GetFile();
        List<PhotoSize> photos = update.getMessage().getPhoto();
        List<String> petPhotos = new ArrayList<>();
        List<String> largePhotos = new ArrayList<>();
        for (PhotoSize photo : photos){
            getFile.setFileId(photo.getFileId());
            try
            {
                File file = execute(getFile);
                petPhotos.add(photo.getFileId());
                for (int i = 0; i < petPhotos.size(); i++){
                    if((i + 1) % 3 == 0){
                        largePhotos.add(petPhotos.get(i));
                        downloadFile(file, new java.io.File( url + "/" + photo.getFileId() + ".png"));
                    }
                }

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return largePhotos;
    }
    /**
     * Метод для отслеживания открытых чатов и закрытие их после 30 минут без сообщений
     * проверяем существование таких чатов раз в минуту
     */

    @Scheduled(cron = "0 * * * * *")
    public void closeChat(){
        long nowSec = (new Date().getTime())/1000;
        Call call;
        List<Call> callList = callRepository.findByChatUpdatedBefore(nowSec);
        for (Call value : callList) {
            call = value;
            if ((call.getUpdatedAt() + 3600L) > nowSec
                    || !userStatusService.getUserStatus(call.getUserChatId().getChatId()).equals(CALL_A_VOLUNTEER.getStatus())) {
                callRepository.delete(call);
                userStatusService.changeUserStatus(call.getUserChatId().getChatId(), NO_STATUS.getStatus());
                SendMessage message = new SendMessage();
                message.setChatId(call.getUserChatId().getChatId());
                message.setText("Чат закрыт автоматически для нового обращения /to_call_a_volunteer");
                sendAnswerMessage(message);
                userStatusService.changeUserStatus(call.getAdminChatId().getChatId(), NO_STATUS.getStatus());
                message.setChatId(call.getAdminChatId().getChatId());
                message.setText("Чат с пользователем был закрыт");
                sendAnswerMessage(message);
            }
        }
    }
    @Scheduled(cron = "0 0 21 * * *")
    public void alarmReport(){
        List<User> chatIds = reportService.alarmReport();
        if(!chatIds.isEmpty()){
            for (User chatId : chatIds){
                SendMessage message = new SendMessage();
                message.setChatId(chatId.getChatId());
                message.setText("Вы забыли про отчет по питомцу");
                sendAnswerMessage(message);
            }
        }
    }
}

