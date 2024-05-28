package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.ReportRepository;

@Slf4j
@Service
public class ReportService {
    private final ReportRepository reportRepository;

    /**
     *
     * @param reportRepository
     */
    public ReportService(ReportRepository reportRepository){
        this.reportRepository = reportRepository;
    }

    /**
     *
     * @param message
     * @return
     */
    public boolean addImgReport(Message message){
        // проверяем что пришло в сообщении если картинка то возвращаем true
        // если не картинка то возвращаем false
        return false;
    }

    /**
     *
     * @param message
     * @return
     */
    public boolean addTextReport(Message message){
        // проверяем что пришло в сообщении если текст то возвращаем true
        // если не текст то возвращаем false
        return false;
    }
}
