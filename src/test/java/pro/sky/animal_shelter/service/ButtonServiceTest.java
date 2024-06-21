package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.model.Repositories.ContactInformationRepository;
import pro.sky.animal_shelter.model.Repositories.PetRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static pro.sky.animal_shelter.enums.AdminButtonMenuEnum.*;
import static pro.sky.animal_shelter.enums.PetButtonEnum.PET_BUTTON_NEXT;
import static pro.sky.animal_shelter.enums.PetButtonEnum.PET_BUTTON_PREV;
import static pro.sky.animal_shelter.enums.UserButtonEnum.CONTACT_INFORMATION_ADD;
import static pro.sky.animal_shelter.enums.UserButtonEnum.PET_REPORT;

@SpringBootTest
@AutoConfigureMockMvc
class ButtonServiceTest {
    Update update = new Update();
    @MockBean
    UserRepository userRepository;
    @MockBean
    ContactInformationRepository contactInformationRepository;
    @MockBean
    PetRepository petRepository;
    @MockBean
    CallRepository callRepository;
    @MockBean
    AdminService adminService;
    @Autowired
    UserStatusService userStatusService;
    @Autowired
    ContactInformationService contactInformationService;
    @Autowired
    PetService petService;
    @Autowired
    MessageUtils messageUtils;
    @Autowired
    CreateButtonService createButtonService;
    @Autowired
    ButtonService buttonService;
    @Test
    void defineCommand() {
        setUpdate();
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        callbackQuery.setMessage(message);
        User user = new User();
        user.setId(1L);
        callbackQuery.setFrom(user);
        update.setCallbackQuery(callbackQuery);
        SendMessage sendMessage = new SendMessage();
        List<SendMessage> sendMessageList = new ArrayList<>();
        long chatId = update.getCallbackQuery().getFrom().getId();
        // ifAdmin
        {
            doReturn(true)
                    .when(adminService)
                    .checkAdmin(update.getCallbackQuery().getMessage().getChatId());
            {
                StringBuilder newMessage = new StringBuilder();
                callbackQuery.setData(VIEW_CONTACT_INFORMATION_COMMAND.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    doReturn(List.of())
                            .when(contactInformationRepository)
                            .findAll();
                    newMessage.append("Пока никто не оставлял заявок на обратную связь.");
                    sendMessageList.add(messageUtils.generateSendMessage(update, newMessage.toString()));
                    sendMessageList.add(messageUtils.generateSendMessage(update, "Главное меню администратора."));
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                {
                    newMessage = new StringBuilder();
                    ContactInformation contactInformation = new ContactInformation();
                    contactInformation.setId(1L);
                    contactInformation.setPhone("111");
                    contactInformation.setName("qqq");
                    doReturn(List.of(contactInformation))
                            .when(contactInformationRepository)
                            .findAll();
                    newMessage.append(contactInformation.toString()).append("\n");
                    newMessage.append("Для удаления обратной связи введите ее id, для перехода ко всем командам exit или нажмите /start");
                    sendMessageList.add(messageUtils.generateSendMessage(update, newMessage.toString()));
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(PET_ADD_COMMAND.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Введите имя питомца.");
                    sendMessageList.add(sendMessage);
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(ADD_ABOUT.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Введите название приюта.");
                    sendMessageList.add(sendMessage);
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(ADD_INFO.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Введите правила как забрать животное из приюта.");
                    sendMessageList.add(sendMessage);
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(PET_BUTTON_PREV.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    Pet pet = new Pet();
                    pet.setId(1L);
                    pet.setPetName("ff");
                    pet.setDescription("ff");
                    String description = pet.getDescription();
                    String petName = pet.getPetName();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setLocationUserOnApp("view_pet_list");
                    user1.setChatId(1L);
                    user1.setPetId(pet);
                    {
                        doReturn(Optional.of(user1))
                                .when(userRepository)
                                .findById(update.getMessage().getChatId());
                        doReturn(List.of(pet))
                                .when(petRepository)
                                .findAll();
                        doReturn(Optional.of(pet))
                                .when(petRepository)
                                .findById(1L);
                        sendMessage = messageUtils.generateSendButton(chatId,"");

                        sendMessage.setChatId(chatId);
                        sendMessage.setText(petName + "\n" + description);
                        sendMessageList.add(sendMessage);
                        assertEquals(sendMessageList,buttonService.defineCommand(update));
                        sendMessageList.clear();
                    }
                }
                callbackQuery.setData(PET_BUTTON_NEXT.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    Pet pet = new Pet();
                    pet.setId(1L);
                    pet.setPetName("ff");
                    pet.setDescription("ff");
                    String description = pet.getDescription();
                    String petName = pet.getPetName();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setLocationUserOnApp("view_pet_list");
                    user1.setChatId(1L);
                    user1.setPetId(pet);
                    {
                        doReturn(Optional.of(user1))
                                .when(userRepository)
                                .findById(update.getMessage().getChatId());
                        doReturn(List.of(pet))
                                .when(petRepository)
                                .findAll();
                        doReturn(Optional.of(pet))
                                .when(petRepository)
                                .findById(1L);
                        sendMessage = messageUtils.generateSendButton(chatId,"");

                        sendMessage.setChatId(chatId);
                        sendMessage.setText(petName + "\n" + description);
                        sendMessageList.add(sendMessage);
                        assertEquals(sendMessageList,buttonService.defineCommand(update));
                        sendMessageList.clear();
                    }
                }
                callbackQuery.setData("close_call");
                update.setCallbackQuery(callbackQuery);
                {
                    sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Чат с пользователем был закрыт");
                    sendMessageList.add(sendMessage);
                    Call call = new Call();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setChatId(1L);
                    call.setId(1L);
                    call.setAdminChatId(user1);
                    call.setUserChatId(user1);
                    when(callRepository.findByAdminChatId(update.getCallbackQuery().getFrom().getId())).thenReturn(call);
                    doReturn(Optional.of(user1))
                            .when(userRepository)
                            .findById(chatId);
                    sendMessage.setChatId(call.getUserChatId().getChatId());
                    sendMessage.setText("Чат был закрыт для нового обращения /to_call_a_volunteer");
                    sendMessageList.add(sendMessage);
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
            }
        }
        // ifUser
        {
            doReturn(false)
                    .when(adminService)
                    .checkAdmin(update.getCallbackQuery().getMessage().getChatId());
            {
                callbackQuery.setData(PET_REPORT.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    String newMessage = """
            В ежедневный отчет входит следующая информация:
            - Фото животного
            - Рацион животного
            - Общее самочувствие и привыкание к новому месту
            - Изменения в поведении: отказ от старых привычек, приобретение новых

            Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет.""";
                    sendMessageList.add(messageUtils.generateSendMessage(update, newMessage));
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(CONTACT_INFORMATION_ADD.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    String MESSAGE= "Введите номер телефона в формате: +7-9**-***-**-**";
                    sendMessageList.add(messageUtils.generateSendMessage(update, MESSAGE));
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
                }
                callbackQuery.setData(PET_BUTTON_PREV.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    Pet pet = new Pet();
                    pet.setId(1L);
                    pet.setPetName("ff");
                    pet.setDescription("ff");
                    String description = pet.getDescription();
                    String petName = pet.getPetName();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setLocationUserOnApp("view_pet_list");
                    user1.setChatId(1L);
                    user1.setPetId(pet);
                    {
                        doReturn(Optional.of(user1))
                                .when(userRepository)
                                .findById(update.getMessage().getChatId());
                        doReturn(List.of(pet))
                                .when(petRepository)
                                .findAll();
                        doReturn(Optional.of(pet))
                                .when(petRepository)
                                .findById(1L);
                        sendMessage = messageUtils.generateSendButton(chatId,"");

                        sendMessage.setChatId(chatId);
                        sendMessage.setText(petName + "\n" + description);
                        sendMessageList.add(sendMessage);
                        assertEquals(sendMessageList,buttonService.defineCommand(update));
                        sendMessageList.clear();
                    }
                }
                callbackQuery.setData(PET_BUTTON_NEXT.getCommand());
                update.setCallbackQuery(callbackQuery);
                {
                    Pet pet = new Pet();
                    pet.setId(1L);
                    pet.setPetName("ff");
                    pet.setDescription("ff");
                    String description = pet.getDescription();
                    String petName = pet.getPetName();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setLocationUserOnApp("view_pet_list");
                    user1.setChatId(1L);
                    user1.setPetId(pet);
                    {
                        doReturn(Optional.of(user1))
                                .when(userRepository)
                                .findById(update.getMessage().getChatId());
                        doReturn(List.of(pet))
                                .when(petRepository)
                                .findAll();
                        doReturn(Optional.of(pet))
                                .when(petRepository)
                                .findById(1L);
                        sendMessage = messageUtils.generateSendButton(chatId,"");

                        sendMessage.setChatId(chatId);
                        sendMessage.setText(petName + "\n" + description);
                        sendMessageList.add(sendMessage);
                        assertEquals(sendMessageList,buttonService.defineCommand(update));
                        sendMessageList.clear();
                    }
                }
                callbackQuery.setData("close_call");
                update.setCallbackQuery(callbackQuery);
                {
                    sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Чат с пользователем был закрыт");
                    sendMessageList.add(sendMessage);
                    Call call = new Call();
                    pro.sky.animal_shelter.model.User user1 = new pro.sky.animal_shelter.model.User();
                    user1.setChatId(1L);
                    call.setId(1L);
                    call.setAdminChatId(user1);
                    call.setUserChatId(user1);
                    when(callRepository.findByUserChatId(update.getCallbackQuery().getFrom().getId())).thenReturn(call);
                    doReturn(Optional.of(user1))
                            .when(userRepository)
                            .findById(chatId);
                    sendMessage.setChatId(call.getAdminChatId().getChatId());
                    sendMessage.setText("Чат был закрыт для нового обращения /to_call_a_volunteer");
                    sendMessageList.add(sendMessage);
                    assertEquals(sendMessageList,buttonService.defineCommand(update));
                    sendMessageList.clear();
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