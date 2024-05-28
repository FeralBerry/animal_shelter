package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.model.ContactInformation;

public interface ContactInformationRepository extends JpaRepository<ContactInformation,Long> {
}
