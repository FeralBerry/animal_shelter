package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;
    @OneToOne
    private User admin;
    @Column(name = "updated_at")
    private Long updatedAt;
    public Long getId() {
        return id;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserChatId() {
        return user;
    }

    public void setUserChatId(User user) {
        this.user = user;
    }

    public User getAdminChatId() {
        return admin;
    }

    public void setAdminChatId(User admin) {
        this.admin = admin;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return Objects.equals(id, call.id) && Objects.equals(user, call.user) && Objects.equals(admin, call.admin) && Objects.equals(updatedAt, call.updatedAt);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, user, admin, updatedAt);
    }
    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", userChatId=" + user +
                ", adminChatId=" + admin +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
