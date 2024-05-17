package pro.sky.animal_shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.service.*;

import static pro.sky.animal_shelter.enums.UserSatausEnum.*;

@Service
public class UrlController{
    private final AdminService adminService;
    private final UserStatusService userStatusService;
    private final StartService startService;
    private final InfoService infoService;
    private final AboutService aboutService;
    private final PetService petService;
    private final ContactInformationService contactInformationService;
    private final CallService callService;

    public UrlController(AdminService adminService,
                         UserStatusService userStatusService,
                         StartService startService,
                         InfoService infoService,
                         AboutService aboutService,
                         PetService petService,
                         CallService callService,
                         ContactInformationService contactInformationService){
        this.adminService = adminService;
        this.userStatusService = userStatusService;
        this.startService = startService;
        this.infoService = infoService;
        this.aboutService = aboutService;
        this.petService = petService;
        this.contactInformationService = contactInformationService;
        this.callService = callService;
    }

    /**
     * @param message получает информацию о пользователе и отправленном сообщении,
     *                от этого проверяет, кем является пользователь, и возвращает сообщение.
     * @return возвращает строку для отправки сообщения вызвавшему метод
     */
    @Operation(summary = "Вывод приветственного сообщения")
    public String start(Message message){
        long chatId = message.getChatId();
        if (adminService.checkAdmin(chatId)) {
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            return "Главное меню администратора.";
        } else {
            String msg = startService.start(message);
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            return msg;
        }
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
     * @param chatId получает id чата пользователя
     * @return возвращает id свободного администратора или 0
     */
    @Operation(summary = "Возвращает id свободного администратора")
    public long callVolunteer(long chatId){
        if(callService.createCall(chatId) == 0){
            return 0;
        } else {
            return callService.createCall(chatId);
        }
    }
}
