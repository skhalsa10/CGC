package cgc.utils.messages;

import cgc.utils.Entity;

public class UpdatedHealth implements Message {
    private Entity entityName;
    private int entityID;
    private boolean healthStatus;

    public UpdatedHealth(Entity entityName, int ID, boolean healthStatus) {
        this.entityName = entityName;
        this.entityID = ID;
        this.healthStatus = healthStatus;
    }

    public Entity getEntityName() {
        return entityName;
    }

    public int getEntityID() {
        return entityID;
    }

    public boolean isHealthStatus() {
        return healthStatus;
    }
}
