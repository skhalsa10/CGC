package cgc.vehiclemanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.messages.*;
import cgc.vehiclemanager.vehicle.PatrolVehicle;
import cgc.vehiclemanager.vehicle.TourVehicle;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The Vehicle Manager will manage all aspects of all vehicles. It will also act as a communicator with the CGC
 * The following is a list of actions and behavior that it may produce during the time it is alive.
 * I may have missed some of them. :) but should be enough to get started.
 *
 *
 * The VehicleManager may send a message to a specific Vehicle and give it a destination
 *
 * The VehicleManager may send a message to all vehicles to get updated location from them.
 *
 * The VehicleManager may send a message to all vehicles to get update health status
 *
 * The VehicleManager may receive a message from the CGC to provide locations of all active vehicles
 *      1. The VehicleManager will send a message to all active vehicles to provide location
 *         This will guarantee that the locations will eventually get update because of the behavior seen next
 *
 * The VehicleManager may receive a message from a specific vehicle with its current location
 *      1. Will respond by forwarding this message to the CGC
 *
 * The VehicleManager may receive a message from the CGC to provide all health information of the cars
 *      1. In responce the VehicleManager will send a message to all Active Vehicles to respond with their
 *          healthStatus
 *
 * The VehicleManager may receive a message from a Specific Vehicle with its HealthStatus
 *      1. the VehicleManager will forward this message to the CGC.
 *
 * The VehicleManager may receive a message from a specific Vehicle that it arrived at it's destination
 *      1. The vehicleManager may respond by taking some action out on the dispatcher or scheduler
 *         or if these end up being threads it may forward the message to one of them or both of them
 *      2. It may forward a message to the CGC to tell the token manager so the tokens will leave the car.
 *      3. another option is the vehicle owns their own copy of references to the token and send them destination arrived messages
 *
 *
 * The VehicleManager may receive a message That it is currently ready to pickup at the southlot
 *      1. The vehicle may forward this information to the CGC to route to the token manager with the vehicle location
 *      2. or it may send this information to the scheduler/dispatcher depending on how you want to implement
 *
 * the VehicleManager may receive a message from a specific vehicle that it is ready to pickup at the northlot.
 *      1. The vehicle may forward this information to the CGC to route to the token manager with the vehicle location
 *      2. or it may send this information to the scheduler/dispatcher depending on how you want to implement
 *
 * The VehicleManager may receive a shutdown message this
 *      1. The VehicleManager shoudl forward a copy of the shutdown message to every car to gracefully have those threads
 *         close
 *      2. after all Vehicle Threads have shutdown it shoudl shutdown itself.
 *
 * The VehicleManager may receive EnterEmergencyMode Message
 *      1. The vehicle Manager should forward a copy of this message to all active vehicles/ or the unit to magage emergency mode
 *      2. it should place it self in Emergency Mode
 *
 * The VehicleManager may receive ExitEmergencyMode Message
 *      1. The vehicle Manager should forward a copy of this message to all active vehicles/ or the unit to magage emergency mode
 *      2. it should exit Emergency Mode
 */
public class VehicleManager extends Thread implements Communicator {
    private PriorityBlockingQueue<Message> messages;
    private CGC cgc;
    private Dispatcher dispatcher;
    private VehicleScheduler vehicleScheduler;
    private boolean isRunning;
    private HashMap<Integer, PatrolVehicle> patrolCars;
    private HashMap<Integer, TourVehicle> tourCars;
    //TODO must make a Data structure to keep track of vehicles
    // if we only have X amount of cars it may need to keep track of the available and used cars
    // is this done with the scheduler or dispatcher?

    //TODO the other option is to have a VehicleFactor that can produce an unlimited amount of vehicles?


    public VehicleManager(CGC cgc) {

        this.cgc = cgc;
        messages = new PriorityBlockingQueue<>();
        isRunning = true;
        //lets initialize some patrol cars
        

        this.start();
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                Message m = messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    private void processMessage(Message m){
        //TODO use the instanceof keyword to determine what message you have and act accordingly
        (m instanceof ShutDown){
            //TODO shutdown all car instances
            isRunning = false;
        }
        else if(m instanceof CGCRequestHealth){
            //TODO loop over all patrol and Tour Vehicles and send message
        }
        else if(m instanceof CGCRequestLocation){
            //TODO loop over all patrol and Tour Vehicles and send message
        }
        else if(m instanceof EnterEmergencyMode){
            //TODO place itself in emergency mode and forward message to all vehicles
        }
        else if(m instanceof  ExitEmergencyMode){
            //TODO forward message and exit emergency mode
        }
        else if(m instanceof UpdatedHealth){
            cgc.sendMessage(m);
        }
        else if(m instanceof UpdatedLocation){
            cgc.sendMessage(m);
        }
        else{
            System.out.println("sorry vehicleManager cannot process message: ");
        }
    }
}
