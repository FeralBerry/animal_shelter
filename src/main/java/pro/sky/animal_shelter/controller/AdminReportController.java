package pro.sky.animal_shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.sky.animal_shelter.model.Report;
import pro.sky.animal_shelter.service.ReportService;

import java.util.List;

@Tag(name = "Контроллер администратора работа с отчетами", description = "Контроллер администратора для действий с отчетами")
@RestController
@RequestMapping("api/admin")
public class AdminReportController {
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }
    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/raw-reports")
    @Operation(description = "Возвращает список не проверенных отчетов")
    public List<Report> getStartMessage() {
        return reportService.rawReports();
    }
    @GetMapping("/report/{id}")
    @Operation(description = "Получение отчета по его id")
    public Report getReportById(@PathVariable long id){
        return reportService.getReport(id);
    }
    @GetMapping("/check/report/{id}")
    @Operation(description = "Проставление отметки отчет принят")
    public void checkReportById(@PathVariable long id){
        reportService.checkReportById(id);
    }
    @GetMapping("/incorrect-report/{id}")
    @Operation(description = "Отправка сообщения, если отчет сдан не верно")
    public void incorrectReportById(@PathVariable long id){
        setView(reportService.incorrectReportById(id));
    }
    @PostMapping("/increase-the-adaptation-period/14day/{chatId}")
    @Operation(description = "Добавляет 14 дней адаптационного периода для животного")
    public void increaseTheAdaptationPeriod14Day(@PathVariable String chatId){
        setView(reportService.increaseTheAdaptationPeriod14Day(Long.parseLong(chatId)));
    }
    @PostMapping("/increase-the-adaptation-period/30day/{chatId}")
    @Operation(description = "Добавляет 30 дней адаптационного периода для животного")
    public void increaseTheAdaptationPeriod30Day(@PathVariable String chatId){
        setView(reportService.increaseTheAdaptationPeriod30Day(Long.parseLong(chatId)));
    }
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
