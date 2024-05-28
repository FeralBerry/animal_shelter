package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        for (Adoption adoption : adoptions){
            listChatIds.add(adoption.getUser().getChatId());
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
}
