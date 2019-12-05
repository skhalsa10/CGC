package cgc.utils.messages;

import javafx.geometry.Point2D;

import java.util.LinkedList;

public class UpdatedDrivingLocation implements Message {

    private int carId;
    private Point2D currentCarLocation;
    private LinkedList<Integer> tokenIds;

    public UpdatedDrivingLocation(int carId, Point2D currentCarLocation, LinkedList<Integer> tokenIds) {
        this.carId = carId;
        this.currentCarLocation = currentCarLocation;
        this.tokenIds = tokenIds;
    }

    public int getCarId() {
        return carId;
    }

    public Point2D getCurrentCarLocation() {
        return currentCarLocation;
    }

    public LinkedList<Integer> getTokenIds() {
        return tokenIds;
    }
}
