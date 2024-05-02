package pro.sky.animal_shelter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.util.Arrays;
import java.util.Objects;

@Entity(name = "pets")
public class Pet {
    @Id
    private Long id;
    private String petName;
    private String filePath;
    private long fileSize;
    private String mediaType;
    @Lob
    private byte[] data;
    public Long getId() {
        return id;
    }
    public String getPetName() {
        return petName;
    }
    public String getFilePath() {
        return filePath;
    }
    public long getFileSize() {
        return fileSize;
    }
    public String getMediaType() {
        return mediaType;
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
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return fileSize == pet.fileSize && Objects.equals(id, pet.id) && Objects.equals(petName, pet.petName) && Objects.equals(filePath, pet.filePath) && Objects.equals(mediaType, pet.mediaType) && Arrays.equals(data, pet.data);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(id, petName, filePath, fileSize, mediaType);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", petName='" + petName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
