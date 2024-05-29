package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.About;

public interface AboutRepository extends JpaRepository<About,Long> {
    @Query(value = "SELECT * FROM about order by id desc limit 1", nativeQuery = true)
    About findAbout();
}
