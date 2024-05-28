package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.Call;

import java.util.List;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Query(value = "SELECT * FROM calls WHERE adminChatId = :adminChatId",nativeQuery = true)
    Call findByAdminChatId(long adminChatId);
    @Query(value = "SELECT * FROM calls WHERE userChatId = :userChatId", nativeQuery = true)
    Call findByUserChatId(long userChatId);
    @Query(value = "SELECT * FROM calls WHERE updatedAt < :updatedAt", nativeQuery = true)
    List<Call> findByChatUpdatedBefore(long updatedAt);
}
