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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
     * Метод проверяет, что пришло в сообщении.

     */
    public void addImgReport(Update update, List<String> photos){
        long chatId = update.getMessage().getChatId();
        User user = adoptionRepository.findByUser(userRepository.findById(chatId).get()).getUser();
        Pet pet = adoptionRepository.findByUser(userRepository.findById(chatId).get()).getPet();
        List <ReportImg> list = new ArrayList<>();
        for (String photo : photos){
            ReportImg reportImg = new ReportImg();
            reportImg.setPetId(pet.getId());
            reportImg.setChatId(user.getChatId());
            reportImg.setChatId(user.getChatId());
            reportImg.setFileId(photo);
            list.add(reportImg);
        }
        reportImgRepository.saveAll(list);
    }

    /**
     * Метод проверяет, что пришло в сообщении.
     */
    public void addTextReport(Update update){
        long nowSec = (new Date().getTime())/1000;
        long chatId = update.getMessage().getChatId();
        User user = adoptionRepository.findByUser(userRepository.findById(chatId).get()).getUser();
        Pet pet = adoptionRepository.findByUser(userRepository.findById(chatId).get()).getPet();
        String text = update.getMessage().getText();
        Report report = new Report();
        report.setChatId(user.getChatId());
        report.setText(text);
        report.setPetId(pet.getId());
        report.setUpdatedAt(nowSec);
        reportRepository.save(report);
    }
    public List<Long> alarmReport(){
        long nowSec = (new Date().getTime())/1000;
        List<Adoption> adoptions = adoptionRepository.findAll();
        List<Report> reports = reportRepository.findByUpdatedAt(nowSec - 60*60*24);
        List<Long> listChatIds = new ArrayList<>();
        List<Long> listReportChatIds = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        for (Adoption adoption : adoptions){
            if((nowSec - adoption.getAdoptAt()) < 14*24*60*60){
                sendMessage.setChatId(adoption.getUser().getChatId());
                sendMessage.setText("Поздравляю с завершением испытательного срока.");
                adoptionRepository.delete(adoption);
            } else {
                listChatIds.add(adoption.getUser().getChatId());
            }
        }
        for (Report report : reports){
            listReportChatIds.add(report.getChatId());
        }
        for (int i = 0; i < listChatIds.size();i++){
            for (Long listReportChatId : listReportChatIds) {
                if (Objects.equals(listChatIds.get(i), listReportChatId)) {
                    listChatIds.remove(i);
                }
            }
        }
        return listChatIds;
    }
    public List<Report> rawReports(){
        return reportRepository.findReportIsNotCheck();
    }
    public Report getReport(long id){
        if(reportRepository.findById(id).isPresent()){
            Report report = reportRepository.findById(id).get();
            report.setLooked(true);
            reportRepository.save(report);
            return report;
        } else {
            return null;
        }
    }
    public void checkReportById(long id){
        if(reportRepository.findById(id).isPresent()){
            Report report = reportRepository.findById(id).get();
            report.setChecked(true);
            reportRepository.save(report);
        }
    }
    public SendMessage incorrectReportById(long id){
        SendMessage sendMessage = new SendMessage();
        if(reportRepository.findById(id).isPresent()){
            Report report = reportRepository.findById(id).get();
            sendMessage.setChatId(report.getChatId());
            sendMessage.setText("Дорогой усыновитель, мы заметили, что ты заполняешь отчет " +
                    "не так подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. " +
                    "В противном случае волонтеры приюта будут обязаны самолично проверять условия содержания животного");
        }
        return sendMessage;
    }
    public SendMessage increaseTheAdaptationPeriod14Day(long chatId) {
        SendMessage sendMessage = new SendMessage();
        long nowSec = (new Date().getTime())/1000;
        Adoption adoption = new Adoption();
        if(userRepository.findById(chatId).isPresent()){
            adoption = adoptionRepository.findByUser(userRepository.findById(chatId).get());
            adoption.setAdoptAt(nowSec + 14*24*60*60);
        }
        adoptionRepository.save(adoption);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Было добавлено 14 дней для адаптационного периода.");
        return sendMessage;
    }
    public SendMessage increaseTheAdaptationPeriod30Day(long chatId) {
        SendMessage sendMessage = new SendMessage();
        long nowSec = (new Date().getTime())/1000;
        Adoption adoption = new Adoption();
        if(userRepository.findById(chatId).isPresent()){
            adoption = adoptionRepository.findByUser(userRepository.findById(chatId).get());
            adoption.setAdoptAt(nowSec + 30*24*60*60);
        }
        adoptionRepository.save(adoption);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Было добавлено 30 дней для адаптационного периода.");
        return sendMessage;
    }
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }


}
