package cgc.utils.messages;

import cgc.utils.LocationStatus;

public class BeginDrivingToGarage implements Message {

    private LocationStatus garageLocation;

    public BeginDrivingToGarage(LocationStatus garageLocation) {
        this.garageLocation = garageLocation;
    }

    public LocationStatus getGarageLocation() {
        return garageLocation;
    }
}
