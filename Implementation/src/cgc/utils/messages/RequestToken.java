package cgc.utils.messages;

/*
This message is sent by the Kiosk Manager to the CGC because a token has been purchased in
a pay kiosk.
*/

import javafx.geometry.Point2D;

public class RequestToken implements Message {
    // TODO: add appropriate properties associated with requesting token.
    private Point2D location;

    public RequestToken(Point2D location) {
        this.location = location;
    }

    public Point2D getLocation() {
        return location;
    }
}
