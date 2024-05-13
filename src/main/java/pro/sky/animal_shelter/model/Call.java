package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userChatId;
    private Long adminChatId;

    public Long getId() {
        return id;
    }

    public Long getUserChatId() {
        return userChatId;
    }

    public Long getAdminChatId() {
        return adminChatId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserChatId(Long userChatId) {
        this.userChatId = userChatId;
    }

    public void setAdminChatId(Long adminChatId) {
        this.adminChatId = adminChatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return Objects.equals(id, call.id) && Objects.equals(userChatId, call.userChatId) && Objects.equals(adminChatId, call.adminChatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userChatId, adminChatId);
    }

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", userChatId=" + userChatId +
                ", adminChatId=" + adminChatId +
                '}';
    }
}
