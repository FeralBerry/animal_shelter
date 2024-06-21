package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.model.ContactInformation;
import pro.sky.animal_shelter.model.Repositories.ContactInformationRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactInformationServiceTest {
    Update update = new Update();

    User user = new User();

    @MockBean
    ContactInformationRepository contactInformationRepository;

    @MockBean
    UserRepository userRepository;


    @Autowired
    ContactInformationService contactInformationService;


    private static final long TEST_CHAT_ID = 982721415L;

    private static final String TEST_NAME = "Вася";

    private static final String TEST_PHONE = "+7(999)9999999";
    private static final String TEST_WRONG_PHONE = "+7-999-999-99-99";



    //ContactInformationService contactInformationServiceMock = Mockito.mock(ContactInformationService.class);
    @Test
    public void getContactInformation() {
        String expected = "Введите номер телефона в формате: +7-9**-***-**-**";
        assertEquals(expected,contactInformationService.getContactInformation());
    }
    @Test

    public void addContactPhonePositiveTest() {
        setUpdate();
        setUser();
        Message message = update.getMessage();
        message.setText(TEST_PHONE);
        update.setMessage(message);
        when(userRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.of(user));
        assertTrue(contactInformationService.addContactPhone(update));

    }
    @Test
    public void addContactPhoneNegativeTestWhenNoUser(){
        setUpdate();
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        assertThrows(RuntimeException.class, () -> {
            contactInformationService.addContactPhone(update);
        });
    }
    @Test
    public void addContactPhoneNegativeTestWhenDoesNotMatch(){
        setUpdate();
        Message message = update.getMessage();
        message.setText(TEST_WRONG_PHONE);
        update.setMessage(message);
        setUser();
        when(userRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.of(user));
        assertFalse(contactInformationService.addContactPhone(update));
    }
    @Test
    public void  addContactNamePositiveTest(){
        setUpdate();
        Message message = update.getMessage();
        message.setText(TEST_NAME);
        update.setMessage(message);
        ContactInformation contactInformation = new ContactInformation();
        when(contactInformationRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.of(contactInformation));
        assertTrue(contactInformationService.addContactName(update));
    }
    @Test
    public void addContactNameNegativeTestWhenMessageTextIsEmpty(){
        setUpdate();
        Message message = update.getMessage();
        message.setText("");
        update.setMessage(message);
        assertFalse(contactInformationService.addContactName(update));
    }
    @Test
    public void addContactNameNegativeTestWhenNoContactInformation(){
        setUpdate();
        Message message = update.getMessage();
        message.setText(TEST_NAME);
        update.setMessage(message);
        when(contactInformationRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.ofNullable(null));
        assertThrows(NoSuchElementException.class, () -> {
            contactInformationService.addContactName(update);
        });
    }
    @Test
    public void getAllContactInformationPositiveTest(){
        List<ContactInformation> contactInformations = List.of(new ContactInformation());
        when(contactInformationRepository.findAll()).thenReturn(contactInformations);
        assertEquals(contactInformationService.getAllContactInformation(), contactInformations);
    }
    @Test
    public void deleteContactInformationByIdPositiveTest(){
        ContactInformation contactInformation = new ContactInformation();
        when(contactInformationRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.of(contactInformation));
        assertEquals(contactInformationService.deleteContactInformationById(TEST_CHAT_ID), "Обратная связь под id: " + TEST_CHAT_ID + " успешно удалена");
    }
    @Test
    public void deleteContactInformationByIdNegativeTest(){
        when(contactInformationRepository.findById(TEST_CHAT_ID)).thenReturn(Optional.ofNullable(null));
        assertEquals(contactInformationService.deleteContactInformationById(TEST_CHAT_ID), "Обратная связь под id не найдена");
    }

    public void setUpdate(){
        update.setUpdateId(193484977);
        Message message = new Message();
        message.setMessageId(2273);
        message.setMessageThreadId(null);
        message.setDate(1717508990);
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(TEST_CHAT_ID);
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
        chat.setId(TEST_CHAT_ID);
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
        message.setText(null);
        MessageEntity messageEntity = new MessageEntity();
        List<MessageEntity> entityList = new ArrayList<>();
        messageEntity.setType("bot_command");
        messageEntity.setOffset(0);
        messageEntity.setLength(6);
        messageEntity.setUrl(null);
        messageEntity.setUser(null);
        messageEntity.setLanguage(null);
        messageEntity.setCustomEmojiId(null);
        messageEntity.setText(TEST_PHONE);
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
    public void setUser(){
        user.setChatId(TEST_CHAT_ID);
        user.setFirstName("1");
        user.setLastName("1");
        user.setUserName("1");
        user.setRole("1");
        user.setLocationUserOnApp("1");
        user.setPetId(null);
        user.setAddedPetId(null);
    }
}