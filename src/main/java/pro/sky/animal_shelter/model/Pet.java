package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;

@Entity(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String petName;
    private String fileName;
    @Lob
    private byte[] data;
    public Long getId() {
        return id;
    }
    public String getPetName() {
        return petName;
    }
    public String getFileName() {
        return fileName;
    }
    public byte[] getData() {
        return data;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) && Objects.equals(petName, pet.petName) && Objects.equals(fileName, pet.fileName) && Arrays.equals(data, pet.data);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(id, petName, fileName);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", petName='" + petName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
