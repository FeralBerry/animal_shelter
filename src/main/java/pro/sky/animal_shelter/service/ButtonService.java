package pro.sky.animal_shelter.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.PetsImg;
import pro.sky.animal_shelter.model.Repositories.PetsImgRepository;
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
    private final AdminService adminService;
    private final ContactInformationService contactInformationService;
    private final UserStatusService userStatusService;
    private final PetService petService;
    private final MessageUtils messageUtils;
    private final PetsImgRepository petsImgRepository;

    public ButtonService(AdminService adminService, ContactInformationService contactInformationService, UserStatusService userStatusService, PetService petService, MessageUtils messageUtils, PetsImgRepository petsImgRepository) {
        this.adminService = adminService;
        this.contactInformationService = contactInformationService;
        this.userStatusService = userStatusService;
        this.petService = petService;
        this.messageUtils = messageUtils;
        this.petsImgRepository = petsImgRepository;
    }

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
                sendMessageList.add(messageUtils.generateSendMessage(update, "Главное меню администратора."));
            } else {
                for (ContactInformation contactInformation : contactInformationService.getAllContactInformation()){
                    newMessage.append(contactInformation.toString())
                            .append("\n");
                }
                newMessage.append("Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                sendMessageList.add(messageUtils.generateSendMessage(update, newMessage.toString()));
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
        }
        return sendMessageList;
    }
    private List ifUser(Update update){
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getFrom().getId();
        List sendMessageList = new ArrayList<>();
        // реакции на кнопки пользователя
        if(callBackData.equals(PET_REPORT.getCommand())){
            // переключить статус пользователя
            userStatusService.changeUserStatus(chatId,"pet_report");
            String newMessage = "pet_report";// поменять на метод в сервисе
            sendMessageList.add(messageUtils.generateSendMessage(update, newMessage));
            //messageUtils.generateEditMessage(update, newMessage.toString());
        } else if (callBackData.equals(CONTACT_INFORMATION_ADD.getCommand())) {
            // переключить статус пользователя
            userStatusService.changeUserStatus(chatId, GET_CONTACT_INFORMATION.getStatus());
            // присылает в каком виде надо отсылать контактную информацию
            sendMessageList.add(messageUtils.generateSendMessage(update, contactInformationService.getContactInformation()));
        } else if(callBackData.equals(VIEW_PETS.getCommand())) {
            userStatusService.changeUserStatus(chatId, VIEW_PET_LIST.getStatus());
            Pet petView = petService.getPet(chatId);
            String description = petView.getDescription();// пока для теста выдает только имя питомца
            List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(petView.getId());
            List<String> images = new ArrayList<>();
            for (PetsImg petsImg : petImages){
                images.add(petsImg.getFileId());
            }
            if (images.size() > 1){
                sendMessageList.add(messageUtils.sendMediaGroup(chatId,images));
            } else {
                sendMessageList.add(messageUtils.sendPhoto(chatId,images.get(0)));
            }
            var sendButton = messageUtils.generateSendButton(chatId,"");
            sendMessageList.add(sendButton);
        } else if(callBackData.equals(PET_BUTTON_PREV.getCommand())) {
            Pet petView = petService.getPrevPet(chatId);
            String description = petView.getPetName();// пока для теста выдает только имя питомца
            List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(petView.getId());
            List<String> images = new ArrayList<>();
            for (PetsImg petsImg : petImages){
                images.add(petsImg.getFileId());
            }
            if (images.size() > 1){
                sendMessageList.add(messageUtils.sendMediaGroup(chatId,images));
            } else {
                sendMessageList.add(messageUtils.sendPhoto(chatId,images.get(0)));
            }
            var sendButton = messageUtils.generateSendButton(chatId,"");
            sendMessageList.add(sendButton);
        } else if(callBackData.equals(PET_BUTTON_NEXT.getCommand())) {
            Pet petView = petService.getNextPet(chatId);
            String description = petView.getPetName();// пока для теста выдает только имя питомца
            List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(petView.getId());
            List<String> images = new ArrayList<>();
            for (PetsImg petsImg : petImages){
                images.add(petsImg.getFileId());
            }
            if (images.size() > 1){
                sendMessageList.add(messageUtils.sendMediaGroup(chatId,images));
            } else {
                sendMessageList.add(messageUtils.sendPhoto(chatId,images.get(0)));
            }
            var sendButton = messageUtils.generateSendButton(chatId,"");
            sendMessageList.add(sendButton);
        }
        return sendMessageList;
    }
}
