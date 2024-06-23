package pro.sky.animal_shelter.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.controller.CallController;
import pro.sky.animal_shelter.controller.TelegramBot;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.AdminButtonMenuEnum.*;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;
import static pro.sky.animal_shelter.enums.UserButtonEnum.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;
import static pro.sky.animal_shelter.enums.PetButtonEnum.*;

@Service
public class ButtonService {
    private final CallRepository callRepository;
    private final AdminService adminService;
    private final ContactInformationService contactInformationService;
    private final UserStatusService userStatusService;
    private final PetService petService;
    private final MessageUtils messageUtils;
    private final CallService callService;
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public ButtonService(CallRepository callRepository, AdminService adminService, ContactInformationService contactInformationService, UserStatusService userStatusService, PetService petService, MessageUtils messageUtils, CallService callService) {
        this.callRepository = callRepository;
        this.adminService = adminService;
        this.contactInformationService = contactInformationService;
        this.userStatusService = userStatusService;
        this.petService = petService;
        this.messageUtils = messageUtils;
        this.callService = callService;
    }

    /**
     * Метод распределения генерации кнопок от статуса пользователя администратор или пользователь
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает список сообщений содержащий кнопок (марк ап), которые надо отправить пользователю
     */
    public List<SendMessage> defineCommand(Update update){
        if (adminService.checkAdmin(update.getCallbackQuery().getMessage().getChatId())){
            return ifAdmin(update);
        } else {
            return ifUser(update);
        }
    }
    private List<SendMessage> ifAdmin(Update update){
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getFrom().getId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        if(callBackData.equals(VIEW_CONTACT_INFORMATION_COMMAND.getCommand())){
            StringBuilder newMessage = new StringBuilder();
            if(contactInformationService.getAllContactInformation().isEmpty()){
                newMessage.append("Пока никто не оставлял заявок на обратную связь.");
                userStatusService.changeUserStatus(chatId,NO_STATUS.getStatus());
                sendMessageList.add(messageUtils.generateSendMessage(chatId, newMessage.toString()));
            } else {
                userStatusService.changeUserStatus(chatId,VIEW_CONTACT_INFORMATION.getStatus());
                for (ContactInformation contactInformation : contactInformationService.getAllContactInformation()){
                    newMessage.append(contactInformation.toString())
                            .append("\n");
                }
                newMessage.append("Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                sendMessageList.add(messageUtils.generateSendMessage(chatId, newMessage.toString()));
            }
        } else if (callBackData.equals(PET_ADD_COMMAND.getCommand())) {
            userStatusService.changeUserStatus(chatId,PET_ADD.getStatus());
            var sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Введите имя питомца.");
            sendMessageList.add(sendMessage);
        } else if (callBackData.equals(ADD_ABOUT.getCommand())){
            userStatusService.changeUserStatus(chatId,ADD_ABOUT_SHELTER_NAME.getStatus());
            var sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Введите название приюта.");
            sendMessageList.add(sendMessage);
        } else if (callBackData.equals(ADD_INFO.getCommand())){
            userStatusService.changeUserStatus(chatId,ADD_INFO_RULES.getStatus());
            var sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Введите правила как забрать животное из приюта.");
            sendMessageList.add(sendMessage);
        } else if(callBackData.equals(PET_BUTTON_PREV.getCommand())) {
            var sendMessage = new SendMessage();
            Pet petView = petService.getPet(update);
            String description = petView.getDescription();
            String petName = petView.getPetName();
            sendMessage = messageUtils.generateSendButton(chatId,"");
            sendMessage.setChatId(chatId);
            sendMessage.setText(petName + "\n" + description);
            sendMessageList.add(sendMessage);
        } else if(callBackData.equals(PET_BUTTON_NEXT.getCommand())) {
            var sendMessage = new SendMessage();
            Pet petView = petService.getPet(update);
            String description = petView.getDescription();
            String petName = petView.getPetName();
            sendMessage = messageUtils.generateSendButton(chatId,"");
            sendMessage.setChatId(chatId);
            sendMessage.setText(petName + "\n" + description);
            sendMessageList.add(sendMessage);
        } else if(callBackData.equals("close_call")){
            var sendMessage = new SendMessage();
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            sendMessage.setChatId(chatId);
            sendMessage.setText("Чат с пользователем был закрыт");
            sendMessageList.add(sendMessage);
            Call call = callRepository.findByAdminChatId(chatId);
            userStatusService.changeUserStatus(call.getUserChatId().getChatId(), NO_STATUS.getStatus());
            sendMessage.setChatId(call.getUserChatId().getChatId());
            sendMessage.setText("Чат был закрыт для нового обращения /to_call_a_volunteer");
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }
    private List<SendMessage> ifUser(Update update){
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getFrom().getId();
        List<SendMessage> sendMessageList = new ArrayList<>();
        var sendMessage = new SendMessage();
        // реакции на кнопки пользователя
        if(callBackData.equals(PET_REPORT.getCommand())){
            // переключить статус пользователя
            userStatusService.changeUserStatus(chatId,"pet_report");
            String newMessage = petService.getPetForm();
            sendMessageList.add(messageUtils.generateSendMessage(chatId, newMessage));
            //messageUtils.generateEditMessage(update, newMessage.toString());
        } else if (callBackData.equals(CONTACT_INFORMATION_ADD.getCommand())) {
            // переключить статус пользователя
            userStatusService.changeUserStatus(chatId, GET_CONTACT_INFORMATION.getStatus());
            // присылает в каком виде надо отсылать контактную информацию
            sendMessageList.add(messageUtils.generateSendMessage(chatId, contactInformationService.getContactInformation()));
        } else if(callBackData.equals(VIEW_PETS.getCommand())){
            Pet petView = petService.getPet(update);
            sendMessage = messageUtils.generateSendButton(chatId,"");
            if(petView == null){
                sendMessage.setChatId(chatId);
                sendMessage.setText("Питомцы все разобраны");
                sendMessageList.add(sendMessage);
            } else {
                String description = petView.getDescription();
                String petName = petView.getPetName();
                userStatusService.changeUserStatus(update, VIEW_PET_LIST.getStatus());
                if(petService.getPetImages(update).size() > 1){
                    SendMediaGroup sendMediaGroups = messageUtils.sendMediaGroup(update.getMessage().getChatId(), petService.getPetImages(update));
                    setView(sendMediaGroups);
                } else {
                    SendPhoto sendPhoto = messageUtils.sendPhoto(update.getMessage().getChatId(),petService.getPetImages(update).get(0));
                    setView(sendPhoto);
                }
                sendMessage.setChatId(chatId);
                sendMessage.setText(petName + "\n" + description);
                sendMessageList.add(sendMessage);
            }
        }
        else if(callBackData.equals(PET_BUTTON_PREV.getCommand())) {
            Pet petView = petService.getPet(update);
            String description = petView.getDescription();
            String petName = petView.getPetName();
            sendMessage = messageUtils.generateSendButton(chatId,"");
            sendMessage.setChatId(chatId);
            sendMessage.setText(petName + "\n" + description);
            sendMessageList.add(sendMessage);
        } else if(callBackData.equals(PET_BUTTON_NEXT.getCommand())) {
            Pet petView = petService.getPet(update);
            String description = petView.getDescription();
            String petName = petView.getPetName();
            sendMessage = messageUtils.generateSendButton(chatId, "");
            sendMessage.setChatId(chatId);
            sendMessage.setText(petName + "\n" + description);
            sendMessageList.add(sendMessage);
        }
        else if (callBackData.equals(CALL_TO.getCommand())) {
            System.out.println(callVolunteer(update)==0);
            if(callVolunteer(update) == 0){
                sendMessageList.add(messageUtils.generateSendMessage(chatId,
                        "Пока что все волонтеры заняты"));
            } else {
                sendMessageList.add(messageUtils.generateSendMessage(chatId,
                        "Пишите сообщения боту он их перенаправит первому освободившемуся волонтеру"));
                sendMessage.setChatId(callVolunteer(update));
                sendMessage.setText("Ожидаем сообщение от");
                sendMessageList.add(sendMessage);
                var sendButton = messageUtils.generateSendButton(chatId,"Закончить разговор");
                sendMessageList.add(sendButton);
            }
        } else if(callBackData.equals("close_call")){
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            sendMessage.setChatId(chatId);
            sendMessage.setText("Чат с пользователем был закрыт");
            sendMessageList.add(sendMessage);
            Call call = callRepository.findByUserChatId(chatId);
            userStatusService.changeUserStatus(call.getAdminChatId().getChatId(), NO_STATUS.getStatus());
            sendMessage.setChatId(call.getAdminChatId().getChatId());
            sendMessage.setText("Чат был закрыт для нового обращения /to_call_a_volunteer");
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }
    public Long callVolunteer(Update update){
        return callService.createCall(update);
    }
    public void setView(SendMediaGroup sendMediaGroup) {
        telegramBot.sendAnswerMessage(sendMediaGroup);
    }
    public void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
}
