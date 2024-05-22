package pro.sky.animal_shelter.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRepository extends JpaRepository<Adoption,Long> {
}
