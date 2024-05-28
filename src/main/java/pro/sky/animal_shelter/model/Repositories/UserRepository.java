package pro.sky.animal_shelter.model.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT '*' FROM users WHERE role = 'admin' AND locationUserOnApp != 'call'")
    List<User> findAllAdmin();
}
