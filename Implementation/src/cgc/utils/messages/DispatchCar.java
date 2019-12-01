package cgc.utils.messages;

import java.util.LinkedList;

/**
 * This message will be send from VehicleDispatcher whenever the car is ready to take the passengers.
 */
public class DispatchCar implements Message {

    private int carId;
    private LinkedList<Integer> tokensId;

    public DispatchCar(int carId, LinkedList<Integer> tokensId) {
        this.carId = carId;
        this.tokensId = tokensId;
    }

    public int getCarId() {
        return carId;
    }

    public LinkedList<Integer> getTokensId() {
        return tokensId;
    }
}
