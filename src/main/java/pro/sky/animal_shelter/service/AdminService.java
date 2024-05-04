package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.*;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AboutRepository aboutRepository;
    @Autowired
    private ContactInformationRepository contactInformationRepository;
    @Autowired
    private InfoRepository infoRepository;
    @Autowired
    private PetRepository petRepository;
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
    public void viewContactInfo() {

    }

}
