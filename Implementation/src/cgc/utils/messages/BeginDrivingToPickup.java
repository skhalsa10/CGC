package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * Dispatcher produced message that is consumed by a tour vehicle and it tells it to drive to an appropriate pick up location
 * @author Anas
 */
public class BeginDrivingToPickup implements Message {

    private LocationStatus pickupLocation;

    public BeginDrivingToPickup(LocationStatus pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationStatus getPickupLocation() {
        return pickupLocation;
    }
}
