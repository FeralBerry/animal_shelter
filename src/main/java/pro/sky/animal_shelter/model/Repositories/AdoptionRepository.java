package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Adoption;
import pro.sky.animal_shelter.model.User;

public interface AdoptionRepository extends JpaRepository<Adoption,Long> {
    @Query(value = "SELECT * FROM adoption WHERE user = :user LIMIT 1", nativeQuery = true)
    Adoption findByUser(User user);
}
