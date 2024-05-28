package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PetsImgRepository extends CrudRepository<PetsImg,Long> {
    @Query("SELECT '*' from pets_img where petId = :petId")
    List<PetsImg> findPetsImgByPetId(long petId);
}
