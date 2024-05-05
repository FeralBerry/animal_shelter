package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.ReportRepository;

@Slf4j
@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    public void sendReportForm(long chatId, )
}
