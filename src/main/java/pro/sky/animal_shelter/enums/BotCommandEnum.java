package pro.sky.animal_shelter.enums;



public enum BotCommandEnum {
    START("start","get welcome message"),
    ABOUT("about","find out information about the nursery"),
    INFO("info","information about animals and rules"),
    PET_REPORT_FORM("pet_report_form","animal report form"),
    TO_CALL_A_VOLUNTEER("to_call_a_volunteer","call a volunteer"),
    CONTACT_INFORMATION("contact_information","feedback"),
    PET_LIST("pet_list","view all pets");
    private final String url;
    private final String description;
    BotCommandEnum(String url,String description) {
        this.url = url;
        this.description = description;
    }
    public String url() {
        return "/" + url;
    }
    public String description() {
        return description;
    }
}
