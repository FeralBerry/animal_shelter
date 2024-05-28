package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Pet;


public interface PetRepository extends JpaRepository<Pet,Long> {
    @Query(
            value = "SELECT * FROM PETS LIMIT 1",
            nativeQuery = true)
    Pet findLimitPet();
    @Query(value = "SELECT * FROM pets WHERE id > :id LIMIT 1", nativeQuery = true)
    Pet findNextPet(long id);
    @Query(value = "SELECT * FROM pets WHERE id < :id LIMIT 1", nativeQuery = true)
    Pet findPrevPet(long id);
    @Query(value = "SELECT id FROM pets order by id asc limit 1",nativeQuery = true)
    long findIdFirstPet();
    @Query(value = "SELECT id FROM pets order by id desc limit 1",nativeQuery = true)
    long findIdLastPet();
}
