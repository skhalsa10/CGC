package cgc.utils.messages;

import cgc.utils.LocationStatus;

public class TourCarArrivedAtGarage implements Message {

    private int carId;
    private LocationStatus garageLocation;

    public TourCarArrivedAtGarage(int carId, LocationStatus garageLocation) {
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
