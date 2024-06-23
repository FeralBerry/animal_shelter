package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.ContactInformation;

import java.util.Optional;

public interface ContactInformationRepository extends JpaRepository<ContactInformation,Long> {
    @Query(value = "SELECT * FROM contact_information where user_chat_id = :chatId", nativeQuery = true)
    Optional<ContactInformation> findByUserId(Long chatId);
}
