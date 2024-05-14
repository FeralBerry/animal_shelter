package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.model.UserRepository;


@Slf4j
@Service
public class UserStatusService {
    /**
     *
     */
    private final UserRepository userRepository;

    /**
     *
     * @param userRepository
     */
    public UserStatusService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     *
     * @param chatId
     * @param newStatus
     */
    public void changeUserStatus(long chatId, String newStatus){
        User user = new User();
        userRepository.findById(chatId)
                .ifPresent(i -> {
                    user.setRole(i.getRole());
                    user.setChatId(chatId);
                    user.setFirstName(i.getFirstName());
                    user.setLastName(i.getLastName());
                    user.setUserName(i.getUserName());
                    user.setLocationUserOnApp(newStatus);
                });
        userRepository.save(user);
        log.info("user " + user +" changed status: " + newStatus);
    }

    /**
     *
     * @param chatId
     * @return
     */
    public String getUserStatus(long chatId){
        StringBuilder loc = new StringBuilder();
        userRepository.findById(chatId)
                .ifPresent(i -> loc.append(i.getLocationUserOnApp()));
        log.info("user loc: " + loc);
        return loc.toString();
    }
}
