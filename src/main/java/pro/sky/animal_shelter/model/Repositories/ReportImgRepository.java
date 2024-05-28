package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.PetsImg;
import pro.sky.animal_shelter.model.ReportImg;

import java.util.List;

public interface ReportImgRepository extends JpaRepository<ReportImg,Long> {
    @Query(value = "SELECT * from reports_img where petId = :petId",nativeQuery = true)
    List<PetsImg> findReportImgByPetId(long petId);
}
