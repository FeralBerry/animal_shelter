package pro.sky.animal_shelter.controller;


import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import pro.sky.animal_shelter.configuration.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.enums.AdminStatusEnum;

import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.service.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;
import static pro.sky.animal_shelter.enums.AdminButtonMenuEnum.*;
import static pro.sky.animal_shelter.enums.UserButtonEnum.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;
import static pro.sky.animal_shelter.enums.BotCommandEnum.*;

// Slf4j - аннотация для использования логов из библиотеки lombok и авто подключения сервисов в конструктор
@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final PetService petService;
    private final ReportService reportService;
    private final AdminService adminService;
    private final ContactInformationService contactInformationService;
    private final CreateButtonService createButtonService;
    private final UserStatusService userStatusService;
    private final UrlController urlController;
    private final CallController callController;
    // добавочное сообщение в конце
    private final String backMsg =  "Если хотите чтобы с Вами связались нажмите на ссылку или выберете пункт в меню /contact_information \n" +
                                    "Если хотите связаться с волонтером нажмите на ссылку или выберете пункт в меню /to_call_a_volunteer";
    private final BotConfig config;
    public TelegramBot(BotConfig config,
                       PetService petService,
                       ReportService reportService,
                       AdminService adminService,
                       ContactInformationService contactInformationService,
                       CreateButtonService createButtonService,
                       UserStatusService userStatusService,
                       CallController callController,
                       UrlController urlController){
        this.petService = petService;
        this.reportService = reportService;
        this.adminService = adminService;
        this.contactInformationService = contactInformationService;
        this.createButtonService = createButtonService;
        this.userStatusService = userStatusService;
        this.urlController = urlController;
        this.callController = callController;
        this.config = config;
        // создаем список команд для меню
        List<BotCommand> botCommandList = new ArrayList<>();
        // добавление кнопок меню
         botCommandList.add(new BotCommand(START.toString(),"get welcome message"));
        botCommandList.add(new BotCommand(ABOUT.toString(),"find out information about the nursery"));
        botCommandList.add(new BotCommand(INFO.toString(),"information about animals and rules"));
        botCommandList.add(new BotCommand(PET_REPORT_FORM.toString(),"animal report form"));
        botCommandList.add(new BotCommand(TO_CALL_A_VOLUNTEER.toString(),"call a volunteer"));
        botCommandList.add(new BotCommand(CONTACT_INFORMATION.toString(),"feedback"));
        // создаем кнопку с меню и обрабатываем ошибку
        try{
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
    @Override
    public String getBotUsername(){
        return config.getBotName();
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }
    @Override
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
        // формируем приветственное сообщение
        StringBuilder helloMsg = new StringBuilder();
        // проверяем есть ли отправленные сообщение
        if(update.hasMessage() && update.getMessage().hasText()){
            // получаем отправленное сообщение
            helloMsg.append("Привет ")
                    .append(update.getMessage().getChat().getFirstName())
                    .append("\n");
            String message = update.getMessage().getText();
            // получаем id чата
            long chatId = update.getMessage().getChatId();
            if (message.equals(START.toString())) {
                if(adminService.checkAdmin(chatId)){
                    sendButton(chatId, true , urlController.start(update.getMessage()));
                } else{
                    sendMessage(chatId, helloMsg.append(urlController.start(update.getMessage())).toString());
                }
            }
            else if (message.equals(INFO.toString())) {
                helloMsg.append(urlController.info(chatId))
                        .append(backMsg);
                sendMessage(chatId, helloMsg.toString());
            }
            else if (message.equals(ABOUT.toString())) {
                helloMsg.append(urlController.about(chatId))
                        .append(backMsg);
                sendMessage(chatId, helloMsg.toString());
            }
            else if (message.equals(PET_REPORT_FORM.toString())) {
                sendMessage(chatId, urlController.petReportForm(chatId));
            }
            else if (message.equals(CONTACT_INFORMATION.toString())) {
                sendMessage(chatId, urlController.contactInformation(chatId));
            }
            else if (message.equals(TO_CALL_A_VOLUNTEER.toString())) {
                urlController.callVolunteer(chatId);
                if(urlController.callVolunteer(chatId) == 0){
                    sendMessage(chatId,"Пока что все волонтеры заняты");
                } else {
                    sendMessage(chatId,"Пишите сообщения боту он их перенаправит первому освободившемуся волонтеру");
                    sendMessage(urlController.callVolunteer(chatId),"Ожидаем сообщение от");
                    sendButton(urlController.callVolunteer(chatId),true, "Закончить разговор");
                }
            }
            else if (message.equals(PET_LIST.toString())) {
                Pet pet = urlController.petList(chatId);
                sendPhotoMessage(chatId, pet.getData(), pet.getFileName(), pet.getPetName());
                sendButton(chatId, adminService.checkAdmin(chatId), "");
            }
            else {
                if (message.equals(getBotToken())) {
                    // при отправке токена бота пользователь становится администратором
                    adminService.setRole(update.getMessage());
                    sendMessage(chatId, "Поздравляем вы стали админом");
                    // переключить статус пользователя
                    userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
                    sendButton(chatId, adminService.checkAdmin(chatId), "Главное меню администратора.");
                } else if (adminService.checkAdmin(chatId)) {
                    Pet pet = new Pet();
                    switch (AdminStatusEnum.valueOf(userStatusService.getUserStatus(chatId))) {
                        case VIEW_CONTACT_INFORMATION -> {
                            if (isNumeric(message)) {
                                sendMessage(chatId, contactInformationService.deleteContactInformationById(Long.parseLong(message)));
                                sendMessage(chatId, "Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                            } else if (message.equals("exit")) {
                                sendButton(chatId, adminService.checkAdmin(chatId), "Главное меню администратора.");
                            } else {
                                sendMessage(chatId, "Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                            }
                        }
                        case PET_ADD -> {
                            pet.setPetName(message);
                            userStatusService.changeUserStatus(chatId, PET_ADD_NAME.getStatus());
                            sendMessage(chatId, "Загрузите изображение");
                        }
                        case PET_ADD_NAME -> {
                            if (update.getMessage().hasPhoto()) {
                                List<PhotoSize> photos = update.getMessage().getPhoto();
                                log.info(photos.toString());
                            }
                            userStatusService.changeUserStatus(chatId, PET_ADD_IMG.getStatus());
                        }
                        case PET_ADD_IMG -> {
                            petService.addPet(chatId, pet);
                            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
                        }
                        case CALL -> {
                            sendMessage(callController.sendMessageToChat(chatId), message);
                            sendButton(urlController.callVolunteer(chatId),true, "Закончить разговор");
                        }
                        default -> {
                            // реакция бота на текстовое сообщение от администратора
                            sendButton(chatId, adminService.checkAdmin(chatId), "Главное меню администратора.");
                        }
                    }
                } else {
                    // отслеживание статуса пользователя и реагируем на текстовое сообщение
                    if (userStatusService.getUserStatus(chatId).equals(NO_STATUS.getStatus())) {
                        sendButton(chatId, adminService.checkAdmin(chatId), "Для чего вы отправили это сообщение?");
                    } else if (userStatusService.getUserStatus(chatId).equals(GET_PET_FORM.getStatus())) {
                        // реакция на текстовое сообщение боту если пользователь в статусе получить форму отчета по питомцу
                        if (reportService.addImgReport(update.getMessage())) {
                            // статус изображение отчета отправлено
                            userStatusService.changeUserStatus(chatId, ADD_PET_REPORT_IMG.getStatus());
                            sendMessage(chatId, "Отлично! Осталось только, написать о состоянии животного.");
                        } else {
                            sendMessage(chatId, "Первым сообщением отчета нужно отсылать фото.");
                        }
                    } else if (userStatusService.getUserStatus(chatId).equals(GET_CONTACT_INFORMATION.getStatus())) {
                        // реакция на текстовое сообщение боту если пользователь в статусе получить контактную информацию
                        if (contactInformationService.addContactInformation(message)) {
                            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
                            sendButton(chatId, adminService.checkAdmin(chatId), "Ваши данные сохранены. Скоро с Вами свяжутся. Чем я еще могу помочь?");
                        } else {
                            sendMessage(chatId, "Не корректно введены данные.\n" + contactInformationService.getContactInformation());
                        }
                    } else if (userStatusService.getUserStatus(chatId).equals(ADD_PET_REPORT_IMG.getStatus())) {
                        if (reportService.addTextReport(update.getMessage())) {
                            // статус изображение отчета отправлено
                            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
                            sendMessage(chatId, "Супер! Сегодня ежедневный отчет сдан");
                        } else {
                            sendMessage(chatId, "Вторым сообщением отчета нужно отсылать описание состояния животного.");
                        }
                    } else if (userStatusService.getUserStatus(chatId).equals(CALL_A_VOLUNTEER.getStatus())) {
                        sendMessage(callController.sendMessageToChat(chatId), message);
                    } else if (userStatusService.getUserStatus(chatId).equals(VIEW_PET_LIST.getStatus())) {
                        // реакция на текстовое сообщение боту если пользователь в статусе просмотра питомцев
                    }
                }
            }
        }  else if (update.hasCallbackQuery()) {
            // получение id последнего сообщения
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            // получение чата сообщения
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            // получение id кнопки
            String callBackData = update.getCallbackQuery().getData();
            if(adminService.checkAdmin(chatId)){
                // реакции на кнопки администратора
                if(callBackData.equals(VIEW_CONTACT_INFORMATION_COMMAND.getCommand())){
                    // получение списка обратной связи
                    StringBuilder newMessage = new StringBuilder();
                    if(contactInformationService.getAllContactInformation().isEmpty()){
                        newMessage.append("Пока никто не оставлял заявок на обратную связь.");
                        // изменение статуса пользователя
                        userStatusService.changeUserStatus(chatId,NO_STATUS.getStatus());
                        sendButton(chatId, adminService.checkAdmin(chatId), "Главное меню администратора.");
                    } else {
                        for (ContactInformation contactInformation : contactInformationService.getAllContactInformation()){
                            newMessage.append(contactInformation.toString())
                                    .append("\n");
                        }
                        newMessage.append("Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                        // изменение статуса пользователя
                        userStatusService.changeUserStatus(chatId,VIEW_CONTACT_INFORMATION.getStatus());
                    }
                    editMessage(chatId, messageId, newMessage.toString());
                } else if (callBackData.equals(PET_ADD_COMMAND.getCommand())) {
                    userStatusService.changeUserStatus(chatId,PET_ADD.getStatus());
                    sendMessage(chatId,"ВВедите имя питомца.");
                }
            } else {
                // реакции на кнопки пользователя
                if(callBackData.equals(PET_REPORT.getCommand())){
                    // переключить статус пользователя
                    userStatusService.changeUserStatus(chatId,"pet_report");

                    String newMessage = "pet_report";// поменять на метод в сервисе
                    editMessage(chatId, messageId, newMessage);
                } else if (callBackData.equals(CONTACT_INFORMATION_ADD.getCommand())) {
                    // переключить статус пользователя
                    userStatusService.changeUserStatus(chatId, GET_CONTACT_INFORMATION.getStatus());
                    // присылает в каком виде надо отсылать контактную информацию
                    sendMessage(chatId, contactInformationService.getContactInformation());
                } else if(callBackData.equals(VIEW_PETS.getCommand())) {
                    userStatusService.changeUserStatus(chatId, VIEW_PET_LIST.getStatus());
                    Pet pet = petService.getPet(chatId);
                    String description = pet.getPetName();// пока для теста выдает только имя питомца
                    sendPhotoMessage(chatId, pet.getData(), pet.getFileName(), description);
                    sendButton(chatId,adminService.checkAdmin(chatId),"");
                } else if(callBackData.equals(CALL_TO.getCommand())) {

                }
            }
        }
    }
    // метод для отправки сообщения ботом
    protected void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        // обрабатываем ошибку отправки
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    // создание всплывающей кнопки при отправке сообщения
    private void editMessage(long chatId, int messageId, String newMessage){
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(newMessage);
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    void sendButton(long chatId, boolean role, String text){
        SendMessage message = new SendMessage();
        if(text.equals("")){
            message.setChatId(chatId);
        } else {
            message.setChatId(chatId);
            message.setText(text);
        }
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        // проверка роли пользователя
        if (role){
            // проверка статуса пользователя
            if(userStatusService.getUserStatus(chatId).equals(NO_STATUS.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToMainMenuAdmin());
            } else if(userStatusService.getUserStatus(chatId).equals(CALL.getStatus())){
                markupInLine.setKeyboard(createButtonService.callToUser(text));
            }
        } else {
            if(userStatusService.getUserStatus(chatId).equals(NO_STATUS.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToUser());
            } else if (userStatusService.getUserStatus(chatId).equals(VIEW_PET_LIST.getStatus())){
                markupInLine.setKeyboard(createButtonService.createButtonToViewPetList(chatId));
            }
        }
        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    public void sendPhotoMessage(long chatId, byte[] imageBytes, String fileName, String caption){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        InputStream img = new ByteArrayInputStream(imageBytes);
        InputFile file = new InputFile(img,fileName);
        sendPhoto.setPhoto(file);
        sendPhoto.setCaption(caption);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    // проверка является ли строка Long или нет
    public static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
