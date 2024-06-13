package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.*;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.PetsImg;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.PetsImgRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {
    Update update = new Update();
    @Mock
    PetRepository petRepository;
    @Mock
    PetsImgRepository petImgRepository;
    @Mock
    UserRepository userRepository;
    PetService petService = new PetService(petRepository,petImgRepository,userRepository);
    PetService petServiceMock = Mockito.mock(PetService.class);

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
    void getPets(List<Pet> pets) {
        doReturn(pets)
                .when(petServiceMock)
                .getPets();
        assertEquals(pets,petServiceMock.getPets());
    }

    @ParameterizedTest
    @MethodSource("pets")
    void getPet(List<Pet> pets) {
        setUpdate();
        pro.sky.animal_shelter.model.User user = spy(pro.sky.animal_shelter.model.User.class);
        doReturn(user)
                .when(petServiceMock)
                .getUserById(982721415L);
        doReturn(pets.get(1))
                .when(petServiceMock)
                .getPet(update);
        assertEquals(pets.get(1),petServiceMock.getPet(update));
    }

    @Test
    void addPetName() {
        setUpdate();
        doNothing()
                .when(petServiceMock)
                .addPetName(update.getMessage().getChatId(),"Имя");
    }

    @ParameterizedTest
    @MethodSource("petImages")
    void getPetImages(List<String> petImages) {
        setUpdate();
        doReturn(petImages.get(1))
                .when(petServiceMock)
                .getPetImages(update);
        assertEquals(petImages.get(1),petServiceMock.getPetImages(update));
    }

    @Test
    void changeNextPetView() {
        setUpdate();
        doNothing()
                .when(petServiceMock)
                .changeNextPetView(update.getMessage().getChatId());
    }

    @Test
    void changePrevPetView() {
        setUpdate();
        doNothing()
                .when(petServiceMock)
                .changePrevPetView(update.getMessage().getChatId());
    }

    @ParameterizedTest
    @MethodSource("petImages")
    void addPetImages(List<String> petImages) {
        setUpdate();
        doNothing()
                .when(petServiceMock)
                .addPetImages(update.getMessage().getChatId(),petImages);
    }

    @Test
    void addPetDescription() {
        setUpdate();
        doNothing()
                .when(petServiceMock)
                .addPetDescription(update.getMessage().getChatId(),"Описание");
    }

    @Test
    void getUserById() {
        setUpdate();
        pro.sky.animal_shelter.model.User user = spy(pro.sky.animal_shelter.model.User.class);
        doReturn(user)
                .when(petServiceMock)
                .getUserById(982721415L);
        assertEquals(user,petServiceMock.getUserById(982721415L));
    }

    @ParameterizedTest
    @MethodSource("pets")
    void getPetById(List<Pet> pets) {
        doReturn(pets.get(1))
                .when(petServiceMock)
                .getPetById(1L);
        assertEquals(pets.get(1),petServiceMock.getPetById(1L));
    }
    static Stream<Arguments> pets(){
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setPetName("Вася");
        pet1.setDescription("Мягкий");
        Pet pet2 = new Pet();
        pet1.setId(2L);
        pet1.setPetName("Бобик");
        pet1.setDescription("Игривый");
        Pet pet3 = new Pet();
        pet1.setId(3L);
        pet1.setPetName("Мурзик");
        pet1.setDescription("Ласковый");
        return Stream.of(
                Arguments.of(List.of(
                        pet1,
                        pet2,
                        pet3
                )));
    }
    static Stream<Arguments> petImages(){
        PetsImg pet1 = new PetsImg();
        Pet pet = new Pet();
        List<PetsImg> petsImgs1 = new ArrayList<>();
        {
            pet.setId(1L);
            pet1.setId(1L);
            pet1.setFileId("1");
            pet1.setPetId(pet);
            petsImgs1.add(pet1);
            pet1.setId(2L);
            pet1.setFileId("2");
            pet1.setPetId(pet);
            petsImgs1.add(pet1);
            pet1.setId(3L);
            pet1.setFileId("3");
            pet1.setPetId(pet);
            petsImgs1.add(pet1);
        }
        List<PetsImg> petsImgs2 = new ArrayList<>();
        {
            pet.setId(2L);
            pet1.setId(4L);
            pet1.setFileId("1");
            pet1.setPetId(pet);
            petsImgs2.add(pet1);
            pet1.setId(5L);
            pet1.setFileId("2");
            pet1.setPetId(pet);
            petsImgs2.add(pet1);
            pet1.setId(6L);
            pet1.setFileId("3");
            pet1.setPetId(pet);
            petsImgs2.add(pet1);
        }
        List<PetsImg> petsImgs3 = new ArrayList<>();
        {
            pet.setId(3L);
            pet1.setId(7L);
            pet1.setFileId("1");
            pet1.setPetId(pet);
            petsImgs3.add(pet1);
            pet1.setId(8L);
            pet1.setFileId("2");
            pet1.setPetId(pet);
            petsImgs3.add(pet1);
            pet1.setId(9L);
            pet1.setFileId("3");
            pet1.setPetId(pet);
            petsImgs3.add(pet1);
        }
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