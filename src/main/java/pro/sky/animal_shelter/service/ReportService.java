package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.animal_shelter.model.ReportRepository;

@Slf4j
@Service
public class ReportService {
    /**
     * Создаём поле reportRepository для дальнейшего внедрения зависимости и использования
     * методов класса ReportRepository
     */
    private final ReportRepository reportRepository;

    /**
     * Конструктор класса ReportService, в котором инициализируем поле reportRepository (внедряем зависимость)
     * @param reportRepository
     */
    public ReportService(ReportRepository reportRepository){
        this.reportRepository = reportRepository;
    }

    /**
     * Метод проверяет, что пришло в сообщении.
     * @param message объект класса Message, полученный из обрабатываемого обновления (объекта Update)
     * @return true, если message содержит изображение, false - если нет.
     */
    public boolean addImgReport(Message message){
        // проверяем что пришло в сообщении если картинка то возвращаем true
        // если не картинка то возвращаем false
        return false;
    }

    /**
     * Метод проверяет, что пришло в сообщении.
     * @param message объект класса Message, полученный из обрабатываемого обновления (объекта Update)
     * @return true, если message содержит текст, false - если нет.
     */
    public boolean addTextReport(Message message){
        // проверяем что пришло в сообщении если текст то возвращаем true
        // если не текст то возвращаем false
        return false;
    }
}
