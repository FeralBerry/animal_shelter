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

    public PetService(PetRepository petRepository,
                      PetsImgRepository petsImgRepository,
                      UserRepository userRepository){
        this.petRepository = petRepository;
        this.petsImgRepository = petsImgRepository;
        this.userRepository = userRepository;
    }

    private static final String PET_FORM = "В ежедневный отчет входит следующая информация:\n" +
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
        User newUser = userRepository.findById(chatId).get();
        long lastViewPetId = newUser.getPetId();
        return checkPet(lastViewPetId,newUser);
    }

    private Pet checkPet(long lastViewPetId, User newUser) {
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
        List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(getPet(chatId).getId());
        List<String> images = new ArrayList<>();
        for (PetsImg petsImg : petImages){
            images.add(petsImg.getFileId());
        }
        return images;
    }
    public void changeNextPetView(long chatId){
        User user = userRepository.findById(chatId).get();
        long lastViewPetId = user.getPetId();
        long lastPetId = petRepository.findIdLastPet();
        if(lastViewPetId < lastPetId){
            user.setPetId(petRepository.findNextPet(lastViewPetId).getId());
            userRepository.save(user);
        } else {
            user.setPetId(petRepository.findIdFirstPet());
            userRepository.save(user);
        }
    }
    public void changePrevPetView(long chatId) {
        User user = userRepository.findById(chatId).get();
        long lastViewPetId = user.getPetId();
        long firstPetId = petRepository.findIdFirstPet();
        if (lastViewPetId != firstPetId) {
            user.setPetId(petRepository.findPrevPet(lastViewPetId).getId());
            userRepository.save(user);
        } else {
            user.setPetId(petRepository.findIdLastPet());
            userRepository.save(user);
        }
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
}
