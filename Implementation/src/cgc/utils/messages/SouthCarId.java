package cgc.utils.messages;

/**
 * This message class is used when VehicleManager initializes cars on the south end garage.
 * This message is sent to the VehicleDispatcher so that it can update the initial list of south cars.
 */
public class SouthCarId implements Message {

    private int id;

    public SouthCarId(int ID) {
        this.id = ID;
    }

    public int getSouthCarId() {
        return id;
    }
}
