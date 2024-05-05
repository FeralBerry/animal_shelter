package pro.sky.animal_shelter.enums;

public enum AdminButtonToMainMenuEnum {
    VIEW_CONTACT_INFORMATION("Просмотреть обратную связь","view_contact_information"),
    PET_ADD("Добавить питомца","pet_add"),
    VIEW_REPORTS("Просмотреть отчеты","view_reports");
    private final String text;
    private final String command;
    AdminButtonToMainMenuEnum(String text, String command) {
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
