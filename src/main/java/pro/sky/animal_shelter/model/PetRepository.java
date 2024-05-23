package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PetRepository extends JpaRepository<Pet,Long> {
    @Query(
            value = "SELECT * FROM PETS LIMIT 1",
            nativeQuery = true)
    Pet findLimitPet();
}
