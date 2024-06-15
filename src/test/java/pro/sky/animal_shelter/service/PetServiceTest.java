package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.*;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.PetsImg;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.PetsImgRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PetServiceTest {
    Update update = new Update();
    @MockBean
    PetRepository petRepository;
    @MockBean
    PetsImgRepository petsImgRepository;
    @MockBean
    UserRepository userRepository;
    @Autowired
    PetService petService;

    @Test
    void getPetForm() {
        String PET_FORM = """
            В ежедневный отчет входит следующая информация:
            - Фото животного
            - Рацион животного
            - Общее самочувствие и привыкание к новому месту
            - Изменения в поведении: отказ от старых привычек, приобретение новых

            Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет.""";
        assertEquals(PET_FORM,petService.getPetForm());
    }

    @ParameterizedTest
    @MethodSource("pets")
    void getPetsPositive(List<Pet> pets) {
        when(petRepository.findAll()).thenReturn(pets);
        assertEquals(petService.getPets(),pets);
    }
    @Test
    void getPetsNegative(){
        doReturn(List.of())
                .when(petRepository)
                .findAll();
        assertEquals(petService.getPets(),List.of());
    }
    @ParameterizedTest
    @MethodSource("pets")
    void getPet(List<Pet> pets) {
        checkPetIsEmpty();
        checkPetLastViewPositive(pets);
    }
    @Test
    void checkPetIsEmpty(){
        setUpdate();
        doReturn(List.of())
                .when(petRepository)
                .findAll();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        assertNull(petService.checkPet(update.getMessage().getChatId(),user));
    }
    @ParameterizedTest
    @MethodSource("pets")
    void checkPetLastViewPositive(List<Pet> pets){
        setUpdate();
        doReturn(pets)
                .when(petRepository)
                .findAll();
        Pet pet = new Pet();
        doReturn(Optional.of(pet))
                .when(petRepository)
                .findById(1L);
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        user.setPetId(pet);

        assertEquals(petService.checkPet(1L,user),pet);

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
    }
    @ParameterizedTest
    @MethodSource("pets")
    void checkPetLastViewNegative(List<Pet> pets){
        setUpdate();
        doReturn(pets)
                .when(petRepository)
                .findAll();
        doReturn(Optional.of(pets.get(1)))
                .when(petRepository)
                .findById(1L);
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        if(petRepository.findById(1L).isPresent()){
            pet = petRepository.findById(1L).get();
        }
        when(petRepository.findLimitPet()).thenReturn(pet);

        user.setPetId(pet);

        assertEquals(petService.checkPet(1L,user),pet);

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);

        assertEquals(petRepository.findLimitPet(),pet);

        assertEquals(petService.checkPet(0,user),pet);

        assertEquals(petRepository.findLimitPet(),pet);
    }
    @Test
    void addPetName() {
        setUpdate();
        long chatId = update.getMessage().getChatId();
        String message = "text";
        Pet pet = new Pet();
        pet.setPetName(message);
        petService.addPetName(chatId,message);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());
        assertEquals(captor.getValue().toString(),pet.toString());

        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(chatId);

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor2 = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor2.capture());
        assertEquals(captor2.getValue(), user);
    }

    @Test
    void getPetImages() {
        setUpdate();
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setPetName("Вася");
        pet1.setDescription("Мягкий");
        List<Pet> pets = List.of(pet1);
        doReturn(pets)
                .when(petRepository)
                .findAll();
        Pet pet = new Pet();
        pet.setId(1L);
        doReturn(Optional.of(pet))
                .when(petRepository)
                .findById(1L);
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        user.setPetId(pet);

        assertEquals(petService.checkPet(1L,user),pet);

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
        List<PetsImg> petsImgList = new ArrayList<>();
        List<String> images = new ArrayList<>();
        PetsImg petsImg = new PetsImg();
        petsImg.setId(1L);
        petsImg.setFileId("1L");
        petsImgList.add(petsImg);
        when(petsImgRepository
                .findPetsImgByPetId(petService
                        .checkPet(1L,user)
                        .getId()))
                .thenReturn(petsImgList);
        for (PetsImg petImg : petsImgList){
            images.add(petImg.getFileId());
        }
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        assertEquals(petService.getPetImages(update).toString(),images.toString());
    }

    @ParameterizedTest
    @MethodSource("pets")
    void changeNextPetViewLast(List<Pet> pets) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        pet.setId(1L);
        user.setPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));
        System.out.println(user.getPetId());
        doReturn(pets.get(2))
                .when(petRepository)
                .findIdLastPet();
        doReturn(pets.get(0))
                .when(petRepository)
                .findIdFirstPet();
        user.setPetId(pets.get(0));

        petService.changeNextPetView(update.getMessage().getChatId());

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
    }
    @ParameterizedTest
    @MethodSource("pets")
    void changeNextPetViewMiddleOrFirst(List<Pet> pets) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        pet.setId(1L);
        user.setPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));

        doReturn(pets.get(0))
                .when(petRepository)
                .findIdLastPet();
        doReturn(pets.get(2))
                .when(petRepository)
                .findNextPet(user.getPetId().getId());
        user.setPetId(pets.get(2));

        petService.changeNextPetView(update.getMessage().getChatId());

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
    }
    @ParameterizedTest
    @MethodSource("pets")
    void changePrevPetViewFirst(List<Pet> pets) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        pet.setId(1L);
        user.setPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));

        doReturn(pets.get(0))
                .when(petRepository)
                .findIdFirstPet();

        doReturn(pets.get(2))
                .when(petRepository)
                .findIdLastPet();
        user.setPetId(pets.get(2));

        petService.changeNextPetView(update.getMessage().getChatId());

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
    }
    @ParameterizedTest
    @MethodSource("pets")
    void changePrevPetViewMiddleOrLast(List<Pet> pets) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        pet.setId(1L);
        user.setPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));

        doReturn(pets.get(2))
                .when(petRepository)
                .findPrevPet(user.getPetId().getId());

        doReturn(pets.get(0))
                .when(petRepository)
                .findIdLastPet();
        user.setPetId(pets.get(0));

        petService.changeNextPetView(update.getMessage().getChatId());

        ArgumentCaptor<pro.sky.animal_shelter.model.User> captor = ArgumentCaptor.forClass(pro.sky.animal_shelter.model.User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(captor.getValue(), user);
    }
    @ParameterizedTest
    @MethodSource("petImages")
    void addPetImages(List<String> petImages) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        Pet pet = new Pet();
        pet.setId(1L);
        user.setPetId(pet);
        user.setAddedPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));
        when(petRepository.findById(userRepository.findById(update.getMessage().getChatId()).get().getAddedPetId().getId())).thenReturn(Optional.of(pet));
        List<PetsImg> petsImgList = new ArrayList<>();
        PetsImg petsImg = new PetsImg();
        for (String photo : petImages){
            petsImg.setPetId(pet);
            petsImg.setFileId(photo);
            petsImgList.add(petsImg);
        }
        petService.addPetImages(update.getMessage().getChatId(),petImages);
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(petsImgRepository).saveAll(captor.capture());
        assertEquals(captor.getValue().toString(), petsImgList.toString());
    }

    @Test
    void addPetDescription() {
        setUpdate();
        String description = "description";
        Pet pet = new Pet();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        pet.setId(1L);
        user.setPetId(pet);
        user.setAddedPetId(pet);
        when(userRepository.findById(update.getMessage().getChatId())).thenReturn(Optional.of(user));
        pet.setDescription(description);
        when(petRepository.findById(userRepository.findById(update.getMessage().getChatId()).get().getAddedPetId().getId())).thenReturn(Optional.of(pet));

        petService.addPetDescription(update.getMessage().getChatId(),description);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());
        assertEquals(captor.getValue(), pet);
    }

    @Test
    void getUserByIdPositive() {
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        assertEquals(petService.getUserById(any(Long.class)),user);
    }
    @Test
    void getUserByIdNegative() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            petService.getUserById(any(Long.class));
        });
    }
    @Test
    void getPetByIdPositive() {
        Pet pet = new Pet();
        when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet));
        assertEquals(petService.getPetById(any(Long.class)),pet);
    }
    @Test
    void getPetByIdNegative() {
        when(petRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            petService.getPetById(any(Long.class));
        });
    }
    static Stream<Arguments> pets(){
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setPetName("Вася");
        pet1.setDescription("Мягкий");
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setPetName("Бобик");
        pet2.setDescription("Игривый");
        Pet pet3 = new Pet();
        pet3.setId(3L);
        pet3.setPetName("Мурзик");
        pet3.setDescription("Ласковый");
        return Stream.of(
                Arguments.of(List.of(
                        pet1,
                        pet2,
                        pet3
                )));
    }
    static Stream<Arguments> petImages(){
        String petsImgs1 = "1";
        String petsImgs2 = "2";
        String petsImgs3 = "3";
        return Stream.of(
                Arguments.of(List.of(
                        petsImgs1,
                        petsImgs2,
                        petsImgs3
                )));
    }
    public void setUpdate(){
        update.setUpdateId(193484977);
        Message message = new Message();
        message.setMessageId(2273);
        message.setMessageThreadId(null);
        message.setDate(1717508990);
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(982721415L);
        user.setUserName("Compas1990");
        user.setFirstName("Compas");
        user.setIsBot(false);
        user.setLastName(null);
        user.setLanguageCode("ru");
        user.setCanJoinGroups(null);
        user.setCanReadAllGroupMessages(null);
        user.setSupportInlineQueries(null);
        user.setIsPremium(null);
        user.setAddedToAttachmentMenu(null);
        message.setFrom(user);
        Chat chat = new Chat();
        chat.setUserName("Compas1990");
        chat.setFirstName("Compas");
        chat.setLastName(null);
        chat.setId(982721415L);
        chat.setType("private");
        chat.setTitle(null);
        chat.setPhoto(null);
        chat.setDescription(null);
        chat.setInviteLink(null);
        chat.setPinnedMessage(null);
        chat.setStickerSetName(null);
        chat.setCanSetStickerSet(null);
        chat.setPermissions(null);
        chat.setSlowModeDelay(null);
        chat.setBio(null);
        chat.setLinkedChatId(null);
        chat.setLocation(null);
        chat.setMessageAutoDeleteTime(null);
        chat.setHasPrivateForwards(null);
        chat.setHasProtectedContent(null);
        chat.setJoinToSendMessages(null);
        chat.setJoinByRequest(null);
        chat.setHasRestrictedVoiceAndVideoMessages(null);
        chat.setIsForum(null);
        chat.setActiveUsernames(null);
        chat.setEmojiStatusCustomEmojiId(null);
        chat.setHasAggressiveAntiSpamEnabled(null);
        chat.setHasHiddenMembers(null);
        message.setChat(chat);
        message.setForwardFrom(null);
        message.setForwardFromChat(null);
        message.setForwardDate(null);
        message.setText("/start");
        MessageEntity messageEntity = new MessageEntity();
        List<MessageEntity> entityList = new ArrayList<>();
        messageEntity.setType("bot_command");
        messageEntity.setOffset(0);
        messageEntity.setLength(6);
        messageEntity.setUrl(null);
        messageEntity.setUser(null);
        messageEntity.setLanguage(null);
        messageEntity.setCustomEmojiId(null);
        messageEntity.setText("/start");
        entityList.add(messageEntity);
        message.setEntities(entityList);
        message.setCaptionEntities(null);
        message.setAudio(null);
        message.setDocument(null);
        message.setPhoto(null);
        message.setSticker(null);
        message.setVideo(null);
        message.setContact(null);
        message.setLocation(null);
        message.setVenue(null);
        message.setAnimation(null);
        message.setPinnedMessage(null);
        message.setNewChatMembers(null);
        message.setLeftChatMember(null);
        message.setNewChatTitle(null);
        message.setNewChatPhoto(null);
        message.setDeleteChatPhoto(null);
        message.setGroupchatCreated(null);
        message.setReplyToMessage(null);
        message.setVoice(null);
        message.setCaption(null);
        message.setSuperGroupCreated(null);
        message.setChannelChatCreated(null);
        message.setMigrateToChatId(null);
        message.setMigrateFromChatId(null);
        message.setEditDate(null);
        message.setGame(null);
        message.setForwardFromMessageId(null);
        message.setInvoice(null);
        message.setSuccessfulPayment(null);
        message.setVideoNote(null);
        message.setAuthorSignature(null);
        message.setForwardSignature(null);
        message.setMediaGroupId(null);
        message.setConnectedWebsite(null);
        message.setPassportData(null);
        message.setForwardSenderName(null);
        message.setPoll(null);
        message.setReplyMarkup(null);
        message.setDice(null);
        message.setViaBot(null);
        message.setSenderChat(null);
        message.setProximityAlertTriggered(null);
        message.setMessageAutoDeleteTimerChanged(null);
        message.setIsAutomaticForward(null);
        message.setHasProtectedContent(null);
        message.setWebAppData(null);
        message.setVideoChatStarted(null);
        message.setVideoChatEnded(null);
        message.setVideoChatParticipantsInvited(null);
        message.setVideoChatScheduled(null);
        message.setIsTopicMessage(null);
        message.setForumTopicCreated(null);
        message.setForumTopicClosed(null);
        message.setForumTopicReopened(null);
        message.setForumTopicEdited(null);
        message.setGeneralForumTopicHidden(null);
        message.setGeneralForumTopicUnhidden(null);
        message.setWriteAccessAllowed(null);
        message.setHasMediaSpoiler(null);
        message.setUserShared(null);
        message.setChatShared(null);
        update.setMessage(message);
        update.setInlineQuery(null);
        update.setChosenInlineQuery(null);
        update.setCallbackQuery(null);
        update.setEditedMessage(null);
        update.setChannelPost(null);
        update.setEditedChannelPost(null);
        update.setShippingQuery(null);
        update.setPreCheckoutQuery(null);
        update.setPoll(null);
        update.setPollAnswer(null);
        update.setMyChatMember(null);
        update.setChatMember(null);
        update.setChatJoinRequest(null);
    }
}