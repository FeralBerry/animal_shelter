package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.*;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.PetsImgRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static pro.sky.animal_shelter.enums.AdminStatusEnum.PET_ADD_NAME;

@Slf4j
@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetsImgRepository petsImgRepository;
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    public PetService(PetRepository petRepository,
                      PetsImgRepository petsImgRepository,
                      UserRepository userRepository,
                      UserStatusService userStatusService){
        this.petRepository = petRepository;
        this.petsImgRepository = petsImgRepository;
        this.userRepository = userRepository;
        this.userStatusService = userStatusService;
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

    /**
     *
     * @return
     */
    public List<Pet> getPets(){
        return petRepository.findAll();
    }

    /**
     *
     * @param chatId
     * @return
     */
    public Pet getPet(long chatId){
        User newUser = getUserInfo(chatId);
        long lastViewPetId = newUser.getPetId();
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
    public Pet getNextPet(long chatId){
        User newUser = getUserInfo(chatId);
        long lastViewPetId = newUser.getPetId();
        if(petRepository.findNextPet(lastViewPetId).getId() > 0){
            newUser.setPetId(petRepository.findNextPet(lastViewPetId).getId());
            userRepository.save(newUser);
            return petRepository.findNextPet(lastViewPetId);
        } else {
            newUser.setPetId(petRepository.findIdFirstPet());
            userRepository.save(newUser);
            return petRepository.findById(petRepository.findIdFirstPet()).get();
        }
    }
    public Pet getPrevPet(long chatId){
        User newUser = getUserInfo(chatId);
        long lastViewPetId = newUser.getPetId();
        if(petRepository.findPrevPet(lastViewPetId).getId() <= 0){
            newUser.setPetId(petRepository.findPrevPet(lastViewPetId).getId());
            userRepository.save(newUser);
            return petRepository.findPrevPet(lastViewPetId);
        } else {
            newUser.setPetId(petRepository.findIdLastPet());
            userRepository.save(newUser);
            return petRepository.findById(petRepository.findIdLastPet()).get();
        }
    }
    private User getUserInfo(long chatId){
        var user = userRepository.findById(chatId).stream().toList();
        User newUser = new User();
        for (User user1 : user) {
            newUser.setChatId(user1.getChatId());
            newUser.setFirstName(user1.getFirstName());
            newUser.setLastName(user1.getLastName());
            newUser.setRole(user1.getRole());
            newUser.setUserName(user1.getUserName());
        }
        return newUser;
    }

    /**
     * Метод создает нового питомца и переключает статус пользователя для добавления изображения,
     * и сохраняет пользователю статус какого питомца последним добавлял администратор
     * @param chatId id чата кто добавляет питомца
     * @param message имя питомца
     */
    public void addPetName(long chatId, String message){
        Pet pet = new Pet();
        pet.setPetName(message);
        petRepository.save(pet);
        User user = new User();
        userRepository.findById(chatId)
                .ifPresent(i -> {
                    user.setRole(i.getRole());
                    user.setChatId(chatId);
                    user.setFirstName(i.getFirstName());
                    user.setLastName(i.getLastName());
                    user.setUserName(i.getUserName());
                    user.setLocationUserOnApp(PET_ADD_NAME.getStatus());
                    user.setAddedPetId(pet.getId());
                });
        userRepository.save(user);
    }
    public List<String> getPetImages(long chatId){
        User user = new User();
        if(userRepository.findById(chatId).isPresent()){
            user = userRepository.findById(chatId).get();
        }
        long petId = user.getPetId();
        List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(petId);
        List<String> images = new ArrayList<>();
        for (PetsImg petsImg : petImages){
            images.add(petsImg.getFileId());
        }
        return images;
    }
    public void addPetImages(long chatId,List<String> photos){
        long petId = userRepository.findById(chatId).get().getAddedPetId();
        List <PetsImg> list = new ArrayList<>();
        for (String photo : photos){
            PetsImg petsImg = new PetsImg();
            petsImg.setPetId(petId);
            petsImg.setFileId(photo);
            list.add(petsImg);
        }
        petsImgRepository.saveAll(list);
    }
    /**

     */
    public void addPetDescription(long chatId,String description){
        long petId = userRepository.findById(chatId).get().getAddedPetId();
        Pet pet = petRepository.findById(petId).get();
        System.out.println(pet);
        pet.setDescription(description);
        petRepository.save(pet);
    }

    /**
     *
     */
    // проверять роль пользователя и изменяем животного в БД
    public void editPet(){

    }

    /**
     *
     */
    // проверять роль пользователя и удаляем животного из БД
    public void deletePet(){

    }
}
