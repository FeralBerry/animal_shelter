package pro.sky.animal_shelter.model;

import jakarta.persistence.*;

@Entity(name = "pets_img")
public class PetsImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Pet pet;
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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
