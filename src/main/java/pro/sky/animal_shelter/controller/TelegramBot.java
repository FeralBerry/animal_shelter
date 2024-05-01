package pro.sky.animal_shelter.controller;


import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.sky.animal_shelter.configuration.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Slf4j - аннотация для использования логов из библиотеки lombok
@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
        // создаем список команд для меню
        List<BotCommand> botCommandList = new ArrayList<>();
        // добавление кнопок меню
        botCommandList.add(new BotCommand("/start","get welcome message"));
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
        // проверяем есть ли отправленные сообщения
        if(update.hasMessage() && update.getMessage().hasText()){
            // получаем отправленное сообщение
            String message = update.getMessage().getText();
            // получаем id чата
            long chatId = update.getMessage().getChatId();
            // сверяем полученное сообщение и выполняем команду
            if(message.equals("/start")){
                // должен обрабатывать метод сервиса
                // регистрировать нового пользователя и возвращать информацию с командами
                // - Узнать информацию о приюте (этап 1) /about
                //- Как взять животное из приюта (этап 2) /info
                //- Прислать отчет о питомце (этап 3) /pet-report
                //- Позвать волонтера /to-call-a-volunteer
                String start = "";
                sendMessage(chatId, start);
            }
            else if (message.equals("/about")) {
                // должен обрабатывать метод сервиса
                // - Бот приветствует пользователя.
                //- Бот может рассказать о приюте.
                //- Бот может выдать расписание работы приюта, адрес и схему проезда.
                //- Бот может выдать контактные данные охраны для оформления пропуска на машину.
                //- Бот может выдать общие рекомендации о технике безопасности на территории приюта.
                //- Бот может принять и записать контактные данные для связи. /contact-information
                //- Если бот не может ответить на вопросы клиента, то можно воспользоваться опцией взаимодействия с волонтером. /to-call-a-volunteer
                String about = "";
                sendMessage(chatId, about);
            }
            else if (message.equals("/info")) {
                // должен обрабатывать метод сервиса
                // - Бот приветствует пользователя.
                //- Бот может выдать список животных для усыновления (возможность перейти). /pet-list
                //- Бот может выдать правила знакомства с животным до того, как забрать его из приюта.
                //- Бот может выдать список документов, необходимых для того, чтобы взять животное из приюта.
                //- Бот может выдать список рекомендаций по транспортировке животного.
                //- Бот может выдать список рекомендаций по обустройству дома для щенка.
                //- Бот может выдать список рекомендаций по обустройству дома для взрослого животного.
                //- Бот может выдать список рекомендаций по обустройству дома для животного с ограниченными возможностями (зрение, передвижение).
                //- Бот может выдать советы кинолога по первичному общению с собакой*.*
                //- Бот может выдать рекомендации по проверенным кинологам для дальнейшего обращения к ним*.*
                //- Бот может выдать список причин, почему могут отказать и не дать забрать собаку из приюта.
                //- Бот может принять и записать контактные данные для связи. /contact-information
                //- Если бот не может ответить на вопросы клиента, то можно позвать волонтера. /to-call-a-volunteer
                String info_text = "";
                sendMessage(chatId, info_text);
            }
            else if (message.equals("/pet-list")) {
                // должен обрабатывать метод сервиса
                // выводит список животных из БД
                String pet_list = "";
                sendMessage(chatId, pet_list);
            }
            else if (message.equals("/pet-list-add")) {
                // должен обрабатывать метод сервиса
                // добавлять список животных, проверять есть ли на это права
                String pet_list_add = "";
                sendMessage(chatId, pet_list_add);
            }
            else if (message.equals("/to-call-a-volunteer")) {
                // должен обрабатывать метод сервиса
                // Вызов волонтера осуществляется одним из следующих способов (на выбор разработчика):
                //- по номеру телефона;
                //- по никнейму в Телеграме;
                //- прямо в боте, то есть волонтер регистрируется в том же боте как администратор, а сообщения от пользователя перенаправляются в боте к волонтеру.
                String to_call_a_volunteer = "";
                sendMessage(chatId, to_call_a_volunteer);
            }
            else if (message.equals("/contact-information")) {
                // должен обрабатывать метод сервиса
                // Выдавать сообщение с типом как написать данные +7-9**-***-**-** ФИО.
                String contact_information = "";
                sendMessage(chatId,contact_information);
            }
            else if (message.equals("/contact-information-add")) {
                // должен обрабатывать метод сервиса
                // записать информацию для контакта /contact-information-add
                // проверять правильность по патерну +7-9**-***-**-** ФИО. Если правильно записывать в БД возвращать сообщение
                // пользователю, что в ближайшее время с ним свяжутся.
                String contact_information = "";
                sendMessage(chatId,contact_information);
            }
            else if (message.equals("/pet-report-form")) {
                // должен обрабатывать метод сервиса
                // присылает форму отчета
                String pet_report_form = "";
                sendMessage(chatId, pet_report_form);
            }
            else if (message.equals("/pet-report")) {
                // должен обрабатывать метод сервиса
                // отправить отчет /pet-report
                // проверять содержит ли изображение и содержит ли текст, если все есть, то сохранять в БД
                String pet_report_form = "";
                sendMessage(chatId, pet_report_form);
            }
            else {
                // должен обрабатывать метод сервиса
                // при отправке сообщения выдавать кнопки с сообщением что сделать
                sendMessage(chatId, "Что с этим сделать?");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
                keyboardButtonsRow.add(createButton("Отправить отчет","/pet-report"));
                keyboardButtonsRow.add(createButton("Записать контактные данные","/contact-information-add"));
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                SendMessage createButtons = new SendMessage();
                createButtons.setChatId(chatId);
                createButtons.setReplyMarkup(inlineKeyboardMarkup);
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
    private InlineKeyboardButton createButton(String buttonText, String buttonCommand){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }
}
