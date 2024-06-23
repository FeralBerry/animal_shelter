package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "SELECT * FROM users WHERE role = 'admin' AND location_user_on_app != 'call'",nativeQuery = true)
    List<User> findAllAdmin();
    @Query(value = "SELECT * FROM users WHERE chat_id = :chatId",nativeQuery = true)
    Optional<User> findByChatId(Long chatId);
}
