package pro.sky.animal_shelter.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import pro.sky.animal_shelter.model.Call;
import pro.sky.animal_shelter.model.Repositories.CallRepository;
import pro.sky.animal_shelter.service.ReportService;
import pro.sky.animal_shelter.service.UserStatusService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static pro.sky.animal_shelter.enums.UserSatausEnum.CALL_A_VOLUNTEER;
import static pro.sky.animal_shelter.enums.UserSatausEnum.NO_STATUS;
@SpringBootTest
@AutoConfigureMockMvc
class TelegramBotTest {
    @Mock
    CallRepository callRepository;
    UserStatusService userStatusService = Mockito.mock(UserStatusService.class);
    Update update = new Update();
    UpdateController updateController = Mockito.mock(UpdateController.class);
    AdminReportController adminReportController = Mockito.mock(AdminReportController.class);
    ReportService reportService = Mockito.mock(ReportService.class);
    TelegramBot telegramBot = Mockito.mock(TelegramBot.class);

    @Test
    void init() {
        doNothing()
                .when(updateController)
                .registerBot(telegramBot);
        doNothing()
                .when(adminReportController)
                .registerBot(telegramBot);
        doNothing()
                .when(reportService)
                .registerBot(telegramBot);
    }

    @Test
    void getBotUsername() {
        String expected = "bot_name";
        doReturn(expected)
                .when(telegramBot)
                .getBotToken();
        assertEquals(expected,telegramBot.getBotToken());
    }

    @Test
    void getBotToken() {
        String expected = "bot_token";
        doReturn(expected)
                .when(telegramBot)
                .getBotToken();
        assertEquals(expected,telegramBot.getBotToken());
    }

    @Test
    void onUpdateReceived() {
        setUpdate();
        doNothing()
                .when(telegramBot)
                .onUpdateReceived(update);
    }

    @Test
    void sendAnswerMessage() {
        SendMessage sendMessage = new SendMessage();
        doNothing()
                .when(telegramBot)
                .sendAnswerMessage(sendMessage);
    }

    @Test
    void testSendAnswerMessage() {
        SendPhoto sendPhoto = new SendPhoto();
        doNothing()
                .when(telegramBot)
                .sendAnswerMessage(sendPhoto);
    }

    @Test
    void testSendAnswerMessage1() {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        doNothing()
                .when(telegramBot)
                .sendAnswerMessage(sendMediaGroup);
    }

    @Test
    void testSendAnswerMessage2() {
        EditMessageText editMessageText = new EditMessageText();
        doNothing()
                .when(telegramBot)
                .sendAnswerMessage(editMessageText);
    }

    @Test
    void downloadPhotos() {
        setUpdate();
        String url = "1";
        GetFile getFile = new GetFile();
        PhotoSize photoSize = new PhotoSize();
        List<PhotoSize> photoSizeList = new ArrayList<>();
        photoSize.setFileId("1");
        photoSizeList.add(photoSize);
        photoSize.setFileId("2");
        photoSizeList.add(photoSize);
        update.getMessage().setPhoto(photoSizeList);
        List<PhotoSize> photos = update.getMessage().getPhoto();
        List<String> petPhotos = new ArrayList<>();
        List<String> largePhotos = new ArrayList<>();
        for (PhotoSize photo : photos){
            getFile.setFileId(photo.getFileId());
            petPhotos.add(photo.getFileId());
            for (int i = 0; i < petPhotos.size(); i++){
                if((i + 1) % 3 == 0){
                    largePhotos.add(petPhotos.get(i));
                }
            }
        }
        assertEquals(largePhotos,telegramBot.downloadPhotos(update, url));
    }

    @Test
    void closeChat() {
        long nowSec = (new Date().getTime())/1000;
        Call call = new Call();
        pro.sky.animal_shelter.model.User user = new pro.sky.animal_shelter.model.User();
        user.setChatId(1L);
        pro.sky.animal_shelter.model.User admin = new pro.sky.animal_shelter.model.User();
        admin.setChatId(2L);
        List<Call> callList = new ArrayList<>();
        call.setUpdatedAt(1L);
        call.setUserChatId(user);
        call.setAdminChatId(admin);
        call.setId(1L);
        callList.add(call);
        call.setUpdatedAt(2L);
        user.setChatId(3L);
        admin.setChatId(4L);
        call.setUserChatId(user);
        call.setAdminChatId(admin);
        call.setId(2L);
        callList.add(call);
        doReturn(callList)
                .when(callRepository)
                .findByChatUpdatedBefore(nowSec);
        List<Call> expectedCallList = callRepository.findByChatUpdatedBefore(nowSec);
        for (int i = 0; i < expectedCallList.size(); i++){
            call = expectedCallList.get(i);
            doReturn(CALL_A_VOLUNTEER.getStatus())
                    .when(userStatusService)
                    .getUserStatus(call.getUserChatId().getChatId());
            if((call.getUpdatedAt() + 3600L) > nowSec
                    || !userStatusService.getUserStatus(call.getUserChatId().getChatId()).equals(CALL_A_VOLUNTEER.getStatus())){
                doReturn(expectedCallList.remove(call))
                        .when(callRepository)
                        .delete(call);
                callRepository.delete(call);
                doReturn(NO_STATUS.getStatus())
                        .when(userStatusService)
                        .getUserStatus(call.getUserChatId().getChatId());
                doReturn(userStatusService.getUserStatus(call.getUserChatId().getChatId()))
                        .when(userStatusService)
                        .changeUserStatus(call.getUserChatId().getChatId(), NO_STATUS.getStatus());
                userStatusService.changeUserStatus(call.getUserChatId().getChatId(), NO_STATUS.getStatus());
                SendMessage message = new SendMessage();
                message.setChatId(call.getUserChatId().getChatId());
                message.setText("Чат закрыт автоматически для нового обращения /to_call_a_volunteer");
                doNothing()
                        .when(telegramBot)
                        .sendAnswerMessage(message);
                doReturn(NO_STATUS.getStatus())
                        .when(userStatusService)
                        .getUserStatus(call.getAdminChatId().getChatId());
                doReturn(userStatusService.getUserStatus(call.getAdminChatId().getChatId()))
                        .when(userStatusService)
                        .changeUserStatus(call.getAdminChatId().getChatId(), NO_STATUS.getStatus());
                userStatusService.changeUserStatus(call.getAdminChatId().getChatId(), NO_STATUS.getStatus());
                message.setChatId(call.getAdminChatId().getChatId());
                message.setText("Чат с пользователем был закрыт");
                doNothing()
                        .when(telegramBot)
                        .sendAnswerMessage(message);
            }
        }
        telegramBot.closeChat();
    }

    @Test
    void alarmReport() {
        List<pro.sky.animal_shelter.model.User> chatIds = reportService.alarmReport();
        if(!chatIds.isEmpty()){
            for (pro.sky.animal_shelter.model.User chatId : chatIds){
                SendMessage message = new SendMessage();
                message.setChatId(chatId.getChatId());
                message.setText("Вы забыли про отчет по питомцу");
                doNothing()
                        .when(telegramBot)
                        .sendAnswerMessage(message);
            }
        }
        telegramBot.alarmReport();
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