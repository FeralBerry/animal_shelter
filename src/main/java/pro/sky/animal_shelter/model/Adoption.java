package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "adoptions")
public class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Pet pet;
    @OneToOne
    private User user;
    private Long adoptAt;

    public Long getAdoptAt() {
        return adoptAt;
    }

    public void setAdoptAt(Long adoptAt) {
        this.adoptAt = adoptAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adoption adoption = (Adoption) o;
        return Objects.equals(id, adoption.id) && Objects.equals(pet, adoption.pet) && Objects.equals(user, adoption.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pet, user);
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", pet=" + pet +
                ", user=" + user +
                '}';
    }
}
