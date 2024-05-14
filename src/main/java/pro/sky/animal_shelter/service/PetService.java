package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.*;

import java.util.List;

@Slf4j
@Service
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    public PetService(PetRepository petRepository,UserRepository userRepository){
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    private static String PET_FORM = "В ежедневный отчет входит следующая информация:\n" +
            "- Фото животного\n" +
            "- Рацион животного\n" +
            "- Общее самочувствие и привыкание к новому месту\n" +
            "- Изменения в поведении: отказ от старых привычек, приобретение новых\n\n" +
            "Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет.";
    public String getPetForm(){

        return PET_FORM;
    }
    // получать информацию из БД всех животных
    public List<Pet> getPets(){
        return petRepository.findAll();
    }
    // получить информацию по определенному животному
    public Pet getPet(long chatId){
        var user = userRepository.findById(chatId).stream().toList();
        User newUser = new User();
        long lastViewPetId = 0;
        for (User user1 : user) {
            lastViewPetId = user1.getPetId();
            newUser.setChatId(user1.getChatId());
            newUser.setFirstName(user1.getFirstName());
            newUser.setLastName(user1.getLastName());
            newUser.setRole(user1.getRole());
            newUser.setUserName(user1.getUserName());
        }
        if(getPets().isEmpty()){
            return null;
        } else {
            if(lastViewPetId == 0 || petRepository.findById(lastViewPetId).isEmpty()){
                newUser.setPetId(petRepository.findLimitPet().getId());
                userRepository.save(newUser);
                return petRepository.findLimitPet();
            } else {
                newUser.setPetId(petRepository.findById(lastViewPetId).get().getId());
                userRepository.save(newUser);
                return petRepository.findById(lastViewPetId).get();
            }
        }
    }
    // проверять роль пользователя и добавлять животного в БД
    public void addPet(long chatId, Pet pet){

    }
    // проверять роль пользователя и изменяем животного в БД
    public void editPet(){

    }
    // проверять роль пользователя и удаляем животного из БД
    public void deletePet(){

    }
}
