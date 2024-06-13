package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.Pet;
import pro.sky.animal_shelter.model.PetsImg;
import pro.sky.animal_shelter.model.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;


class ReportServiceTest {
    Update update = new Update();
    ReportService reportServiceMock = Mockito.mock(ReportService.class);
    @Test
    void registerBot() {
    }
    @ParameterizedTest
    @MethodSource("petPhotos")
    void addImgReport(List<String> photos) {
        setUpdate();
        doNothing()
                .when(reportServiceMock)
                .addImgReport(update,photos);
    }
    @Test
    void addTextReport() {
        setUpdate();
        doNothing()
                .when(reportServiceMock)
                .addTextReport(update);
    }

    @Test
    void alarmReport() {
        List<Long> expected = new ArrayList<>();
        expected.add(1L);
        expected.add(2L);
        doReturn(expected)
                .when(reportServiceMock)
                .alarmReport();
        assertEquals(expected,reportServiceMock.alarmReport());
    }

    @Test
    void adoptionChatId() {
        List<Long> expected = new ArrayList<>();
        doReturn(expected)
                .when(reportServiceMock)
                .adoptionChatId();
        assertEquals(expected,reportServiceMock.adoptionChatId());
    }

    @Test
    void rawReports() {
        List<Report> expected = new ArrayList<>();
        doReturn(expected)
                .when(reportServiceMock)
                .rawReports();
        assertEquals(expected,reportServiceMock.rawReports());
    }

    @Test
    void getReport() {
        Report expected = new Report();
        doReturn(expected)
                .when(reportServiceMock)
                .getReport(1);
        assertEquals(expected,reportServiceMock.getReport(1));
    }

    @Test
    void checkReportById() {
        doNothing()
                .when(reportServiceMock)
                .checkReportById(1);
    }

    @Test
    void incorrectReportById() {
        SendMessage sendMessage = new SendMessage();
        long chatId = 1L;
        doReturn(sendMessage)
                .when(reportServiceMock)
                .incorrectReportById(chatId);
        assertEquals(sendMessage,reportServiceMock.incorrectReportById(chatId));
    }

    @Test
    void increaseTheAdaptationPeriod14Day() {
        SendMessage sendMessage = new SendMessage();
        long chatId = 1L;
        doReturn(sendMessage)
                .when(reportServiceMock)
                .increaseTheAdaptationPeriod14Day(chatId);
        assertEquals(sendMessage,reportServiceMock.increaseTheAdaptationPeriod14Day(chatId));
    }

    @Test
    void increaseTheAdaptationPeriod30Day() {
        SendMessage sendMessage = new SendMessage();
        long chatId = 1L;
        doReturn(sendMessage)
                .when(reportServiceMock)
                .increaseTheAdaptationPeriod30Day(chatId);
        assertEquals(sendMessage,reportServiceMock.increaseTheAdaptationPeriod30Day(chatId));
    }

    @Test
    void getReportById() {
        Report report = new Report();
        long chatId = 1L;
        doReturn(report)
                .when(reportServiceMock)
                .getReportById(chatId);
        assertEquals(report,reportServiceMock.getReportById(chatId));
    }

    @Test
    void getUserById() {
    }
    static Stream<Arguments> petPhotos(){
        List<String> photos = new ArrayList<>();
        photos.add("1");
        photos.add("2");
        photos.add("3");
        return Stream.of(
                Arguments.of(List.of(
                        photos
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