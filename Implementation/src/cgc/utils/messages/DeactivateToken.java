package cgc.utils.messages;

import cgc.utils.Entity;

public class DeactivateToken implements Message {
    private int ID;
    private Entity entity;

    public DeactivateToken(int ID, Entity entity) {
        this.ID = ID;
        this.entity = entity;
    }

    public int getID() {
        return ID;
    }

    public Entity getEntity() {
        return entity;
    }
}
