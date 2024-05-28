package pro.sky.animal_shelter.service;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.BotCommandEnum.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;

@Service
public class UrlService {
    private final AdminService adminService;
    private final UserStatusService userStatusService;
    private final StartService startService;
    private final InfoService infoService;
    private final AboutService aboutService;
    private final PetService petService;
    private final ContactInformationService contactInformationService;
    private final CallService callService;
    private final MessageUtils messageUtils;
    public UrlService(AdminService adminService,
                      UserStatusService userStatusService,
                      StartService startService,
                      InfoService infoService,
                      AboutService aboutService,
                      PetService petService,
                      CallService callService,
                      ContactInformationService contactInformationService, MessageUtils messageUtils){
        this.adminService = adminService;
        this.userStatusService = userStatusService;
        this.startService = startService;
        this.infoService = infoService;
        this.aboutService = aboutService;
        this.petService = petService;
        this.contactInformationService = contactInformationService;
        this.callService = callService;
        this.messageUtils = messageUtils;
    }
    public List<SendMessage> defineACommand(Update update){
        List<SendMessage> list = new ArrayList<>();
        StringBuilder helloMsg = new StringBuilder();
        helloMsg.append("Привет ")
                .append(update.getMessage().getChat().getFirstName())
                .append("\n");
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String backMsg =  "Если хотите чтобы с Вами связались нажмите на ссылку или выберете пункт в меню /contact_information \n" +
                "Если хотите связаться с волонтером нажмите на ссылку или выберете пункт в меню /to_call_a_volunteer";
        if (message.equals(START.toString())) {
            startService.register(update.getMessage());
            if (adminService.checkAdmin(chatId)) {
                String msg = "Главное меню администратора";
                userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
                list.add(messageUtils.generateSendButton(chatId,msg));
            } else {
                String msg = startService.start();
                list.add(messageUtils.generateSendMessage(update,helloMsg.append(msg).toString()));
                userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            }
        }
        else if (message.equals(INFO.toString())) {
            list.add(messageUtils.generateSendMessage(update,
                    helloMsg.append(info(chatId)).append(backMsg).toString()));
        }
        else if (message.equals(ABOUT.toString())) {
            list.add(messageUtils.generateSendMessage(update,
                    helloMsg.append(about(chatId)).append(backMsg).toString()));
        }
        else if (message.equals(PET_REPORT_FORM.toString())) {
            list.add(messageUtils.generateSendMessage(update,
                    petReportForm(chatId)));
        }
        else if (message.equals(CONTACT_INFORMATION.toString())) {
            list.add(messageUtils.generateSendMessage(update,
                    contactInformation(chatId)));
        }
        else if (message.equals(TO_CALL_A_VOLUNTEER.toString())) {
            callVolunteer(update);
            if(callVolunteer(update) == 0){
                list.add(messageUtils.generateSendMessage(update,
                        "Пока что все волонтеры заняты"));
            } else {
                list.add(messageUtils.generateSendMessage(update,
                        "Пишите сообщения боту он их перенаправит первому освободившемуся волонтеру"));
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(callVolunteer(update));
                sendMessage.setText("Ожидаем сообщение от");
                list.add(sendMessage);
                var sendButton = messageUtils.generateSendButton(chatId,"Закончить разговор");
                list.add(sendButton);
            }
        }
        else if (message.equals(PET_LIST.toString())) {
            Pet pet = petService.getPet(chatId);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(pet.getPetName() + "\n" + pet.getDescription());
            list.add(sendMessage);
            var sendButton = messageUtils.generateSendButton(chatId,"");
            list.add(sendButton);
        }
        return list;
    }

    /**
     * @param chatId получает id чата пользователя
     * @return возвращает строку, сгенерированную в методе info из infoService
     */
    @Operation(summary = "Вывод информации о документах, правилах, чтобы забрать животное")
    public String info(long chatId){
        userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        return infoService.info();
    }

    /**
     * @param chatId получает id чата пользователя
     * @return возвращает строку, сгенерированную в методе about из aboutService
     */
    @Operation(summary = "Вывод информации о приюте")
    public String about(long chatId){
        userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        return aboutService.about();
    }

    /**
     * @param chatId получает id чата пользователя
     * @return возвращает строку, сгенерированную в методе getPetForm из petService
     */
    @Operation(summary = "Выводит информации о форме отчета")
    public String petReportForm(long chatId){
        userStatusService.changeUserStatus(chatId, GET_PET_FORM.getStatus());
        return petService.getPetForm();
    }

    /**
     * @param chatId получает id чата пользователя
     * @return возвращает строку, сгенерированную в методе getContactInformation из contactInformationService
     */
    @Operation(summary = "Выводит формат для отправки контактной информации")
    public String contactInformation(long chatId){
        userStatusService.changeUserStatus(chatId, GET_CONTACT_INFORMATION.getStatus());
        return contactInformationService.getContactInformation();
    }

    /**
     * @param chatId получает id чата пользователя
     * @return возвращает объект животного из базы данных, последнего просмотренного или первого из списка
     */
    @Operation(summary = "Выводит последнее просмотренное животное или первое")
    public Pet petList(long chatId){
        userStatusService.changeUserStatus(chatId, VIEW_PET_LIST.getStatus());
        return petService.getPet(chatId);
    }

    /**
     * @param update объект чата пользователя
     * @return возвращает id свободного администратора или 0
     */
    @Operation(summary = "Возвращает id свободного администратора")
    public Long callVolunteer(Update update){
        return callService.createCall(update);
    }
}
