package cgc.utils.messages;

/*
This message is sent by the Kiosk Manager to the CGC because a token has been purchased in
a pay kiosk.
*/

import javafx.geometry.Point2D;

public class RequestToken implements Message {
    private Point2D spawnPoint;

    public RequestToken(Point2D spawnPoint) {
        spawnPoint = new Point2D(spawnPoint.getX(),spawnPoint.getY());
    }

    public Point2D getSpawnPoint() {
        return spawnPoint;
    }
}
