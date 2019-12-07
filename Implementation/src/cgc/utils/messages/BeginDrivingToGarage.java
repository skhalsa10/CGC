package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * Dispatcher message to the Tour Vehicle to move it the an appropriate garage location.
 *
 * @author Anas
 */
public class BeginDrivingToGarage implements Message {

    private LocationStatus garageLocation;

    public BeginDrivingToGarage(LocationStatus garageLocation) {
        this.garageLocation = garageLocation;
    }

    public LocationStatus getGarageLocation() {
        return garageLocation;
    }
}
