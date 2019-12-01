package cgc.utils.messages;

import cgc.utils.LocationStatus;

public class BeginDrivingToPickup implements Message {

    private LocationStatus pickupLocation;

    public BeginDrivingToPickup(LocationStatus pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationStatus getPickupLocation() {
        return pickupLocation;
    }
}
