package pro.sky.animal_shelter.enums;

public enum UserButtonEnum {
    PET_REPORT("Отправить отчет","pet_report"),
    VIEW_PETS("Просмотреть животных","view_pets"),
    CALL_TO("Отправить сообщение волонтеру","call_to"),
    CONTACT_INFORMATION_ADD("Записать контактные данные","contact_information_add");
    private final String text;
    private final String command;
    UserButtonEnum(String text, String command) {
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
