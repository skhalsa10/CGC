package cgc.vehiclemanager.vehicle;

import cgc.messages.Message;
import cgc.vehiclemanager.VehicleManager;

import java.awt.*;

/**
 * The Patrol vehicle is a special version of the vehicle class that simulates the behavior of a patrol car and how it
 * might interact with the world.
 *
 * the Patrol vehicle may receive a message from the Vehicle Manager to give it a destination
 *  1. set destination and behave accordingly
 *
 * the Patrol vehicle may receive a message from the Vehicle Manager to update it with it's location
 *  1. Send message to VehicleManager with updated location
 *
 * the Patrol Vehicle may receive a message from the vehicle manager to update it with its current health status
 *  1. send message to vehicleManager with current healthstatus.
 *
 * The Patrol Vehicle may send a message to the Vehicle manager that it has arrived at destination
 *
 * The patrol vehicle may send message to vehicle manager that it is now beginning autonomouspatrol
 *
 *  * The Patrol Vehicle may receive message from VehicleManager EnterEmergencyMode
 *  *  1. it will respond by following emergency protocal behavior
 *  *
 *  * the PatrolVehicle May receive message from VehicleManager to ExitEmergencyMode
 *  *  1. respond accordingly
 *  *
 *  * The PatrolVehicle may receive a message from VehicleManager to ShutDown
 *  *  1. thread will shut down gracefully.
 *
 */
public class PatrolVehicle extends Vehicle {


    public PatrolVehicle(int ID, VehicleManager vehicleManager, Point location) {
        super(ID, vehicleManager, location);
    }

    @Override
    protected void startVehicleTimer() {
        //TODO build the timer and timer task here.
        // this can be used to update the location of the car over time
    }

    @Override
    protected void stopVehicleTimer() {
        //TODO in case you need to kill the current timer and task
    }

    @Override
    public synchronized void sendMessage(Message m) {

        //TODO place this message in messages queue
    }

    @Override
    public synchronized void getLocation() {
        //TODO Place a message in messages queue to send a location update to Token Manager
    }

    @Override
    public synchronized void checkHealth() {
        //TODO Place a message in messages queue to send a health update to Token Manager
    }

    @Override
    public void run() {
        //TODO This should loop and wait on the message queue and shut down only if shutdown is received
        //TODO this will call processMessage(m) to respond accordingly
    }


    @Override
    protected void processMessage(Message m) {
        //TODO process m using instanceof
    }
}
