
package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

import java.util.Date;
import java.util.Random;

import static pro.sky.animal_shelter.enums.UserSatausEnum.CALL_A_VOLUNTEER;
import static pro.sky.animal_shelter.enums.UserSatausEnum.NO_STATUS;
@Slf4j
@Service
public class CallService {
    private final UserStatusService userStatusService;
    private final UserRepository userRepository;
    private final CallRepository callRepository;
    private final AdminService adminService;
    public CallService(UserStatusService userStatusService,
                       UserRepository userRepository,
                       AdminService adminService,
                       CallRepository callRepository){
        this.userStatusService = userStatusService;
        this.userRepository = userRepository;
        this.callRepository = callRepository;
        this.adminService = adminService;
    }
    /**
     * Создает чат между свободным администратором и пользователем
     * @param update объект пользователя, который хочет задать вопрос
     * @return возвращает id свободного администратора для дальнейшего общения
     */

    public Long createCall(Update update){
        Random randomizer = new Random();
        long chatId = update.getMessage().getChatId();
        if(userRepository.findAllAdmin().isEmpty()){
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            return null;
        } else {
            userStatusService.changeUserStatus(chatId, CALL_A_VOLUNTEER.getStatus());
            var adminsList = userRepository.findAllAdmin();
            User randomAdmin = adminsList.get(randomizer.nextInt(adminsList.size()));
            User admin = new User();
            admin.setChatId(randomAdmin.getChatId());
            admin.setFirstName(randomAdmin.getFirstName());
            admin.setLastName(randomAdmin.getLastName());
            admin.setUserName(randomAdmin.getUserName());
            admin.setRole(randomAdmin.getRole());
            admin.setLocationUserOnApp("call");
            userRepository.save(admin);
            log.info("admin change status: " + admin);
            User user = new User();
            if(userRepository.findById(chatId).isPresent()){
                user = userRepository.findById(chatId).get();
            }
            Call call = new Call();
            long nowSec = (new Date().getTime())/1000;
            call.setAdminChatId(admin);
            call.setUserChatId(user);
            call.setUpdatedAt(nowSec);
            callRepository.save(call);
            log.info("created new call: " + call);
            return admin.getChatId();
        }
    }

    /**
     * Метод определения id собеседника
     * и обновляет время последнего сообщения чата
     * @param chatId id отправившего сообщение
     * @return возвращает id кому отправить сообщение
     */
    public long sendMessageChat(long chatId){
        long nowSec = (new Date().getTime())/1000;
        Call call;
        long sendChatId;
        if(adminService.checkAdmin(chatId)){
            call = callRepository.findByAdminChatId(chatId);
            call.setUpdatedAt(nowSec);
            sendChatId = call.getUserChatId().getChatId();
        } else {
            call = callRepository.findByUserChatId(chatId);
            call.setUpdatedAt(nowSec);
            sendChatId = call.getAdminChatId().getChatId();
        }
        callRepository.save(call);
        return sendChatId;
    }
}
