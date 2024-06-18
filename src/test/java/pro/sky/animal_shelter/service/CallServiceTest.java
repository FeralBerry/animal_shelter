package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pro.sky.animal_shelter.enums.UserSatausEnum.CALL_A_VOLUNTEER;
import static pro.sky.animal_shelter.enums.UserSatausEnum.NO_STATUS;

@SpringBootTest
@AutoConfigureMockMvc
class CallServiceTest {
    @MockBean
    UserStatusService userStatusService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CallRepository callRepository;
    @MockBean
    AdminService adminService;
    @Autowired
    CallService callService;
    Update update = new Update();
    @BeforeEach
    void setUp(){
        setUpdate();
    }
    @Test
    void createCallPositive() {
        doNothing()
                .when(userStatusService)
                .changeUserStatus(update.getMessage().getChatId(), CALL_A_VOLUNTEER.getStatus());
        User admin = new User();
        admin.setChatId(1L);
        admin.setFirstName("1L");
        admin.setLastName("1L");
        admin.setUserName("1L");
        admin.setRole("admin");
        doReturn(List.of(admin))
                .when(userRepository)
                .findAllAdmin();
        admin.setLocationUserOnApp("call");
        User user = new User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(update.getMessage().getChatId());
        assertEquals(callService.createCall(update),admin.getChatId());
    }
    @Test
    void createCallNegative() {
        doNothing()
                .when(userStatusService)
                .changeUserStatus(update.getMessage().getChatId(), NO_STATUS.getStatus());
        when(userRepository.findAllAdmin()).thenReturn(List.of());
        assertNull(callService.createCall(update));
    }
    @Test
    void sendMessageChatAdmin() {
        long nowSec = (new Date().getTime())/1000;
        doReturn(true)
                .when(adminService)
                .checkAdmin(update.getMessage().getChatId());
        Call call = new Call();
        User user = new User();
        User admin = new User();
        admin.setChatId(update.getMessage().getChatId());
        user.setChatId(1L);
        call.setAdminChatId(admin);
        call.setUserChatId(user);
        call.setUpdatedAt(nowSec);
        doReturn(call)
                .when(callRepository)
                .findByAdminChatId(update.getMessage().getChatId());
        assertEquals(callService.sendMessageChat(update.getMessage().getChatId()),1L);
        ArgumentCaptor<Call> captor = ArgumentCaptor.forClass(Call.class);
        verify(callRepository).save(captor.capture());
        assertEquals(captor.getValue(), call);
    }
    @Test
    void sendMessageChatUser() {
        long nowSec = (new Date().getTime())/1000;
        doReturn(false)
                .when(adminService)
                .checkAdmin(update.getMessage().getChatId());
        Call call = new Call();
        User user = new User();
        User admin = new User();
        admin.setChatId(1L);
        user.setChatId(update.getMessage().getChatId());
        call.setAdminChatId(admin);
        call.setUserChatId(user);
        call.setUpdatedAt(nowSec);
        doReturn(call)
                .when(callRepository)
                .findByUserChatId(update.getMessage().getChatId());
        assertEquals(callService.sendMessageChat(update.getMessage().getChatId()),1L);
        ArgumentCaptor<Call> captor = ArgumentCaptor.forClass(Call.class);
        verify(callRepository).save(captor.capture());
        assertEquals(captor.getValue(), call);
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