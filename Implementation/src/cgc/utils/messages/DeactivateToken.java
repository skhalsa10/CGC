package cgc.utils.messages;

import cgc.utils.Entity;

/**
 * produced by a guest token when it has reached the end of its time on the park. it is consumed by the token manager
 * and gui which remove them from their lists
 */
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
