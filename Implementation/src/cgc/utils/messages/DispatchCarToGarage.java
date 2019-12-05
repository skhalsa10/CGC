package cgc.utils.messages;

import cgc.utils.LocationStatus;

public class DispatchCarToGarage implements Message {
    private int carId;
    private LocationStatus garageLocation;

    public DispatchCarToGarage(int carId, LocationStatus garageLocation) {
        this.carId = carId;
        this.garageLocation = garageLocation;
    }

    public int getCarId() {
        return carId;
    }

    public LocationStatus getGarageLocation() {
        return garageLocation;
    }
}
