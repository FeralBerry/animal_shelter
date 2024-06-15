package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Info;

public interface InfoRepository extends JpaRepository<Info,Long> {
    @Query(value = "SELECT * FROM info order by id desc limit 1", nativeQuery = true)
    Info findInfo();
}
