package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String text;
    @OneToOne
    private User user;
    @OneToOne
    private Pet pet;
    @Column(name = "updated_at")
    private Long updatedAt;
    @Column(name = "checked")
    private boolean checked;
    @Column(name = "looked")
    private boolean looked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getChatId() {
        return user;
    }

    public void setChatId(User user) {
        this.user = user;
    }

    public Pet getPetId() {
        return pet;
    }

    public void setPetId(Pet pet) {
        this.pet = pet;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public boolean isLooked() {
        return looked;
    }

    public void setLooked(boolean looked) {
        this.looked = looked;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(text, report.text) && Objects.equals(user, report.user) && Objects.equals(pet, report.pet);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, text, user, pet);
        result = 31 * result;
        return result;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", chatId=" + user +
                ", petId=" + pet +
                '}';
    }
}
