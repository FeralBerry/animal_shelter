package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.*;

@Slf4j
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AboutRepository aboutRepository;
    private final ContactInformationRepository contactInformationRepository;
    private final InfoRepository infoRepository;
    private final PetRepository petRepository;
    public AdminService(UserRepository userRepository,
                        AboutRepository aboutRepository,
                        ContactInformationRepository contactInformationRepository,
                        InfoRepository infoRepository,
                        PetRepository petRepository){
        this.userRepository = userRepository;
        this.aboutRepository = aboutRepository;
        this.contactInformationRepository = contactInformationRepository;
        this.infoRepository = infoRepository;
        this.petRepository = petRepository;
    }
    public boolean checkAdmin(long chatId){
        var user = userRepository.findById(chatId);
        StringBuilder role = new StringBuilder();
        if(user.isEmpty()) {
            return false;
        } else{
            user.ifPresent(item->role.append(item.getRole()));
        }
        return role.toString().equals("admin");
    }
    public void setRole(Message message){
        User user = new User();
        user.setChatId(message.getChatId());
        user.setFirstName(message.getChat().getFirstName());
        user.setLastName(message.getChat().getLastName());
        user.setUserName(message.getChat().getUserName());
        user.setRole("admin");
        userRepository.save(user);
        log.info("user saved: " + user);
    }
}
