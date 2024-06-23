package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.controller.TelegramBot;
import pro.sky.animal_shelter.model.*;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.PetsImgRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static pro.sky.animal_shelter.enums.AdminStatusEnum.PET_ADD_NAME;

@Slf4j
@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetsImgRepository petsImgRepository;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public PetService(PetRepository petRepository,
                      PetsImgRepository petsImgRepository,
                      UserRepository userRepository, MessageUtils messageUtils){
        this.petRepository = petRepository;
        this.petsImgRepository = petsImgRepository;
        this.userRepository = userRepository;
        this.messageUtils = messageUtils;
    }

    private static final String PET_FORM = """
            В ежедневный отчет входит следующая информация:
            - Фото животного
            - Рацион животного
            - Общее самочувствие и привыкание к новому месту
            - Изменения в поведении: отказ от старых привычек, приобретение новых

            Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет.""";

    /**
     * @return возвращает строку для вывода пользователю формы отчета
     */
    public String getPetForm(){
        return PET_FORM;
    }

    /**
     * @return возвращает список всех животных
     */
    public List<Pet> getPets(){
        return petRepository.findAll();
    }

    /**
     * Метод возвращает поледнее просмотренное животное если оно есть, если нет то возвращает первое
     * @param update объект параметров запроса из телеграм бота
     * @return возвращает объект животного
     */
    public Pet getPet(Update update){
        User newUser;
        if(update.hasCallbackQuery()){
            newUser = getUserById(update.getCallbackQuery().getFrom().getId());
        } else {
            newUser = getUserById(update.getMessage().getChatId());
        }
        if(newUser.getPetId() == null){
            List<Pet> pets = petRepository.findAll();
            if(pets.isEmpty()){
                return null;
            }
            Pet pet = petRepository.findIdFirstPet();
            if(pet != null){
                return checkPet(pet.getId(),newUser);
            } else {
                return null;
            }
        }
        Pet lastViewPetId = newUser.getPetId();
        return checkPet(lastViewPetId.getId(),newUser);
    }

    /**
     * Метод проверки существования животных и животного по id
     * @param lastViewPetId id последнего просмотренного пользователем животного
     * @param newUser объект пользователя который просматривает животных
     * @return возвращает объект животного
     */
    public Pet checkPet(long lastViewPetId, User newUser) {
        if(getPets().isEmpty()){
            return null;
        } else {
            if(lastViewPetId == 0 || petRepository.findById(lastViewPetId).isEmpty()){
                newUser.setPetId(petRepository.findLimitPet());
                userRepository.save(newUser);
                return petRepository.findLimitPet();
            } else {
                newUser.setPetId(petRepository.findById(lastViewPetId).get());
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
                    user.setAddedPetId(pet);
                });
        userRepository.save(user);
    }

    /**
     * Метод получает список адресов картинок
     * @param update объект параметров запроса из телеграм бота
     * @return список адресов изображений с id животного из запроса
     */
    public List<String> getPetImages(Update update){
        List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(getPet(update).getId());
        List<String> images = new ArrayList<>();
        for (PetsImg petsImg : petImages){
            images.add(petsImg.getFileId());
        }
        return images;
    }
    public List<String> getPetImagesByPet(Pet pet){
        List<PetsImg> petImages = petsImgRepository.findPetsImgByPetId(pet.getId());
        List<String> images = new ArrayList<>();
        for (PetsImg petsImg : petImages){
            images.add(petsImg.getFileId());
        }
        return images;
    }

    /**
     * Меняет текущее значение просмотренного животного, если текущее последнее переставляет на первое
     */
    public void changeNextPetView(Update update){
        long chatId = update.getCallbackQuery().getFrom().getId();
        User user = getUserById(chatId);
        Pet lastViewPetId = user.getPetId();
        Pet lastPetId = petRepository.findIdLastPet();
        Pet viewPet;
        if(lastViewPetId.getId() < lastPetId.getId()){
            viewPet = petRepository.findNextPet(lastViewPetId.getId());
            user.setPetId(viewPet);
            userRepository.save(user);
        } else {
            viewPet = petRepository.findIdFirstPet();
            user.setPetId(viewPet);
            userRepository.save(user);
        }
        if(getPetImages(update).size() > 1){
            SendMediaGroup sendMediaGroups = messageUtils.sendMediaGroup(chatId, getPetImagesByPet(viewPet));
            setView(sendMediaGroups);
        } else {
            SendPhoto sendPhoto = messageUtils.sendPhoto(chatId,getPetImagesByPet(viewPet).get(0));
            setView(sendPhoto);
        }
    }

    /**
     * Меняет текущее значение просмотренного животного, если текущее первое переставляет на последнее
     */
    public void changePrevPetView(Update update) {
        long chatId = update.getCallbackQuery().getFrom().getId();
        User user = getUserById(chatId);
        Pet lastViewPetId = user.getPetId();
        Pet firstPetId = petRepository.findIdFirstPet();
        if (lastViewPetId != firstPetId) {
            user.setPetId(petRepository.findPrevPet(lastViewPetId.getId()));
            userRepository.save(user);
        } else {
            user.setPetId(petRepository.findIdLastPet());
            userRepository.save(user);
        }
        if(getPetImages(update).size() > 1){
            SendMediaGroup sendMediaGroups = messageUtils.sendMediaGroup(chatId, getPetImages(update));
            setView(sendMediaGroups);
        } else {
            SendPhoto sendPhoto = messageUtils.sendPhoto(chatId,getPetImages(update).get(0));
            setView(sendPhoto);
        }
    }
    /**
     * Проверяет, какое животное заполняет администратор, и добавляет адреса всех
     * загруженных изображений в базу данных
     * @param chatId id пользователя заполняющего информацию о животном
     */
    public void addPetImages(long chatId,List<String> photos){
        Pet petId = getUserById(chatId).getAddedPetId();
        List <PetsImg> list = new ArrayList<>();
        PetsImg petsImg = new PetsImg();
        for (String photo : photos){
            petsImg.setPetId(petId);
            petsImg.setFileId(photo);
            list.add(petsImg);
        }
        petsImgRepository.saveAll(list);
    }

    /**
     * Метод добавляет описание животному
     * @param chatId id пользователя заполняющего информацию о животном
     * @param description описание животного получаемое из чата с ботом
     */
    public void addPetDescription(long chatId,String description){
        Pet petId = getUserById(chatId).getAddedPetId();
        Pet pet = getPetById(petId.getId());
        pet.setDescription(description);
        petRepository.save(pet);
    }
    /**
     * Метод проверки существования пользователя с таким id
     * @param chatId id пользователя
     * @return возвращает пользователя по id или ошибку если такого пользователя нет
     */
    public User getUserById(long chatId){
        Optional<User> user = userRepository.findByChatId(chatId);
        if(user.isPresent()){
            return user.get();
        } else {
            throw new NoSuchElementException("Пользователь с id=" + chatId + " не существует");
        }
    }
    /**
     * Метод проверки существования животного с таким id
     * @param id id животного
     * @return возвращает животного по id или ошибку если такого животного нет
     */
    public Pet getPetById(long id){
        Optional<Pet> pet = petRepository.findById(id);
        if(pet.isPresent()) {
            return pet.get();
        } else {
            throw new NoSuchElementException("Животное с id=" + id + " не существует");
        }
    }
    public void setView(SendMediaGroup sendMediaGroup) {
        telegramBot.sendAnswerMessage(sendMediaGroup);
    }
    public void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
}
