package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "contact_information")
public class ContactInformation {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
