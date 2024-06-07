package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Long> {
    @Query(value = "select * from report where updated_at > :updatedAt",nativeQuery = true)
    List<Report> findByUpdatedAt(Long updatedAt);
    @Query(value = "select * from report where chat_id = :chatId",nativeQuery = true)
    Report findByChatId(Long chatId);
    @Query(value = "select * from report where checked = false", nativeQuery = true)
    List<Report> findReportIsNotCheck();
}
