package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.model.Info;

public interface InfoRepository extends JpaRepository<Info,Long> {

}
