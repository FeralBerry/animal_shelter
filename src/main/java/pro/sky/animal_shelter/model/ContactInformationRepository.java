package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ContactInformationRepository extends JpaRepository<ContactInformation,Long> {
    @Modifying
    @Query(value = "DELETE FROM contact_information WHERE id = ?", nativeQuery = true)
    void deleteByIdContactInformation(long id);
}
