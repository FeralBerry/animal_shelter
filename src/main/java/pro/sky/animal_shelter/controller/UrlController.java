package pro.sky.animal_shelter.controller;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.service.*;

import static pro.sky.animal_shelter.enums.UserSatausEnum.*;

@Service
public class UrlController{
    /**
     *
     */
    private final AdminService adminService;
    /**
     *
     */
    private final UserStatusService userStatusService;
    /**
     *
     */
    private final StartService startService;
    /**
     *
     */
    private final InfoService infoService;
    /**
     *
     */
    private final AboutService aboutService;
    /**
     *
     */
    private final PetService petService;
    /**
     *
     */
    private final ContactInformationService contactInformationService;
    /**
     *
     */
    private final CallService callService;

    /**
     *
     * @param adminService
     * @param userStatusService
     * @param startService
     * @param infoService
     * @param aboutService
     * @param petService
     * @param callService
     * @param contactInformationService
     */
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
     *
     * @param message
     * @return
     */
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
     *
     * @param chatId
     * @return
     */
    public String info(long chatId){
        userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        return infoService.info();
    }

    /**
     *
     * @param chatId
     * @return
     */
    public String about(long chatId){
        userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
        return aboutService.about();
    }

    /**
     *
     * @param chatId
     * @return
     */
    public String petReportForm(long chatId){
        userStatusService.changeUserStatus(chatId, GET_PET_FORM.getStatus());
        return petService.getPetForm();
    }

    /**
     *
     * @param chatId
     * @return
     */
    public String contactInformation(long chatId){
        userStatusService.changeUserStatus(chatId, GET_CONTACT_INFORMATION.getStatus());
        return contactInformationService.getContactInformation();
    }

    /**
     *
     * @param chatId
     * @return
     */
    public Pet petList(long chatId){
        // переключить статус пользователя
        userStatusService.changeUserStatus(chatId, VIEW_PET_LIST.getStatus());
        return petService.getPet(chatId);
    }

    /**
     * @param chatId
     */
    public long callVolunteer(long chatId){
        if(callService.createCall(chatId) == 0){
            return 0;
        } else {
            return callService.createCall(chatId);
        }
    }
}
