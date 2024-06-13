package pro.sky.animal_shelter.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import pro.sky.animal_shelter.service.AdminService;
import pro.sky.animal_shelter.service.CreateButtonService;
import pro.sky.animal_shelter.service.UserStatusService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static pro.sky.animal_shelter.enums.AdminStatusEnum.CALL;
import static pro.sky.animal_shelter.enums.UserSatausEnum.NO_STATUS;
import static pro.sky.animal_shelter.enums.UserSatausEnum.VIEW_PET_LIST;

@SpringBootTest
@AutoConfigureMockMvc
class MessageUtilsTest {
    Update update = new Update();
    AdminService adminService = Mockito.mock(AdminService.class);
    UserStatusService userStatusService = Mockito.mock(UserStatusService.class);
    CreateButtonService createButtonService = new CreateButtonService();
    MessageUtils messageUtils = new MessageUtils(adminService,userStatusService,createButtonService);

    @Test
    void generateSendMessage() {
        setUpdate();
        String text = "text";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        assertEquals(sendMessage,messageUtils.generateSendMessage(update,text));
    }

    @Test
    void testGenerateSendMessage() {
        setUpdate();
        String text = "text";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        assertEquals(sendMessage,messageUtils.generateSendMessage(update.getMessage().getChatId(),text));
    }

    @Test
    void generateSendButton() {
        setUpdate();
        SendMessage message = new SendMessage();
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        String text = "text";
        long chatId = update.getMessage().getChatId();
        message.setChatId(chatId);
        message.setText(text);
        doReturn(true)
                .when(adminService)
                .checkAdmin(chatId);
        doReturn(NO_STATUS.getStatus())
                .when(userStatusService)
                .getUserStatus(chatId);
        markupInLine.setKeyboard(createButtonService.createButtonToMainMenuAdmin());
        message.setReplyMarkup(markupInLine);
        assertEquals(message,messageUtils.generateSendButton(chatId,text));

        doReturn(CALL.getStatus())
                .when(userStatusService)
                .getUserStatus(chatId);
        markupInLine.setKeyboard(createButtonService.callToUser(text));
        message.setReplyMarkup(markupInLine);
        assertEquals(message,messageUtils.generateSendButton(chatId,text));

        doReturn(VIEW_PET_LIST.getStatus())
                .when(userStatusService)
                .getUserStatus(chatId);
        markupInLine.setKeyboard(createButtonService.createButtonToViewPetList());
        message.setReplyMarkup(markupInLine);
        assertEquals(message,messageUtils.generateSendButton(chatId,text));

        doReturn(false)
                .when(adminService)
                .checkAdmin(chatId);
        doReturn(NO_STATUS.getStatus())
                .when(userStatusService)
                .getUserStatus(chatId);
        markupInLine.setKeyboard(createButtonService.createButtonToUser());
        message.setReplyMarkup(markupInLine);
        assertEquals(message,messageUtils.generateSendButton(chatId,text));

        doReturn(VIEW_PET_LIST.getStatus())
                .when(userStatusService)
                .getUserStatus(chatId);
        markupInLine.setKeyboard(createButtonService.createButtonToViewPetList());
        message.setReplyMarkup(markupInLine);
        assertEquals(message,messageUtils.generateSendButton(chatId,text));
    }

    @Test
    void generateEditMessage() {
        setUpdate();
        String newMessage = "hi";
        EditMessageText message = new EditMessageText();
        message.setChatId(update.getMessage().getChatId());
        message.setMessageId(update.getMessage().getMessageId());
        message.setText(newMessage);
        assertEquals(message,messageUtils.generateEditMessage(update,newMessage));
    }

    @Test
    void sendPhoto() {
        setUpdate();
        String image = "image";
        long chatId = update.getMessage().getChatId();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File file = new File(image);
        InputFile inputFile = new InputFile(file,file.getName());
        sendPhoto.setPhoto(inputFile);
        assertEquals(sendPhoto,messageUtils.sendPhoto(chatId,image));
    }    @Test
    void sendMediaGroup() {
        setUpdate();
        List<String> images = List.of("1","2","3","4");
        List<InputMedia> list = new ArrayList<>();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        long chatId = update.getMessage().getChatId();
        for (String image : images) {
            InputMedia photo = new InputMediaPhoto();
            InputFile file = new InputFile(new File("src/main/resources/img/pets/" + image + ".png"), image);
            photo.setMedia(file.getNewMediaFile(), file.getMediaName());
            list.add(photo);
        }
        sendMediaGroup.setChatId(chatId);
        sendMediaGroup.setMedias(list);
        assertEquals(sendMediaGroup,messageUtils.sendMediaGroup(chatId,images));
    }

    @Test
    void testSendPhoto() {
        setUpdate();
        long chatId = update.getMessage().getChatId();
        String image = "image";
        String caption = "caption";
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File file = new File(image);
        InputFile inputFile = new InputFile(file,file.getName());
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption(caption);
        assertEquals(sendPhoto,messageUtils.sendPhoto(chatId, image, caption));
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