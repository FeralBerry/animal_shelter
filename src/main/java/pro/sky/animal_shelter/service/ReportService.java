package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.animal_shelter.controller.TelegramBot;
import pro.sky.animal_shelter.model.*;
import pro.sky.animal_shelter.model.Repositories.AdoptionRepository;
import pro.sky.animal_shelter.model.Repositories.ReportImgRepository;
import pro.sky.animal_shelter.model.Repositories.ReportRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;

import java.util.*;

@Slf4j
@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportImgRepository reportImgRepository;
    private final AdoptionRepository adoptionRepository;
    private final UserRepository userRepository;
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public ReportService(ReportRepository reportRepository, ReportImgRepository reportImgRepository, AdoptionRepository adoptionRepository, UserRepository userRepository){
        this.reportRepository = reportRepository;
        this.reportImgRepository = reportImgRepository;
        this.adoptionRepository = adoptionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Метод проверяет какое животное усыновлено пользователем и сохраняет изображения что пришлет пользователь
     */
    public void addImgReport(Update update, List<String> photos){
        long chatId = update.getMessage().getChatId();
        User user = adoptionRepository.findByUser(getUserById(chatId)).getUser();
        Pet pet = adoptionRepository.findByUser(getUserById(chatId)).getPet();
        List <ReportImg> list = new ArrayList<>();
        for (String photo : photos){
            ReportImg reportImg = new ReportImg();
            reportImg.setPetId(pet.getId());
            reportImg.setChatId(user.getChatId());
            reportImg.setFileId(photo);
            list.add(reportImg);
        }
        reportImgRepository.saveAll(list);
    }
    /**
     * Метод проверяет какое животное усыновлено пользователем и сохраняет текст отчета по этому животному
     */
    public void addTextReport(Update update){
        long nowSec = (new Date().getTime())/1000;
        long chatId = update.getMessage().getChatId();
        User user = adoptionRepository.findByUser(getUserById(chatId)).getUser();
        Pet pet = adoptionRepository.findByUser(getUserById(chatId)).getPet();
        String text = update.getMessage().getText();
        Report report = new Report();
        report.setChatId(user.getChatId());
        report.setText(text);
        report.setPetId(pet.getId());
        report.setUpdatedAt(nowSec);
        reportRepository.save(report);
    }
    /**
     * Метод проверяет список сдавших отчет за последние 24 часа со списком усыновивших
     * @return список не сдавших отчет из усыновивших
     */
    public List<Long> alarmReport(){
        long nowSec = (new Date().getTime())/1000;
        List<Report> reports = reportRepository.findByUpdatedAt(nowSec - 60*60*24);
        List<Long> listReportChatIds = new ArrayList<>();
        List<Long> adoptionChatId = adoptionChatId();
        for (Report report : reports){
            listReportChatIds.add(report.getChatId());
        }
        for (int i = 0; i < adoptionChatId.size();i++){
            for (Long listReportChatId : listReportChatIds) {
                if (Objects.equals(adoptionChatId.get(i), listReportChatId)) {
                    adoptionChatId.remove(i);
                }
            }
        }
        return adoptionChatId;
    }

    /**
     * Метод получает список всех усыновленных животных и их усыновителей и проверяет,
     * прошел ли срок для окончания периода адаптации животного, если да то тем пользователям у которых
     * прошел срок адаптации животного приходит сообщение с поздравлением.
     * @return список кем усыновлены животные
     */
    public List<Long> adoptionChatId(){
        long nowSec = (new Date().getTime())/1000;
        List<Adoption> adoptions = adoptionRepository.findAll();
        SendMessage sendMessage = new SendMessage();
        List<Long> listChatIds = new ArrayList<>();
        for (Adoption adoption : adoptions){
            if((nowSec - adoption.getAdoptAt()) < 14*24*60*60){
                sendMessage.setChatId(adoption.getUser().getChatId());
                sendMessage.setText("Поздравляю с завершением испытательного срока.");
                setView(sendMessage);
                adoptionRepository.delete(adoption);
            } else {
                listChatIds.add(adoption.getUser().getChatId());
            }
        }
        return listChatIds;
    }

    /**
     * @return возвращает список не проверенных отчетов
     */
    public List<Report> rawReports(){
        return reportRepository.findReportIsNotCheck();
    }

    /**
     * Метод получения отчета по id
     * @param id id отчета
     * @return возвращает отчет по id или null если такого отчета нет
     */
    public Report getReport(long id){
        return getReportById(id);
    }

    /**
     * Метод делает пометку, что отчет проверен по его id
     * @param id id отчета
     */
    public void checkReportById(long id){
        Report report = getReportById(id);
        report.setChecked(true);
        reportRepository.save(report);
    }

    /**
     * Метод подготавливает сообщение пользователю сдавшему не корректный отчет
     * @param id id отчета
     * @return возвращает объект с сообщением и id усыновителя
     */
    public SendMessage incorrectReportById(long id){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getReportById(id).getChatId());
        sendMessage.setText("Дорогой усыновитель, мы заметили, что ты заполняешь отчет " +
                "не так подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. " +
                "В противном случае волонтеры приюта будут обязаны самолично проверять условия содержания животного");
        return sendMessage;
    }

    /**
     * Метод меняет время усыновления животного на 14 дней
     * (лучше добавить еще 1 поле в БД для хранения времени когда пройдет адаптационный период)
     * и подготавливает объект с текстом и id чата куда отправить сообщение об увеличении периода адаптации
     * @param id id отчета
     * @return возвращает объект с подготовленным сообщением для пользователя, которому увеличили
     * срок для адаптации животного на 14 дней
     */
    public SendMessage increaseTheAdaptationPeriod14Day(long id) {
        SendMessage sendMessage = new SendMessage();
        long chatId = reportRepository.findByChatId(id).getChatId();
        long nowSec = (new Date().getTime())/1000;
        Adoption adoption = adoptionRepository.findByUser(getUserById(chatId));
        adoption.setAdoptAt(nowSec + 14*24*60*60);
        adoptionRepository.save(adoption);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Было добавлено 14 дней для адаптационного периода.");
        return sendMessage;
    }
    /**
     * Метод меняет время усыновления животного на 30 дней
     * (лучше добавить еще 1 поле в БД для хранения времени когда пройдет адаптационный период)
     * и подготавливает объект с текстом и id чата куда отправить сообщение об увеличении периода адаптации
     * @param id id отчета
     * @return возвращает объект с подготовленным сообщением для пользователя, которому увеличили
     * срок для адаптации животного на 30 дней
     */
    public SendMessage increaseTheAdaptationPeriod30Day(long id) {
        SendMessage sendMessage = new SendMessage();
        long chatId = reportRepository.findByChatId(id).getChatId();
        long nowSec = (new Date().getTime())/1000;
        Adoption adoption = adoptionRepository.findByUser(getUserById(chatId));
        adoption.setAdoptAt(nowSec + 30*24*60*60);
        adoptionRepository.save(adoption);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Было добавлено 30 дней для адаптационного периода.");
        return sendMessage;
    }

    /**
     * Метод для отправки текстового сообщение из этого класса в телеграм бота
     * @param sendMessage объект отправляемого сообщения телеграмм боту
     *                    должен содержать sendMessage.setChatId() и sendMessage.setText()
     */
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    /**
     * Метод проверки существования отчета с таким id
     * @param id id отчета
     * @return возвращает отчет по id или ошибку если такого отчета нет
     */
    public Report getReportById(long id){
        if(reportRepository.findById(id).isPresent()){
            return reportRepository.findById(id).get();
        } else {
            throw new NoSuchElementException("Отечет с id=" + id + " не существует");
        }
    }
    /**
     * Метод проверки существования пользователя с таким id
     * @param chatId id пользователя
     * @return возвращает пользователя по id или ошибку если такого пользователя нет
     */
    public User getUserById(long chatId){
        if(userRepository.findById(chatId).isPresent()){
            return userRepository.findById(chatId).get();
        } else {
            throw new NoSuchElementException("Пользователь с id=" + chatId + " не существует");
        }
    }

}
