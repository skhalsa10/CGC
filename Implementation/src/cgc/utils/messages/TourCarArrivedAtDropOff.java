package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * This message will be sent from car to vehicleDispatcher whenever the car arrives at dropoff location.
 */
public class TourCarArrivedAtDropOff implements Message {

    private int carId;
    private LocationStatus dropOffLocation;

    public TourCarArrivedAtDropOff(int carId, LocationStatus dropOffLocation) {
        this.carId = carId;
        this.dropOffLocation = dropOffLocation;
    }

    public int getCarId() {
        return carId;
    }

    public LocationStatus getDropOffLocation() {
        return dropOffLocation;
    }
}
