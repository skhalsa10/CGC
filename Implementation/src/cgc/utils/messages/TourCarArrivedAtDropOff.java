package cgc.utils.messages;

import cgc.utils.LocationStatus;

import java.util.LinkedList;

/**
 * This message will be sent from car to vehicleDispatcher whenever the car arrives at dropoff location.
 */
public class TourCarArrivedAtDropOff implements Message {

    private int carId;
    private LocationStatus dropOffLocation;
    private LinkedList<Integer> tokensId;

    public TourCarArrivedAtDropOff(int carId, LocationStatus dropOffLocation, LinkedList<Integer> tokensId) {
        this.carId = carId;
        this.dropOffLocation = dropOffLocation;
        this.tokensId = tokensId;
    }

    public int getCarId() {
        return carId;
    }

    public LocationStatus getDropOffLocation() {
        return dropOffLocation;
    }

    public LinkedList<Integer> getTokensId() {
        return tokensId;
    }
}
