package cgc.utils.messages;

import cgc.utils.Entity;
import javafx.geometry.Point2D;

import java.awt.*;

public class UpdatedLocation implements Message {

    private Entity entityName;
    private int entityID;
    private Point2D loc;

    public UpdatedLocation(Entity entityName, int ID, Point2D location) {
        this.entityName = entityName;
        this.entityID = ID;
        this.loc = new Point2D(location.getX(), location.getY());
    }

    public Entity getEntityName() {
        return entityName;
    }

    public int getEntityID() {
        return entityID;
    }

    public Point2D getLoc() {
        return new Point2D(loc.getX(), loc.getY());
    }
}
