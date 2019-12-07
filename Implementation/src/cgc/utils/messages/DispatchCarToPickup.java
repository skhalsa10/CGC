package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * produced by the vehicle dispatcher to tell a car to move towards a pickup
 */
public class DispatchCarToPickup implements Message {

    private int carId;
    private LocationStatus carLocation;

    public DispatchCarToPickup(int carId, LocationStatus carLocation) {
        this.carId = carId;
        this.carLocation = carLocation;
    }

    public int getCarId() {
        return carId;
    }

    public LocationStatus getCarLocation() {
        return carLocation;
    }
}
