package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Query("SELECT '*' FROM calls WHERE adminChatId = ?")
    Call findByAdminChatId(long adminChatId);
    @Query("SELECT '*' FROM calls WHERE userChatId = ?")
    Call findByUserChatId(long userChatId);
}
