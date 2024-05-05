package pro.sky.animal_shelter.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import pro.sky.animal_shelter.configuration.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.service.*;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.BotCommandEnum.*;

// Slf4j - аннотация для использования логов из библиотеки lombok и авто подключения сервисов в конструктор
@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final AboutService aboutService;
    private final InfoService infoService;
    private final PetService petService;
    private final ReportService reportService;
    private final StartService startService;
    private final AdminService adminService;
    private final ContactInformationService contactInformationService;
    private final CreateButtonService createButtonService;
    private final UserStatusService userStatusService;
    // добавочное сообщение в конце
    private final String backMsg =  "Если хотите чтобы с Вами связались нажмите на ссылку или выберете пункт в меню /contact_information \n" +
                                    "Если хотите связаться с волонтером нажмите на ссылку или выберете пункт в меню /to_call_a_volunteer";
    private final BotConfig config;
    public TelegramBot(BotConfig config,
                       AboutService aboutService,
                       InfoService infoService,
                       PetService petService,
                       ReportService reportService,
                       StartService startService,
                       AdminService adminService,
                       ContactInformationService contactInformationService,
                       CreateButtonService createButtonService,
                       UserStatusService userStatusService){
        this.aboutService = aboutService;
        this.infoService = infoService;
        this.petService = petService;
        this.reportService = reportService;
        this.startService = startService;
        this.adminService = adminService;
        this.contactInformationService = contactInformationService;
        this.createButtonService = createButtonService;
        this.userStatusService = userStatusService;
        this.config = config;
        // создаем список команд для меню
        List<BotCommand> botCommandList = new ArrayList<>();
        // добавление кнопок меню
        botCommandList.add(new BotCommand(START.getUrl(),"get welcome message"));
        botCommandList.add(new BotCommand(ABOUT.getUrl(),"find out information about the nursery"));
        botCommandList.add(new BotCommand(INFO.getUrl(),"information about animals and rules"));
        botCommandList.add(new BotCommand(PET_REPORT_FORM.getUrl(),"animal report form"));
        botCommandList.add(new BotCommand(TO_CALL_A_VOLUNTEER.getUrl(),"call a volunteer"));
        botCommandList.add(new BotCommand(CONTACT_INFORMATION.getUrl(),"feedback"));
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
            // сверяем полученное сообщение и выполняем команду
            if(message.equals(START.getUrl())){
                // формируем сообщение из startService
                helloMsg.append(startService.start(update.getMessage()));
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            } else if (message.equals(ABOUT.getUrl())) {
                // формируем сообщение из aboutService и стандартного сообщения о связи
                helloMsg.append(aboutService.about())
                        .append(backMsg);
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            } else if (message.equals(INFO.getUrl())) {
                // формируем сообщение из infoService и стандартного сообщения о связи
                helloMsg.append(infoService.info())
                        .append(backMsg);
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            } else if (message.equals(PET_REPORT_FORM.getUrl())) {
                // присылает форму отчета
                sendMessage(chatId, petService.getPetForm());
            } else if (message.equals(CONTACT_INFORMATION.getUrl())) {
                // присылает в каком виде надо отсылать контактную информацию
                sendMessage(chatId, contactInformationService.getContactInformation());
            } else if (message.equals(TO_CALL_A_VOLUNTEER.getUrl())) {
                // должен обрабатывать метод сервиса
                // Вызов волонтера осуществляется одним из следующих способов (на выбор разработчика):
                //- по номеру телефона;
                //- по никнейму в Телеграме;
                //- прямо в боте, то есть волонтер регистрируется в том же боте как администратор, а сообщения от пользователя перенаправляются в боте к волонтеру.
                String to_call_a_volunteer = "";
                sendMessage(chatId, to_call_a_volunteer);
            } else if (message.equals(PET_LIST.getUrl())) {
                sendMessage(chatId, petService.getPets());
            } else {
                if (message.equals(getBotToken())) {
                    // при отправке токена бота пользователь становится администратором
                    adminService.setRole(update.getMessage());
                    sendMessage(chatId, "Поздравляем вы стали админом");
                    sendButton(chatId, adminService.checkAdmin(chatId));
                } else if (adminService.checkAdmin(chatId)) {
                    // отслеживание статуса пользователя
                    switch (userStatusService.getUserStatus(chatId)) {
                        // создание списка кнопок всплывающих администратору при отправке сообщения
                        case "null" -> sendButton(chatId, adminService.checkAdmin(chatId));
                        // действия бота если пользователь в статусе просмотра контактной информации
                        case "view_contact_information" -> {
                            // выход в главное меню
                            if (message.equals("exit")) {
                                userStatusService.changeUserStatus(chatId, "null");
                                sendButton(chatId, adminService.checkAdmin(chatId));
                            } else {
                                // проверка введенных данных пользователя
                                if (isNumeric(message)) {
                                    sendMessage(chatId, contactInformationService.deleteContactInformationById(Long.parseLong(message)) + "\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit");
                                } else {
                                    sendMessage(chatId, "Ввели не id, введите id обратной связи или exit для перехода ко всем командам");
                                }
                            }
                        }
                    }
                } else {
                    sendButton(chatId, adminService.checkAdmin(chatId));
                }
            }
        }  else if (update.hasCallbackQuery()) {
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();
            if(adminService.checkAdmin(chatId)){
                switch (callBackData) {
                    case "pet_add" -> {
                        String newMessage = "pet_add";// поменять на метод в сервисе
                        editMessage(chatId, messageId, newMessage);
                    }
                    case "view_contact_information" -> {
                        // получение списка обратной связи
                        StringBuilder newMessage = new StringBuilder();
                        if(contactInformationService.getAllContactInformation().equals("[]")){
                            newMessage.append("Пока никто не оставлял заявок на обратную связь.");
                            // изменение статуса пользователя
                            userStatusService.changeUserStatus(chatId,"null");
                            sendButton(chatId, adminService.checkAdmin(chatId));
                        } else {
                            newMessage.append(contactInformationService.getAllContactInformation())
                                    .append("\n")
                                    .append("Для удаления обратной связи введите ее id, для перехода ко всем командам exit");
                            // изменение статуса пользователя
                            userStatusService.changeUserStatus(chatId,"view_contact_information");
                        }
                        editMessage(chatId, messageId, newMessage.toString());
                    }
                }
            } else {
                switch (callBackData) {
                    case "pet_report" -> {
                        String newMessage = "pet_report";// поменять на метод в сервисе
                        userStatusService.changeUserStatus(chatId,"pet_report");
                        editMessage(chatId, messageId, newMessage);
                    }
                    case "contact_information_add" -> {
                        String newMessage = "contact_information_add";// поменять на метод в сервисе
                        editMessage(chatId, messageId, newMessage);
                    }
                }
            }
        }
    }
    // метод для отправки сообщения ботом
    private void sendMessage(long chatId, String textToSend){
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
    private void sendButton(long chatId, boolean role){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Что делаем дальше?");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        // проверка роли пользователя
        if (role){
            markupInLine.setKeyboard(createButtonService.createButtonToMainMenuAdmin());
        } else {
            markupInLine.setKeyboard(createButtonService.createButtonToUser());
        }
        message.setReplyMarkup(markupInLine);
        log.info(String.valueOf(message));
        try {
            execute(message);
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
