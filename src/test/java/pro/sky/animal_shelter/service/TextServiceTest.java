package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.controller.CallController;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.model.Repositories.InfoRepository;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.ADD_PET_REPORT_TEXT;

@SpringBootTest
@AutoConfigureMockMvc
class TextServiceTest {
    @MockBean
    UserRepository userRepository;
    @MockBean
    InfoRepository infoRepository;
    @MockBean
    CallRepository callRepository;
    @MockBean
    PetRepository petRepository;
    @MockBean
    UserStatusService userStatusService;
    @Autowired
    MessageUtils messageUtils;
    @Autowired
    CallService callService;
    @MockBean
    AdminService adminService;
    @MockBean
    ContactInformationService contactInformationService;
    @Autowired
    CallController callController;
    @Autowired
    UrlService urlService;
    @Autowired
    PetService petService;
    @MockBean
    AboutService aboutService;
    @MockBean
    InfoService infoService;
    @MockBean
    ReportService reportService;
    Update update = new Update();
    @Autowired
    TextService textService;
    @BeforeEach
    void setUp(){
        textService = new TextService(this.messageUtils,
                this.adminService,
                this.userStatusService,
                this.contactInformationService,
                this.callController,
                this.urlService,
                this.petService,
                this.aboutService,
                this.infoService,
                this.reportService);
    }
    @Test
    void defineMethod() {
        setUpdate();
        List<SendMessage> sendMessageList = new ArrayList<>();
        SendMessage message;
        long chatId = update.getMessage().getChatId();
        User user = new User();
        {
            user.setChatId(chatId);
            user.setRole("admin");
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(chatId);
            {
                doReturn(true)
                        .when(adminService)
                        .checkAdmin(chatId);
                {
                    {
                        doReturn(VIEW_CONTACT_INFORMATION.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);
                        message = messageUtils.generateSendButton(update.getMessage().getChatId(),
                                "Ввели не id или не число.\n" +
                                        "Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                        {
                            update.getMessage().setText("text");
                            sendMessageList.add(message);
                            assertEquals(sendMessageList,textService.defineMethod(update));
                            sendMessageList.clear();
                        }
                        update.getMessage().setText("1");
                        message = messageUtils.generateSendMessage(update,contactInformationService.deleteContactInformationById(Long.parseLong(update.getMessage().getText())) + "\n" +
                                "Ввели не id или не число.\nДля удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                        {
                            sendMessageList.add(message);
                            assertEquals(sendMessageList,textService.defineMethod(update));
                            sendMessageList.clear();
                        }
                        update.getMessage().setText("exit");
                        message = messageUtils.generateSendButton(update.getMessage().getChatId(),
                                "Главное меню администратора.");
                        {
                            sendMessageList.add(message);
                            assertEquals(sendMessageList,textService.defineMethod(update));
                            sendMessageList.clear();
                        }
                    }
                    {
                        doReturn(CALL.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);
                        Call call = new Call();

                        call.setAdminChatId(user);
                        User user1 = new User();
                        user1.setChatId(2L);
                        call.setUserChatId(user1);

                        doReturn(call)
                                .when(callRepository)
                                .findByAdminChatId(chatId);

                        message.setChatId(callController.sendMessageToChat(update.getMessage().getChatId()));

                        ArgumentCaptor<Call> callCaptor = ArgumentCaptor.forClass(Call.class);
                        verify(callRepository).save(callCaptor.capture());
                        assertEquals(callCaptor.getValue(), call);

                        message.setText(update.getMessage().getText());

                        doReturn(CALL_A_VOLUNTEER.getStatus())
                                .when(userStatusService)
                                .getUserStatus(2L);
                        {
                            {
                                doReturn(List.of())
                                        .when(userRepository)
                                        .findAllAdmin();
                                assertEquals(0L,callService.createCall(update));
                                message.setReplyMarkup(null);
                                sendMessageList.add(message);
                                assertEquals(sendMessageList,textService.defineMethod(update));
                                sendMessageList.clear();
                            }
                            {
                                sendMessageList.add(message);
                                doReturn(List.of(user1))
                                        .when(userRepository)
                                        .findAllAdmin();
                                message = messageUtils.generateSendButton(urlService.callVolunteer(update), "Закончить разговор");
                                sendMessageList.add(message);

                                assertEquals(sendMessageList,textService.defineMethod(update));
                                sendMessageList.clear();
                            }
                        }
                    }
                    {
                        message = new SendMessage();
                        doReturn(PET_ADD.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        Pet pet = new Pet();
                        pet.setPetName(update.getMessage().getText());

                        user.setAddedPetId(pet);
                        user.setLocationUserOnApp(PET_ADD_NAME.getStatus());

                        doReturn(Optional.of(user))
                                .when(userRepository)
                                .findById(chatId);

                        petService.addPetName(update.getMessage().getChatId(),update.getMessage().getText());

                        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
                        verify(petRepository).save(petCaptor.capture());
                        assertEquals(petCaptor.getValue().toString(), pet.toString());
                        message.setChatId(update.getMessage().getChatId());
                        message.setText("Загрузите изображение и потом добавьте описание животного");

                        sendMessageList.add(message);
                        assertEquals(sendMessageList,textService.defineMethod(update));
                        sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(PET_ADD_IMG.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);
                        Pet pet = new Pet();
                        pet.setId(1L);
                        {
                            {
                                when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
                                assertEquals(petService.getUserById(any(Long.class)),user);
                                {
                                    when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet));
                                    assertEquals(petService.getPetById(any(Long.class)),pet);
                                    pet.setDescription(update.getMessage().getText());
                                    message.setChatId(chatId);
                                    message.setText("Успешно сохранен новый питомец");
                                    sendMessageList.add(message);
                                    sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
                                    assertEquals(sendMessageList,textService.defineMethod(update));
                                    sendMessageList.clear();
                                }
                                {
                                    when(petRepository.findById(any(Long.class))).thenReturn(Optional.empty());
                                    assertThrows(NoSuchElementException.class, () ->
                                        petService.getPetById(any(Long.class)));
                                }

                            }
                            {
                                when(userRepository.findById(chatId)).thenReturn(Optional.empty());
                                assertThrows(NoSuchElementException.class, () ->
                                    petService.getUserById(chatId));
                            }
                        }
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_ABOUT_SHELTER_NAME.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddShelterName(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_ABOUT_SCHEDULE.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddSchedule(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_ABOUT_SECURITY_CONTACTS.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddSecurityContacts(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_INFO_RULES.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddRules(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_INFO_DOCUMENTS.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddDocuments(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(ADD_INFO_TRANSPORTATION.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(adminAddTransportation(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        message = new SendMessage();
                        doReturn(NO_STATUS.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);
                        sendMessageList.add(messageUtils.generateSendButton(chatId, "Главное меню администратора."));
                        assertEquals(sendMessageList,textService.defineMethod(update));
                        sendMessageList.clear();
                    }
                }
            }
            {
                doReturn(false)
                        .when(adminService)
                        .checkAdmin(chatId);
                {
                    {
                        doReturn(NO_STATUS.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userNoStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        doReturn(GET_PET_FORM.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userGetPetFormStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        doReturn(GET_CONTACT_INFORMATION.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userGetContactInformationStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        doReturn(ADD_PHONE.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userAddPhoneStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        doReturn(ADD_PET_REPORT_TEXT.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userAddPetReportTextStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                    {
                        doReturn(CALL_A_VOLUNTEER.getStatus())
                                .when(userStatusService)
                                .getUserStatus(chatId);

                        // sendMessageList.add(userCallToVolunteerStatus(update));
                        // assertEquals(sendMessageList,textService.defineMethod(update));
                        // sendMessageList.clear();
                    }
                }
            }
        }
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