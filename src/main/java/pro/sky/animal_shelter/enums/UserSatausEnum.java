package pro.sky.animal_shelter.enums;

public enum UserSatausEnum {
    NO_STATUS("null"),
    ADD_PHONE("add_phone"),
    GET_PET_FORM("get_pet_form"),
    ADD_PET_REPORT_IMG("add_pet_report_img"),
    GET_CONTACT_INFORMATION("get_contact_information"),
    CALL_A_VOLUNTEER("call_a_volunteer"),
    VIEW_PET_LIST("view_pet_list");
    private final String status;
    UserSatausEnum(String status) {
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
