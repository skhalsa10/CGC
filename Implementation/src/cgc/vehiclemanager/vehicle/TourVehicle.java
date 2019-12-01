package cgc.vehiclemanager.vehicle;

import cgc.utils.Entity;
import cgc.utils.messages.*;
import cgc.vehiclemanager.VehicleManager;
import javafx.geometry.Point2D;

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

    private boolean run;
    private boolean emergencyMode;

    public TourVehicle(int ID, VehicleManager vehicleManager, Point2D location) {
        super(ID, vehicleManager, location);
        this.run = true;
        this.emergencyMode = false;
        startVehicleTimer();
        this.start();
    }

    @Override
    public void run() {
        while (run) {
            try {
                Message m = this.messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void startVehicleTimer() {
        //TODO build the timer and timer task here.
        // this can be used to update the location of the car over time
    }

    @Override
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    @Override
    protected synchronized void processMessage(Message m) {
        if (m instanceof ShutDown) {
            System.out.println("Tour Vehicle " + this.ID + " is shutting down.");
            this.run = false;
            this.timer.cancel();
        }
        else if(m instanceof CGCRequestHealth) {
            Message updatedHealth = new UpdatedHealth(Entity.TOUR_VEHICLE, this.ID, this.healthStatus);
            this.vehicleManager.sendMessage(updatedHealth);
        }
        else if(m instanceof CGCRequestLocation) {
            Message updatedLocation = new UpdatedLocation(
                    Entity.TOUR_VEHICLE, this.ID, new Point2D(this.location.getX(), this.location.getY()));
            this.vehicleManager.sendMessage(updatedLocation);
        }
        else if(m instanceof EnterEmergencyMode) {
            if (!this.emergencyMode) {
                this.emergencyMode = true;
                // TODO: Handle the cases in emergency mode.
            }
        }
        else if(m instanceof ExitEmergencyMode) {
            if (this.emergencyMode) {
                this.emergencyMode = false;
                // TODO: Handle the cases of resuming the timer and going back to normal mode.
            }
        }
    }
}
