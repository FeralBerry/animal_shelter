package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Query("SELECT '*' FROM calls WHERE adminChatId = :adminChatId")
    Call findByAdminChatId(long adminChatId);
    @Query("SELECT '*' FROM calls WHERE userChatId = :userChatId")
    Call findByUserChatId(long userChatId);
    @Query("SELECT '*' FROM calls WHERE updatedAt < :updatedAt")
    List<Call> findByChatUpdatedBefore(long updatedAt);
}
