package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.sky.animal_shelter.model.PetsImg;

import java.util.List;

public interface PetsImgRepository extends CrudRepository<PetsImg,Long> {
    @Query(value = "SELECT * from pets_img where petId = :petId",nativeQuery = true)
    List<PetsImg> findPetsImgByPetId(long petId);
}
