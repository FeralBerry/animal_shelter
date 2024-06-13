package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "reports_img")
public class ReportImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Pet pet;
    @ManyToOne
    private User user;
    @Column(name = "file_id")
    private String fileId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPetId() {
        return pet;
    }

    public void setPetId(Pet pet) {
        this.pet = pet;
    }

    public User getChatId() {
        return user;
    }

    public void setChatId(User user) {
        this.user = user;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportImg reportImg = (ReportImg) o;
        return Objects.equals(id, reportImg.id) && Objects.equals(pet, reportImg.pet) && Objects.equals(user, reportImg.user) && Objects.equals(fileId, reportImg.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pet, user, fileId);
    }

    @Override
    public String toString() {
        return "ReportImg{" +
                "id=" + id +
                ", petId=" + pet +
                ", chatId=" + user +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
