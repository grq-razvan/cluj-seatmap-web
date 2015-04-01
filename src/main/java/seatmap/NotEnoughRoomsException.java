package seatmap;

/**
 * @author: Paul Bochis, Catalysts GmbH
 */
public class NotEnoughRoomsException extends Exception {

    String message;

    public NotEnoughRoomsException(int emp) {
        this.message = "You need more rooms for " + emp + " employees";
    }

    @Override
    public String toString() {
        return message;
    }
}
