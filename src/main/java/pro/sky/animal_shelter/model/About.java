package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "about")
public class About {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String shelterName;
    private String schedule;
    private String securityContacts;
    private String safetyPrecautions;
    public Long getId() {
        return id;
    }
    public String getShelterName() {
        return shelterName;
    }
    public String getSchedule() {
        return schedule;
    }
    public String getSecurityContacts() {
        return securityContacts;
    }
    public String getSafetyPrecautions() {
        return safetyPrecautions;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    public void setSecurityContacts(String securityContacts) {
        this.securityContacts = securityContacts;
    }
    public void setSafetyPrecautions(String safetyPrecautions) {
        this.safetyPrecautions = safetyPrecautions;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        About about = (About) o;
        return Objects.equals(id, about.id) && Objects.equals(shelterName, about.shelterName) && Objects.equals(schedule, about.schedule) && Objects.equals(securityContacts, about.securityContacts) && Objects.equals(safetyPrecautions, about.safetyPrecautions);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, shelterName, schedule, securityContacts, safetyPrecautions);
    }
    @Override
    public String toString() {
        return "About{" +
                "id=" + id +
                ", shelterName='" + shelterName + '\'' +
                ", schedule='" + schedule + '\'' +
                ", securityContacts='" + securityContacts + '\'' +
                ", safetyPrecautions='" + safetyPrecautions + '\'' +
                '}';
    }
}
