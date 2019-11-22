package cgc.utils.messages;

import cgc.utils.Entity;

import java.awt.*;

public class UpdatedLocation implements Message {

    private Entity entityName;
    private int entityID;
    private Point loc;

    public UpdatedLocation(Entity entityName, int ID, Point location) {
        this.entityName = entityName;
        this.entityID = ID;
        this.loc = location;
    }

    public Entity getEntityName() {
        return entityName;
    }

    public int getEntityID() {
        return entityID;
    }

    public Point getLoc() {
        return loc;
    }
}
