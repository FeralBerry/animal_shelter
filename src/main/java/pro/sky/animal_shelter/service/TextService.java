package pro.sky.animal_shelter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.controller.CallController;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.UserSatausEnum.*;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;

@Service
public class TextService {
    private final MessageUtils messageUtils;
    private final AdminService adminService;
    private final UserStatusService userStatusService;
    private final ContactInformationService contactInformationService;
    private final CallController callController;
    private final UrlService urlService;
    private final PetService petService;
    private final AboutService aboutService;
    private final InfoService infoService;
    private final ReportService reportService;
    @Value("${telegram.bot.token}")
    private String token;

    public TextService(MessageUtils messageUtils, AdminService adminService, UserStatusService userStatusService, ContactInformationService contactInformationService, CallController callController, UrlService urlService, PetService petService, AboutService aboutService, InfoService infoService, ReportService reportService) {
        this.messageUtils = messageUtils;
        this.adminService = adminService;
        this.userStatusService = userStatusService;
        this.contactInformationService = contactInformationService;
        this.callController = callController;
        this.urlService = urlService;
        this.petService = petService;
        this.aboutService = aboutService;
        this.infoService = infoService;
        this.reportService = reportService;
    }

    public List<SendMessage> defineMethod(Update update){
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        if (message.equals(token)) {
            // при отправке токена бота пользователь становится администратором
            adminService.setRole(update.getMessage());
            sendMessageList.add(messageUtils.generateSendMessage(update,"Поздравляем вы стали админом"));
            // переключить статус пользователя
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            sendMessageList.add(messageUtils.generateSendButton(chatId,"Главное меню администратора."));
            return sendMessageList;
        } else if (adminService.checkAdmin(chatId)) {
            return ifAdmin(update);
        } else {
            return ifUser(update);
        }
    }
    private List<SendMessage> ifAdmin(Update update){
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        var sendMessage = new SendMessage();
        if(userStatusService.getUserStatus(chatId).equals(VIEW_CONTACT_INFORMATION.getStatus())){
            if (isNumeric(message)) {
                sendMessageList.add(messageUtils.generateSendMessage(update,contactInformationService.deleteContactInformationById(Long.parseLong(message))));
                sendMessageList.add(messageUtils.generateSendMessage(update,"Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start"));
            } else if (message.equals("exit")) {
                sendMessageList.add(messageUtils.generateSendButton(chatId,"Главное меню администратора."));
            } else {
                sendMessageList.add(messageUtils.generateSendButton(chatId,"Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start"));
            }
        } else if (userStatusService.getUserStatus(chatId).equals(CALL.getStatus())) {
            sendMessage.setChatId(callController.sendMessageToChat(chatId));
            sendMessage.setText(message);
            sendMessageList.add(sendMessage);
            sendMessageList.add(messageUtils.generateSendButton(urlService.callVolunteer(update), "Закончить разговор"));
        } else if (userStatusService.getUserStatus(chatId).equals(PET_ADD.getStatus())) {
            petService.addPetName(chatId,message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Загрузите изображение и потом добавьте описание животного");
            sendMessageList.add(sendMessage);
        } else if (userStatusService.getUserStatus(chatId).equals(PET_ADD_IMG.getStatus())) {
            petService.addPetDescription(chatId,message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Успешно сохранен новый питомец");
            sendMessageList.add(sendMessage);
            sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SHELTER_NAME.getStatus())) {
            aboutService.addShelterName(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Название приюта сохранено. Введите график работы приюта.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId,ADD_ABOUT_SCHEDULE.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SCHEDULE.getStatus())) {
            aboutService.addSchedule(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("График работы приюта сохранен. Введите контакты охраны.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId,ADD_ABOUT_SECURITY_CONTACTS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SECURITY_CONTACTS.getStatus())) {
            aboutService.addSecurityContacts(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Контакты охраны успешно сохранены");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId,NO_STATUS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_RULES.getStatus())) {
            infoService.addRules(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Контакты успешно сохранены. Добавьте описание какие документы нужны.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId,ADD_INFO_DOCUMENTS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_DOCUMENTS.getStatus())) {
            infoService.addDocuments(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Список документов успешно сохранен. Добавьте описание как можно транспортировать животное.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId,ADD_INFO_TRANSPORTATION.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_TRANSPORTATION.getStatus())) {
            infoService.addTransportation(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Как транспортировать животное успешно сохранено");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        } else {
            sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
        }
        return sendMessageList;
    }
    private List<SendMessage> ifUser(Update update){
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        if (userStatusService.getUserStatus(chatId).equals(NO_STATUS.getStatus())) {
            sendMessageList.add(messageUtils.generateSendButton(chatId, "Для чего вы отправили это сообщение?"));
        } else if (userStatusService.getUserStatus(chatId).equals(GET_PET_FORM.getStatus())) {
            sendMessageList.add(messageUtils.generateSendMessage(update,petService.getPetForm()));
            userStatusService.changeUserStatus(chatId, ADD_PET_REPORT_IMG.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(GET_CONTACT_INFORMATION.getStatus())) {
            if (contactInformationService.addContactPhone(message,chatId)) {
                userStatusService.changeUserStatus(chatId, ADD_PHONE.getStatus());
                sendMessageList.add(messageUtils.generateSendMessage(update,"Введите как к Вам обращаться."));
            } else {
                sendMessageList.add(messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation()));
            }
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PHONE.getStatus())) {
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            if(contactInformationService.addContactName(message,chatId)){
                sendMessageList.add(messageUtils.generateSendButton(chatId, "Ваши данные сохранены. Скоро с Вами свяжутся. Чем я еще могу помочь?"));
            } else  {
                sendMessageList.add(messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation()));
            }
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PET_REPORT_TEXT.getStatus())) {
            reportService.addTextReport(update);
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(CALL_A_VOLUNTEER.getStatus())) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(callController.sendMessageToChat(chatId));
            sendMessage.setText(message);
            sendMessageList.add(sendMessage);
        } else if (userStatusService.getUserStatus(chatId).equals(VIEW_PET_LIST.getStatus())) {
            // реакция на текстовое сообщение боту если пользователь в статусе просмотра питомцев
        }
        return sendMessageList;
    }
    /**
     * Метод проверки строки является ли она Long
     * @param str строка для парсинга
     * @return возвращает значение true, если строка является числом Long, false, если строка не является числом Long
     */
    private static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
