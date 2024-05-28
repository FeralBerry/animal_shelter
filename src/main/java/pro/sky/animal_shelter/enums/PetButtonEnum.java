package pro.sky.animal_shelter.enums;

public enum PetButtonEnum {
    PET_BUTTON_NEXT("Вперед","next_pet"),
    PET_BUTTON_PREV("Назад","prev_pet");
    private final String text;
    private final String command;
    PetButtonEnum(String text,String command) {
        this.text = text;
        this.command = command;
    }
    public String getText() {
        return text;
    }
    public String getCommand() {
        return command;
    }
    @Override
    public String toString() {
        return "PetButtonEnum{" +
                "text='" + text + '\'' +
                "command='" + command + '\'' +
                '}';
    }
}
