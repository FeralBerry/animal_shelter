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

// Slf4j - аннотация для использования логов из библиотеки lombok и авто подключения сервисов в конструктор
@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private AboutService aboutService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private PetService petService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private StartService startService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CreateButtonService createButtonService;
    // добавочное сообщение в конце
    private final String backMsg =  "Если хотите чтобы с Вами связались нажмите на ссылку или выберете пункт в меню /contact_information \n" +
                                    "Если хотите связаться с волонтером нажмите на ссылку или выберете пункт в меню /to_call_a_volunteer";
    private final BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
        // создаем список команд для меню
        List<BotCommand> botCommandList = new ArrayList<>();
        // добавление кнопок меню
        botCommandList.add(new BotCommand("/start","get welcome message"));
        botCommandList.add(new BotCommand("/about","find out information about the nursery"));
        botCommandList.add(new BotCommand("/info","information about animals and rules"));
        botCommandList.add(new BotCommand("/pet_report_form","animal report form"));
        botCommandList.add(new BotCommand("/to_call_a_volunteer","call a volunteer"));
        botCommandList.add(new BotCommand("/contact_information","feedback"));
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
            if(message.equals("/start")){
                // формируем сообщение из startService
                helloMsg.append(startService.start(update.getMessage()));
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            }
            else if (message.equals("/about")) {
                // формируем сообщение из aboutService и стандартного сообщения о связи
                helloMsg.append(aboutService.about())
                        .append(backMsg);
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            }
            else if (message.equals("/info")) {
                // формируем сообщение из infoService и стандартного сообщения о связи
                helloMsg.append(infoService.info())
                        .append(backMsg);
                // отправляем сообщение пользователю
                sendMessage(chatId, helloMsg.toString());
            }
            else if (message.equals("/pet_list")) {
                // должен обрабатывать метод сервиса
                // выводит список животных из БД
                String pet_list = "";
                sendMessage(chatId, pet_list);
            }
            else if (message.equals("/to_call_a_volunteer")) {
                // должен обрабатывать метод сервиса
                // Вызов волонтера осуществляется одним из следующих способов (на выбор разработчика):
                //- по номеру телефона;
                //- по никнейму в Телеграме;
                //- прямо в боте, то есть волонтер регистрируется в том же боте как администратор, а сообщения от пользователя перенаправляются в боте к волонтеру.
                String to_call_a_volunteer = "";
                sendMessage(chatId, to_call_a_volunteer);
            }
            else if (message.equals("/contact_information")) {
                // должен обрабатывать метод сервиса
                // Выдавать сообщение с типом как написать данные +7-9**-***-**-** ФИО.
                String contact_information = "";
                sendMessage(chatId,contact_information);
            }
            else if (message.equals("/contact_information_add")) {
                // должен обрабатывать метод сервиса
                // записать информацию для контакта /contact-information-add
                // проверять правильность по патерну +7-9**-***-**-** ФИО. Если правильно записывать в БД возвращать сообщение
                // пользователю, что в ближайшее время с ним свяжутся.
                // отправлять уведомление волонтеру
                String contact_information = "";
                sendMessage(chatId,contact_information);
            }
            else if (message.equals("/pet_report_form")) {
                // должен обрабатывать метод сервиса
                // присылает форму отчета
                String pet_report_form = "";
                sendMessage(chatId, pet_report_form);
            }
            else {
                if (message.equals(getBotToken())){
                    adminService.setRole(update.getMessage());
                    sendMessage(chatId, "Поздравляем вы стали админом");
                } else if(adminService.checkAdmin(chatId)){
                    sendButton(chatId,"admin");
                } else {
                    sendButton(chatId,"user");
                }
            }
        }  else if (update.hasCallbackQuery()) {
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();
            if(callBackData.equals("pet_report")){
                String newMessage = "pet_report";// поменять на метод в сервисе
                editMessage(chatId, messageId, newMessage);
            } else if (callBackData.equals("contact_information_add")) {
                String newMessage = "contact_information_add";// поменять на метод в сервисе
                editMessage(chatId, messageId, newMessage);
            } else if (callBackData.equals("pet_list_add")) {
                String newMessage = "pet_list_add";// поменять на метод в сервисе
                editMessage(chatId, messageId, newMessage);
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
    private void sendButton(long chatId,String role){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Что с этим сделать?");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        if (role.equals("user")){
            markupInLine.setKeyboard(createButtonService.createButtonToUser());
        }
        if (role.equals("admin")){
            markupInLine.setKeyboard(createButtonService.createButtonToAdmin());
        }
        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
