package pro.sky.animal_shelter.enums;

public enum AdminStatusEnum {
    PET_ADD("pet_add"),
    VIEW_CONTACT_INFORMATION("view_contact_information");
    private final String status;
    AdminStatusEnum(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    @Override
    public String toString() {
        return "AdminButtonEnum{" +
                "text='" + status + '\'' +
                '}';
    }
}
