package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "users")
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private String role;
    private String locationUserOnApp;
    private long petId;
    public Long getChatId() {
        return chatId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUserName() {
        return userName;
    }
    public String getLocationUserOnApp(){
        return locationUserOnApp;
    }
    public String getRole() {
        return role;
    }
    public long getPetId() {
        return petId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setLocationUserOnApp(String locationUserOnApp){
        this.locationUserOnApp = locationUserOnApp;
    }
    public void setPetId(long petId){
        this.petId = petId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(chatId, user.chatId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(userName, user.userName) && Objects.equals(role, user.role) && Objects.equals(locationUserOnApp, user.locationUserOnApp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, firstName, lastName, userName, role, locationUserOnApp);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", role='" + role + '\'' +
                ", locationUserOnApp='" + locationUserOnApp + '\'' +
                '}';
    }
}
