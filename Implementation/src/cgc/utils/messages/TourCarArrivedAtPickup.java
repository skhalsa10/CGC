package cgc.utils.messages;

import cgc.utils.LocationStatus;
import javafx.geometry.Point2D;

public class TourCarArrivedAtPickup implements Message {
    private int carId;
    private LocationStatus carDirection;

    public TourCarArrivedAtPickup(int carId, LocationStatus arrivalDirection) {
        this.carId = carId;
        this.carDirection = arrivalDirection;
    }

    public int getCarId() {
        return carId;
    }

    public LocationStatus getCarDirection() {
        return carDirection;
    }
}
