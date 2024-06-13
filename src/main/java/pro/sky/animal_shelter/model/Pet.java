package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

@Entity(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pet_name", length = 100)
    private String petName;
    @Column(name = "description")
    private String description;
    public Long getId() {
        return id;
    }
    public String getPetName() {
        return petName;
    }
    public String getDescription() {
        return description;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
