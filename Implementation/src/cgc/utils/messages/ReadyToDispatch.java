package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * This message is for countdown of vehicle dispatcher. Its used when the car at pickup location
 * is waiting and less than 10 tokens are ready to leave.
 */
public class ReadyToDispatch implements Message {

    private LocationStatus carLocation;

    public ReadyToDispatch(LocationStatus carLocation) {
        this.carLocation = carLocation;
    }

    public LocationStatus getCarLocation() {
        return carLocation;
    }
}
