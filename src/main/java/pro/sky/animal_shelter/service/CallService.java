
package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.CallRepository;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.UserRepository;
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
     *
     * @param chatId
     * @return
     */

    public long createCall(long chatId){
        Random randomizer = new Random();
        if(userRepository.findAllAdmin().isEmpty()){
            userStatusService.changeUserStatus(chatId, NO_STATUS.getStatus());
            return 0;
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
            call.setAdminChatId(random.getChatId());
            call.setUserChatId(chatId);
            callRepository.save(call);
            log.info("created new call: " + call);
            return random.getChatId();
        }
    }
    public long sendMessageChat(long chatId){
        if(adminService.checkAdmin(chatId)){
            return callRepository.findByUserChatId(chatId).getUserChatId();
        } else {
            return callRepository.findByAdminChatId(chatId).getAdminChatId();
        }
    }
}
