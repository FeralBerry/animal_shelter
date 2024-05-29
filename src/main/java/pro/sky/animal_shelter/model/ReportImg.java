package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "reports_img")
public class ReportImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long petId;
    private Long chatId;
    private String fileId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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
        return Objects.equals(id, reportImg.id) && Objects.equals(petId, reportImg.petId) && Objects.equals(chatId, reportImg.chatId) && Objects.equals(fileId, reportImg.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petId, chatId, fileId);
    }

    @Override
    public String toString() {
        return "ReportImg{" +
                "id=" + id +
                ", petId=" + petId +
                ", chatId=" + chatId +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
