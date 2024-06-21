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

    /**
     * Метод определяющий регистрацию пользователя как администратора,
     * и действия при статусе пользователя или администратора
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает список сообщений которые надо отправить пользователю
     */
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

    /**
     * Метод распределения действий при определенных статусах администратора
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает список сообщений которые надо отправить пользователю
     */
    private List<SendMessage> ifAdmin(Update update){
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        String userStatus = userStatusService.getUserStatus(chatId);
        if(userStatus.equals(VIEW_CONTACT_INFORMATION.getStatus())){
            sendMessageList.add(adminStatusViewContactInformation(update));
        } else if (userStatus.equals(CALL.getStatus())) {
            sendMessageList = adminCall(update);
        } else if (userStatus.equals(PET_ADD.getStatus())) {
            sendMessageList.add(adminAddPetName(update));
        } else if (userStatus.equals(PET_ADD_IMG.getStatus())) {
            sendMessageList = adminAddPetDescription(update);
        } else if (userStatus.equals(ADD_ABOUT_SHELTER_NAME.getStatus())) {
            sendMessageList.add(adminAddShelterName(update));
        } else if (userStatus.equals(ADD_ABOUT_SCHEDULE.getStatus())) {
            sendMessageList.add(adminAddSchedule(update));
        } else if (userStatus.equals(ADD_ABOUT_SECURITY_CONTACTS.getStatus())) {
            sendMessageList.add(adminAddSecurityContacts(update));
        } else if (userStatus.equals(ADD_INFO_RULES.getStatus())) {
            sendMessageList.add(adminAddRules(update));
        } else if (userStatus.equals(ADD_INFO_DOCUMENTS.getStatus())) {
            sendMessageList.add(adminAddDocuments(update));
        } else if (userStatus.equals(ADD_INFO_TRANSPORTATION.getStatus())) {
            sendMessageList.add(adminAddTransportation(update));
        } else {
            sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
        }
        return sendMessageList;
    }

    /**
     * Метод вызывает метод для сохранения информации по транспортировке животного,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddTransportation(Update update) {
        SendMessage sendMessage = new SendMessage();
        infoService.addTransportation(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Как транспортировать животное успешно сохранено");
        userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        return sendMessage;
    }

    /**
     * Метод вызывает метод для сохранения информации по необходимым документам для усыновления животного,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */

    private SendMessage adminAddDocuments(Update update) {
        SendMessage sendMessage = new SendMessage();
        infoService.addDocuments(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Список документов успешно сохранен. Добавьте описание как можно транспортировать животное.");
        userStatusService.changeUserStatus(update,ADD_INFO_TRANSPORTATION.getStatus());
        return sendMessage;
    }
    /**
     * Метод вызывает метод для сохранения информации о правилах усыновления животного,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddRules(Update update) {
        SendMessage sendMessage = new SendMessage();
        infoService.addRules(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Контакты успешно сохранены. Добавьте описание какие документы нужны.");
        userStatusService.changeUserStatus(update,ADD_INFO_DOCUMENTS.getStatus());
        return sendMessage;
    }
    /**
     * Метод вызывает метод для сохранения информации об контактах охраны,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddSecurityContacts(Update update) {
        SendMessage sendMessage = new SendMessage();
        aboutService.addSecurityContacts(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Контакты охраны успешно сохранены");
        userStatusService.changeUserStatus(update,NO_STATUS.getStatus());
        return sendMessage;
    }
    /**
     * Метод вызывает метод для сохранения информации о графике работы приюта,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddSchedule(Update update) {
        SendMessage sendMessage = new SendMessage();
        aboutService.addSchedule(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("График работы приюта сохранен. Введите контакты охраны.");
        userStatusService.changeUserStatus(update,ADD_ABOUT_SECURITY_CONTACTS.getStatus());
        return sendMessage;
    }
    /**
     * Метод вызывает метод для сохранения информации о названии приюта,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddShelterName(Update update) {
        SendMessage sendMessage = new SendMessage();
        aboutService.addShelterName(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Название приюта сохранено. Введите график работы приюта.");
        userStatusService.changeUserStatus(update,ADD_ABOUT_SCHEDULE.getStatus());
        return sendMessage;
    }
    /**
     * Метод вызывает метод для сохранения описания животного,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private List<SendMessage> adminAddPetDescription(Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        petService.addPetDescription(chatId,message);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Успешно сохранен новый питомец");
        List<SendMessage> sendMessageList = new ArrayList<>();
        sendMessageList.add(sendMessage);
        sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
        userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        return sendMessageList;
    }
    /**
     * Метод вызывает метод для сохранения имени животного,
     * поступающей от администратора, в базу данных.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает сгенерированное сообщение для пользователя
     */
    private SendMessage adminAddPetName(Update update) {
        SendMessage sendMessage = new SendMessage();
        petService.addPetName(update.getMessage().getChatId(),update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Загрузите изображение и потом добавьте описание животного");
        return sendMessage;
    }
    /**
     * Метод вызывает метод определения с кем сейчас разговаривает администратор и отправки
     * сообщений тому с кем общается администратор.
     * Генерирует сообщение для администратора, и переключает статус для дальнейших действий.
     * @param update объект параметров запроса из телеграм бота
     * @return список администратору сообщений пользователю
     */
    private List<SendMessage> adminCall(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(callController.sendMessageToChat(update.getMessage().getChatId()));
        sendMessage.setText(update.getMessage().getText());
        List<SendMessage> sendMessageList = new ArrayList<>();
        sendMessageList.add(sendMessage);
        if(urlService.callVolunteer(update) != 0L){
            sendMessageList.add(messageUtils.generateSendButton(urlService.callVolunteer(update), "Закончить разговор"));
        }
        return sendMessageList;
    }
    /**
     * Метод распределения действий при определенных статусах пользователя
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает список сообщений которые надо отправить пользователю
     */
    private List<SendMessage> ifUser(Update update){
        long chatId = update.getMessage().getChatId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        String userStatus = userStatusService.getUserStatus(chatId);
        if (userStatus.equals(NO_STATUS.getStatus())) {
            sendMessageList.add(userNoStatus(update));
        } else if (userStatus.equals(GET_PET_FORM.getStatus())) {
            sendMessageList.add(userGetPetFormStatus(update));
        } else if (userStatus.equals(GET_CONTACT_INFORMATION.getStatus())) {
            sendMessageList.add(userGetContactInformationStatus(update));
        } else if (userStatus.equals(ADD_PHONE.getStatus())) {
            sendMessageList.add(userAddPhoneStatus(update));
        } else if (userStatus.equals(ADD_PET_REPORT_TEXT.getStatus())) {
            sendMessageList.add(userAddPetReportTextStatus(update));
        } else if (userStatus.equals(CALL_A_VOLUNTEER.getStatus())) {
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

    /**
     * Метод генерирует сообщение для пользователя в статусе NoStatus
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userNoStatus(Update update){
        return messageUtils.generateSendButton(update.getMessage().getChatId(), "Для чего вы отправили это сообщение?");
    }
    /**
     * Метод генерирует сообщение для пользователя в статусе GetPetFormStatus
     * меняет статус пользователя на ADD_PET_REPORT_IMG
     * получает сообщение из метода petService.getPetForm()
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userGetPetFormStatus(Update update){
        userStatusService.changeUserStatus(update, ADD_PET_REPORT_IMG.getStatus());
        return messageUtils.generateSendMessage(update,petService.getPetForm());
    }
    /**
     * Проверяет соответствие введенного номера паттерну, генерирует сообщение пользователю,
     * если верно то переключает статус и выводит сообщение пользователю,
     * если не соответствует паттерну, то выдает сообщение о некорректном вводе и напоминает о форме ввода
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userGetContactInformationStatus(Update update){
        if (contactInformationService.addContactPhone(update)) {
            userStatusService.changeUserStatus(update, ADD_PHONE.getStatus());
            return messageUtils.generateSendMessage(update,"Введите как к Вам обращаться.");
        } else {
            return messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation());
        }
    }
    /**
     * Метод генерирует сообщение для пользователя в статусе AddPhone
     * сохраняет сообщение, которое прислал пользователь в качестве имени, и переключает статус на NO_STATUS.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userAddPhoneStatus(Update update){
        if(contactInformationService.addContactName(update)){
            userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
            return messageUtils.generateSendButton(update.getMessage().getChatId(), "Ваши данные сохранены. Скоро с Вами свяжутся. Чем я еще могу помочь?");
        } else  {
            return messageUtils.generateSendMessage(update,"Не корректно введены данные.\n" + contactInformationService.getContactInformation());
        }
    }
    /**
     * Метод вызывает метод для сохранения текстовой информации отчета по-животному
     * и переключает статус на NO_STATUS.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userAddPetReportTextStatus(Update update){
        reportService.addTextReport(update);
        userStatusService.changeUserStatus(update, NO_STATUS.getStatus());
        return messageUtils.generateSendMessage(update,"Отчет успешно отправлен");
    }

    /**
     * Проверяет с каким администратором общается пользователь и отправляет сообщение администратору
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage userCallToVolunteerStatus(Update update){
        return messageUtils.generateSendMessage(callController.sendMessageToChat(update.getMessage().getChatId()),update.getMessage().getText());
    }
    /**
     * Метод проверяет, что прислал администратор число или нет, если нет то выдает сообщение, что надо прислать число,
     * если число, то проверяет существование контактной информации с таким id в базе данных и удаляет.
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект сообщения пользователю
     */
    private SendMessage adminStatusViewContactInformation(Update update){
        if (isNumeric(update.getMessage().getText())) {
            return messageUtils.generateSendMessage(update,contactInformationService.deleteContactInformationById(Long.parseLong(update.getMessage().getText())) + "\n" +
                    "Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
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
