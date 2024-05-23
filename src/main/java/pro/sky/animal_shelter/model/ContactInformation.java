package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "contact_information")
public class ContactInformation {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String phone;
    private String name;
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public Long getChatId() {
        return chatId;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInformation that = (ContactInformation) o;
        return Objects.equals(id, that.id) && Objects.equals(phone, that.phone) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, name);
    }

    @Override
    public String toString() {
        return "ContactInformation{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
