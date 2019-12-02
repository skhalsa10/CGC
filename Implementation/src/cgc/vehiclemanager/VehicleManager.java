package cgc.vehiclemanager;

import cgc.CGC;
import cgc.utils.Communicator;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import cgc.vehiclemanager.vehicle.PatrolVehicle;
import cgc.vehiclemanager.vehicle.TourVehicle;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

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
    private VehicleDispatcher vehicleDispatcher;
    private VehicleScheduler vehicleScheduler;
    private boolean isRunning;
    private boolean isInEmergency;
    private HashMap<Integer, PatrolVehicle> patrolCars;
    private HashMap<Integer, TourVehicle> tourCars;
    //TODO must make a Data structure to keep track of vehicles
    // if we only have X amount of cars it may need to keep track of the available and used cars
    // is this done with the scheduler or dispatcher?

    //TODO the other option is to have a VehicleFactor that can produce an unlimited amount of vehicles?


    public VehicleManager(CGC cgc) {

        this.cgc = cgc;
        this.isInEmergency = false;
        messages = new PriorityBlockingQueue<>();
        isRunning = true;
        //lets initialize some patrol cars
        patrolCars = new HashMap<>();
        tourCars = new HashMap<>();

        this.start();
    }

    @Override
    public void run() {
        //initialize here instead of constructor
        this.vehicleDispatcher = new VehicleDispatcher(this);
        initializePatrolCars();
        initializeTourCars();
        while(isRunning){
            try {
                Message m = messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializePatrolCars() {
        for (int id = 1; id < 6; id++){
            Point2D p = new Point2D((MapInfo.MAP_WIDTH/4)*3,MapInfo.MAP_HEIGHT-MapInfo.SOUTHBUILDING_HEIGHT);
            patrolCars.put(id,
                    new PatrolVehicle(id, this, p));
        }
    }

    // initializing tour cars on the south end garage.
    private void initializeTourCars() {
        // incrementing by 2 to (inclusive) random bounds just so the random numbers dont end up at the
        // corner of the garage box and we can't see the car.
        Random randomBounds = new Random();
        double xLeftBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + 2;
        double xRightBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + MapInfo.GARAGE_WIDTH;
        double yMinBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + 2;
        double yMaxBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + MapInfo.GARAGE_HEIGHT;

        // creating 10 cars at random locations inside the south garage.
        for (int id = 1; id < 11; id++) {
            Point2D location = new Point2D(xLeftBound + (xRightBound - xLeftBound) * randomBounds.nextDouble(),
                                           yMinBound + (yMaxBound - yMinBound) * randomBounds.nextDouble());
            tourCars.put(id, new TourVehicle(id, this, location));
            // send the car info to dispatcher so it can update its list.
            Message southCarId = new SouthCarId(id);
            this.vehicleDispatcher.sendMessage(southCarId);
        }
    }

    private void sendMessageToPatrolVehicles(Message m) {
        for(PatrolVehicle pv : patrolCars.values()){
            pv.sendMessage(m);
        }
    }

    private void sendMessageToTourVehicles(Message m) {
        for (TourVehicle tourVehicle : tourCars.values()) {
            tourVehicle.sendMessage(m);
        }
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    private synchronized void processMessage(Message m){
        if (m instanceof ShutDown){
            this.vehicleDispatcher.sendMessage(m);
            sendMessageToPatrolVehicles(m);
            sendMessageToTourVehicles(m);
            isRunning = false;
        }
        else if(m instanceof CGCRequestHealth){
            sendMessageToPatrolVehicles(m);
            sendMessageToTourVehicles(m);
        }
        else if(m instanceof CGCRequestLocation){
            sendMessageToPatrolVehicles(m);
            sendMessageToTourVehicles(m);
        }
        else if(m instanceof EnterEmergencyMode){
            if(!isInEmergency) {
                isInEmergency = true;
                this.vehicleDispatcher.sendMessage(m);
                sendMessageToPatrolVehicles(m);
                sendMessageToTourVehicles(m);
            }
        }
        else if(m instanceof ExitEmergencyMode){
            if(isInEmergency) {
                isInEmergency = false;
                this.vehicleDispatcher.sendMessage(m);
                sendMessageToPatrolVehicles(m);
                sendMessageToTourVehicles(m);
            }
        }
        else if (m instanceof TokenReadyToLeave) {
            this.vehicleDispatcher.sendMessage(m);
        }
        else if (m instanceof DispatchCarToPickup) {
            DispatchCarToPickup m2 = (DispatchCarToPickup) m;
            TourVehicle currentCar = tourCars.get(m2.getCarId());

            Message driveToPickUp = new BeginDrivingToPickup(m2.getCarLocation());
            currentCar.sendMessage(driveToPickUp);
        }
        else if (m instanceof DispatchCar) {
            DispatchCar m2 = (DispatchCar) m;
            TourVehicle carToBeginDriving = tourCars.get(m2.getCarId());

            Message driveToDropOff = new BeginDrivingToDropOff(m2.getTokensId());
            carToBeginDriving.sendMessage(driveToDropOff);
        }
        else if (m instanceof DispatchCarToGarage) {
            DispatchCarToGarage m2 = (DispatchCarToGarage) m;
            TourVehicle carToDriveToGarage = tourCars.get(m2.getCarId());

            Message driveToGarage = new BeginDrivingToGarage(m2.getGarageLocation());
            carToDriveToGarage.sendMessage(driveToGarage);
        }
        else if (m instanceof TourCarArrivedAtPickup) {
            this.vehicleDispatcher.sendMessage(m);
        }
        else if (m instanceof TourCarArrivedAtDropOff) {
            System.out.println("Vehicle Manager processed TourCarArrivedAtDropOff and it has I");
            this.vehicleDispatcher.sendMessage(m);
            cgc.sendMessage(m);
        }
        else if (m instanceof TourCarArrivedAtGarage) {
            this.vehicleDispatcher.sendMessage(m);
        }
        else if(m instanceof UpdatedHealth){
            cgc.sendMessage(m);
        }
        else if(m instanceof UpdatedLocation){
            cgc.sendMessage(m);
        }
        else if (m instanceof UpdatedDrivingLocation) {
            cgc.sendMessage(m);
        }
        else{
            System.out.println("sorry vehicle Manager cannot process message: ");
        }
    }
}
