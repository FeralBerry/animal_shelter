package pro.sky.animal_shelter.exeptions;

public class NoSuchInfoException extends RuntimeException{
    public NoSuchInfoException(String message) {
        super(message);
    }
}
