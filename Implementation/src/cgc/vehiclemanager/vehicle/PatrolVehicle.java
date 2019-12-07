package cgc.vehiclemanager.vehicle;

import cgc.utils.Entity;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import cgc.vehiclemanager.VehicleManager;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Patrol vehicle is a special version of the vehicle class that simulates the behavior of a patrol car and how it
 * might interact with the world. The patrol car is extremely simple it literally picks a random point in the patrol area
 * and drives to it.
 *
 * @version 1
 * @author Siri
 *
 * Skeleton were written by Anas and Siri
 *
 */
public class PatrolVehicle extends Vehicle {
    private Point2D dest;
    private Random rand;
    private boolean isRunning = true;
    private double distance;
    private boolean isInEmergency = false;
    private Timer timer;


    public PatrolVehicle(int ID, VehicleManager vehicleManager, Point2D location) {
        super(ID, vehicleManager, location);
        this.timer = new Timer();
        rand = new Random();
        dest = genRandDest();
        distance= Math.sqrt(
                ((dest.getX()-location.getX())*(dest.getX()-location.getX()))
                + ((dest.getY()-location.getY())*(dest.getY()-location.getY())));
        startVehicleTimer();
        this.start();
    }

    @Override
    protected void startVehicleTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MovePatrolVehicle move = new MovePatrolVehicle();
                messages.put(move);
            }
        };
        // schedules after every second.
        this.timer.schedule(task, 0, 17);
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    /**
     * here we will simple block and wait of message queue until we have a emssage to process.
     */
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


    /**
     * Process messages to this patrol vehicle
     * @param m
     */
    @Override
    protected synchronized void processMessage(Message m) {
        if(m instanceof CGCRequestLocation){

            vehicleManager.sendMessage(
                    new UpdatedLocation(
                            Entity.PATROL_VEHICLE,
                            this.ID,
                            new Point2D(
                                    location.getX(),
                                    location.getY())));

        }
        else if(m instanceof CGCRequestHealth){

            vehicleManager.sendMessage(
                    new UpdatedHealth(
                            Entity.PATROL_VEHICLE,
                            this.ID,
                            this.healthStatus));

        }

        else if(m instanceof EnterEmergencyMode){
            this.isInEmergency = true;
        }

        else if(m instanceof ExitEmergencyMode){
            this.isInEmergency = false;
        }
        else if(m instanceof ShutDown){
            System.out.println("Patrol vehicle " + ID + " is shutting down");
            isRunning = false;
            timer.cancel();
        }
        else if(m instanceof MovePatrolVehicle){
            moveVehicle();
        }
        else{
            System.out.println("The Patrol vehicle can not handle this message: " + m);
        }
    }

    //movement algorithm very basic just moves to a point over
    // time and then picks a new random point in bound
    private void moveVehicle() {
        location = location.add(((dest.getX()-location.getX())/distance)*2,
                ((dest.getY()-location.getY())/distance)*2);

        //check to see if we are in the viscinity of the destination and pick a new destination
        if(dest.getX()-1<location.getX() &&
                location.getX()<dest.getX()+1 &&
                dest.getY()-1<location.getY() &&
                location.getY()<dest.getY()+1){

            //set a new random destination
            dest = genRandDest();

            //using pythagorean theorem to calculate distance
            distance= Math.sqrt(
                    ((dest.getX()-location.getX())*
                            (dest.getX()-location.getX()))
                            + ((dest.getY()-location.getY())*
                            (dest.getY()-location.getY())));
        }

        //make sure to upload the new location
        vehicleManager.sendMessage(
                new UpdatedLocation(
                        Entity.PATROL_VEHICLE,
                        this.ID,
                        new Point2D(
                                location.getX(),
                                location.getY())));
    }

    private Point2D genRandDest() {
        double xLeftBound = 0;
        double xRightBound = MapInfo.MAP_WIDTH;
        double yMinBound = MapInfo.UPPER_LEFT_PATROL_BOX.getY();
        double yMaxBound = MapInfo.BOTTOM_RIGHT_PATROL_BOX.getY();

        return new Point2D(xLeftBound + (xRightBound - xLeftBound) * rand.nextDouble(),
                yMinBound + (yMaxBound - yMinBound) * rand.nextDouble());
    }
}
