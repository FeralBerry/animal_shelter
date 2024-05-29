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
            adminService.setRole(update.getMessage());
            sendMessageList.add(messageUtils.generateSendMessage(update,"Поздравляем вы стали админом"));
            userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
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
            sendMessageList.add(adminStatusViewContactInformation(update));
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
            userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SHELTER_NAME.getStatus())) {
            aboutService.addShelterName(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Название приюта сохранено. Введите график работы приюта.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update,ADD_ABOUT_SCHEDULE.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SCHEDULE.getStatus())) {
            aboutService.addSchedule(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("График работы приюта сохранен. Введите контакты охраны.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update,ADD_ABOUT_SECURITY_CONTACTS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_ABOUT_SECURITY_CONTACTS.getStatus())) {
            aboutService.addSecurityContacts(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Контакты охраны успешно сохранены");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update,NO_STATUS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_RULES.getStatus())) {
            infoService.addRules(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Контакты успешно сохранены. Добавьте описание какие документы нужны.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update,ADD_INFO_DOCUMENTS.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_DOCUMENTS.getStatus())) {
            infoService.addDocuments(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Список документов успешно сохранен. Добавьте описание как можно транспортировать животное.");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update,ADD_INFO_TRANSPORTATION.getStatus());
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_INFO_TRANSPORTATION.getStatus())) {
            infoService.addTransportation(message);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Как транспортировать животное успешно сохранено");
            sendMessageList.add(sendMessage);
            userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        } else {
            sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
        }
        return sendMessageList;
    }
    private List<SendMessage> ifUser(Update update){
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        if (userStatusService.getUserStatus(chatId).equals(NO_STATUS.getStatus())) {
            sendMessageList.add(userNoStatus(update));
        } else if (userStatusService.getUserStatus(chatId).equals(GET_PET_FORM.getStatus())) {
            sendMessageList.add(userGetPetFormStatus(update));
        } else if (userStatusService.getUserStatus(chatId).equals(GET_CONTACT_INFORMATION.getStatus())) {
            sendMessageList.add(userGetContactInformationStatus(update));
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PHONE.getStatus())) {
            sendMessageList.add(userAddPhoneStatus(update));
        } else if (userStatusService.getUserStatus(chatId).equals(ADD_PET_REPORT_TEXT.getStatus())) {
            sendMessageList.add(userAddPetReportTextStatus(update));
        } else if (userStatusService.getUserStatus(chatId).equals(CALL_A_VOLUNTEER.getStatus())) {
            sendMessageList.add(userCallToVolunteerStatus(update));
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
    private SendMessage userNoStatus(Update update){
        return messageUtils.generateSendButton(update.getMessage().getChatId(), "Для чего вы отправили это сообщение?");
    }
    private SendMessage userGetPetFormStatus(Update update){
        userStatusService.changeUserStatus(update, ADD_PET_REPORT_IMG.getStatus());
        return messageUtils.generateSendMessage(update,petService.getPetForm());
    }
    private SendMessage userGetContactInformationStatus(Update update){
        if (contactInformationService.addContactPhone(update)) {
            userStatusService.changeUserStatus(update, ADD_PHONE.getStatus());
            return messageUtils.generateSendMessage(update,"Введите как к Вам обращаться.");
        } else {
            return messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation());
        }
    }
    private SendMessage userAddPhoneStatus(Update update){
        userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        if(contactInformationService.addContactName(update)){
            return messageUtils.generateSendButton(update.getMessage().getChatId(), "Ваши данные сохранены. Скоро с Вами свяжутся. Чем я еще могу помочь?");
        } else  {
            return messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation());
        }
    }
    private SendMessage userAddPetReportTextStatus(Update update){
        reportService.addTextReport(update);
        userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        return messageUtils.generateSendMessage(update,"Отчет успешно отправлен");
    }
    private SendMessage userCallToVolunteerStatus(Update update){
        return messageUtils.generateSendMessage(callController.sendMessageToChat(update.getMessage().getChatId()),update.getMessage().getText());
    }
    private SendMessage adminStatusViewContactInformation(Update update){
        if (isNumeric(update.getMessage().getText())) {
            return messageUtils.generateSendMessage(update,contactInformationService.deleteContactInformationById(Long.parseLong(update.getMessage().getText() + "\n" +
                    "Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start")));
        } else if (update.getMessage().getText().equals("exit")) {
            return messageUtils.generateSendButton(update.getMessage().getChatId(),
                    "Главное меню администратора.");
        } else {
            return messageUtils.generateSendButton(update.getMessage().getChatId(),
                    "Ввели не id или не число.\n" +
                        "Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
        }
    }
}
