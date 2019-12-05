package cgc.vehiclemanager.vehicle;

import cgc.utils.Communicator;
import cgc.utils.Locatable;
import cgc.utils.Maintainable;
import cgc.utils.messages.Message;
import cgc.vehiclemanager.VehicleManager;
import javafx.geometry.Point2D;


import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * Zeke before you get mad at me I did not make skeleton classes for statistics Module, deviceManager, or RoutingModule.
 * I do agree with these as classes for the real world situation but I think the behavior in this scenario is so basic
 * that it can be simulated inside of the vehicle class. For example the Routing module.
 * I am guessing this will find the fastest/safest route to its destination. that can easily be done by using the current location
 * and the location of the destination and then incrementing the x and y of the vehicle to move it closer to the destination
 * This is essential some basic routing but can be done in a function rather than an entire class.
 *
 * I will leave it up to you if you want to implement these but I quickly ran out of time and dont feel the added complexity will be benificial
 *
 * Another example is the device manager. the transducer monitor will nto be needed in our final product we can
 * add later if we have time,  but we should prioritize a finished basic product. I think we can simplify Maintenance with
 * a boolean value in the health status this can be changed later if we have time
 * the Cabin Monitor is also not critical for our deliverable.
 *
 * Feel free ti implement this if you think you can manage but may be out of scope for our final product.
 *
 */
public abstract class Vehicle extends Thread implements Maintainable, Communicator, Locatable {

    protected int ID;
    protected VehicleManager vehicleManager;
    //The health will be simple in this version right now it has a boolean value of true or false
    protected boolean healthStatus;
    protected Point2D location;
    protected PriorityBlockingQueue<Message> messages;

    public Vehicle(int ID, VehicleManager vehicleManager, Point2D location) {
        this.ID = ID;
        this.vehicleManager = vehicleManager;
        this.healthStatus = true;
        this.location = location;
        this.messages = new PriorityBlockingQueue<>();

    }

    protected abstract void startVehicleTimer();
    protected abstract void processMessage(Message m);
}
