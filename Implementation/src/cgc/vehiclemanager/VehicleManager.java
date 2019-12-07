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

/**
 * The Vehicle Manager will manage all aspects of all vehicles. It is another simple manager class
 * it keeps track of active vehicles and forwards the appropriate messages to them.
 *
 * @version 1
 * @author Siri and Anas
 *
 * Skeleton were written by Anas and Siri
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
        double xLeftBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + 5;
        double xRightBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + MapInfo.GARAGE_WIDTH - 5;
        double yMinBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + 5;
        double yMaxBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + MapInfo.GARAGE_HEIGHT - 5;

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
            //System.out.println("Vehicle Manager processed TourCarArrivedAtDropOff and it has I");
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
