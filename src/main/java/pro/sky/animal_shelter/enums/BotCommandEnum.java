package pro.sky.animal_shelter.enums;



public enum BotCommandEnum {
    START("start"),
    ABOUT("about"),
    INFO("info"),
    PET_REPORT_FORM("pet_report_form"),
    TO_CALL_A_VOLUNTEER("to_call_a_volunteer"),
    CONTACT_INFORMATION("contact_information"),
    PET_LIST("pet_list");
    private final String url;
    BotCommandEnum(String url) {
        this.url = url;
    }
    public String getUrl() {
        return "/" + url;
    }
    @Override
    public String toString() {
        return "BotCommandEnum{" +
                "url='" + url + '\'' +
                '}';
    }
}
