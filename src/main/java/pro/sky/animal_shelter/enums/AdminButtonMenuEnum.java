package pro.sky.animal_shelter.enums;

public enum AdminButtonMenuEnum {
    VIEW_CONTACT_INFORMATION_COMMAND("Просмотреть обратную связь","view_contact_information"),
    PET_ADD_COMMAND("Добавить питомца","pet_add"),
    VIEW_REPORTS_COMMAND("Просмотреть отчеты","view_reports"),
    ADD_ABOUT("Изменить информацию о приюте","add_about");
    private final String text;
    private final String command;
    AdminButtonMenuEnum(String text, String command) {
        this.text = text;
        this.command = command;
    }
    public String getText() {
        return text;
    }
    public String getCommand(){
        return command;
    }

    @Override
    public String toString() {
        return "AdminButtonEnum{" +
                "text='" + text + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
