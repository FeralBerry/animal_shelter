
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
            User random = userRepository.findAllAdmin().get(randomizer.nextInt(userRepository.findAllAdmin().size()));
            User user = new User();
            user.setChatId(random.getChatId());
            user.setFirstName(random.getFirstName());
            user.setLastName(random.getLastName());
            user.setUserName(random.getUserName());
            user.setRole(random.getRole());
            user.setLocationUserOnApp("call");
            userRepository.save(user);
            log.info("admin change status: " + user);
            Call call = new Call();
            long nowSec = (new Date().getTime())/1000;
            call.setAdminChatId(random.getChatId());
            call.setUserChatId(chatId);
            call.setUpdatedAt(nowSec);
            callRepository.save(call);
            log.info("created new call: " + call);
            return random.getChatId();
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
            call = callRepository.findByUserChatId(chatId);
            call.setUpdatedAt(nowSec);
            sendChatId = callRepository.findByUserChatId(chatId).getUserChatId();
        } else {
            call = callRepository.findByAdminChatId(chatId);
            call.setUpdatedAt(nowSec);
            sendChatId = callRepository.findByAdminChatId(chatId).getAdminChatId();
        }
        callRepository.save(call);
        return sendChatId;
    }
}
