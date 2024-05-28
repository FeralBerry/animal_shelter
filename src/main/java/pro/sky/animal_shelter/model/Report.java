package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] img;
    private String text;
    private Long chatId;
    private Long petId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Arrays.equals(img, report.img) && Objects.equals(text, report.text) && Objects.equals(chatId, report.chatId) && Objects.equals(petId, report.petId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, text, chatId, petId);
        result = 31 * result + Arrays.hashCode(img);
        return result;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", img=" + Arrays.toString(img) +
                ", text='" + text + '\'' +
                ", chatId=" + chatId +
                ", petId=" + petId +
                '}';
    }
}
