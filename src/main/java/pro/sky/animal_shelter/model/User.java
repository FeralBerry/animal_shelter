package pro.sky.animal_shelter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Entity(name = "users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class User {
    @Id
    private Long chatId;
    @Column(length = 100)
    private String firstName;
    @Column(length = 100)
    private String lastName;
    @Column(length = 100)
    private String userName;
    @Column(length = 255)
    private String role;
    @Column(length = 50)
    private String locationUserOnApp;
    @OneToOne
    private Pet pet;
    @OneToOne
    private Pet addedPet;
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

    public Pet getPetId() {
        return pet;
    }

    public void setPetId(Pet pet) {
        this.pet = pet;
    }

    public Pet getAddedPetId() {
        return addedPet;
    }

    public void setAddedPetId(Pet addedPet) {
        this.addedPet = addedPet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return pet == user.pet && addedPet == user.addedPet && Objects.equals(chatId, user.chatId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(userName, user.userName) && Objects.equals(role, user.role) && Objects.equals(locationUserOnApp, user.locationUserOnApp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, firstName, lastName, userName, role, locationUserOnApp, pet, addedPet);
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
                ", petId=" + pet +
                ", addedPetId=" + addedPet +
                '}';
    }
}
