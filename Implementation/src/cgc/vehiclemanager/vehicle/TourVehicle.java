package cgc.vehiclemanager.vehicle;

import cgc.utils.messages.Message;
import cgc.vehiclemanager.VehicleManager;

import java.awt.*;

/**
 * this is a speciel extension of the Vehicle class the generates Tour related behavior.
 * for example this cars movemove may only move towards pickup locations and back and fourht on the main highway
 *
 * the Tour vehicle may receive a message from the Vehicle Manager to give it a destination
 *  1. set destination and behave accordingly
 *
 * the Tour vehicle may receive a message from the Vehicle Manager to update it with it's location
 *  1. Send message to VehicleManager with updated location
 *
 * the Tour Vehicle may receive a message from the vehicle manager to update it with its current health status
 *  1. send message to vehicleManager with current healthstatus.
 *
 * the Tour vehicle may receive a message from the Vehicle Manager
 *
 * The Tour Vehicle may send a message to the Vehicle manager that it has arrived at destination
 *
 * The Tour vehicle may send a message to the Vehicle Manager that is ready to pick up at South/North
 *
 * The Tour Vehicle may receive message from VehicleManager EnterEmergencyMode
 *  1. it will respond by following emergency protocal behavior
 *
 * the Tour Vehicle May receive message from VehicleManager to ExitEmergencyMode
 *  1. respond accordingly
 *
 * The Tour Vehicle may receive a message from VehicleManager to ShutDown
 *  1. all threads will shut down gracefully.
 */
public class TourVehicle extends Vehicle {



    public TourVehicle(int ID, VehicleManager vehicleManager, Point location) {
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
    public void run() {
        //TODO This should loop and wait on the message queue and shut down only if shutdown is received
        //TODO this will call processMessage(m) to respond accordingly
    }


    @Override
    protected void processMessage(Message m) {
        //TODO process m using instanceof
    }
}
