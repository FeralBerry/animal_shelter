package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Call;

import java.util.List;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Query(value = "SELECT * FROM calls WHERE admin_chat_id = :adminChatId",nativeQuery = true)
    Call findByAdminChatId(long adminChatId);
    @Query(value = "SELECT * FROM calls WHERE user_chat_id = :userChatId", nativeQuery = true)
    Call findByUserChatId(long userChatId);
    @Query(value = "SELECT * FROM calls WHERE updated_at < :updatedAt", nativeQuery = true)
    List<Call> findByChatUpdatedBefore(long updatedAt);
}
