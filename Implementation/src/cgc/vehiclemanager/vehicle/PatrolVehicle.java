package cgc.vehiclemanager.vehicle;

import cgc.utils.Entity;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import cgc.vehiclemanager.VehicleManager;
import javafx.geometry.Point2D;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Patrol vehicle is a special version of the vehicle class that simulates the behavior of a patrol car and how it
 * might interact with the world.
 *
 * the Patrol vehicle may receive a message from the Vehicle Manager to give it a destination
 *  1. set destination and behave accordingly
 *  N/A
 *
 * the Patrol vehicle may receive a message from the Vehicle Manager to update it with it's location
 *  1. Send message to VehicleManager with updated location
 *  DONE
 *
 * the Patrol Vehicle may receive a message from the vehicle manager to update it with its current health status
 *  1. send message to vehicleManager with current healthstatus.
 *  DONE
 *
 * The Patrol Vehicle may send a message to the Vehicle manager that it has arrived at destination
 *
 * The patrol vehicle may send message to vehicle manager that it is now beginning autonomouspatrol
 *
 *  * The Patrol Vehicle may receive message from VehicleManager EnterEmergencyMode
 *  *  1. it will respond by following emergency protocal behavior
 *  DONE
 *  *
 *  * the PatrolVehicle May receive message from VehicleManager to ExitEmergencyMode
 *  *  1. respond accordingly
 *  DONE
 *  *
 *  * The PatrolVehicle may receive a message from VehicleManager to ShutDown
 *  *  1. thread will shut down gracefully.
 *  DONE
 *
 */
public class PatrolVehicle extends Vehicle {
    private Point2D dest;
    private ThreadLocalRandom rand;
    private boolean isRunning = true;
    private double distance;
    private boolean isInEmergency = false;


    public PatrolVehicle(int ID, VehicleManager vehicleManager, Point2D location) {
        super(ID, vehicleManager, location);
        rand = ThreadLocalRandom.current();
        dest = new Point2D(rand.nextDouble(0,MapInfo.MAP_WIDTH),
                rand.nextDouble(MapInfo.UPPER_LEFT_PATROL_BOX.getY(),MapInfo.BOTTOM_RIGHT_PATROL_BOX.getY()));
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


    private void restartTimer() {
        //this.timer.cancel();
        this.timer = new Timer();
        startVehicleTimer();
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
        //need to first tell the manager the initial health state
        vehicleManager.sendMessage(new UpdatedHealth(Entity.PATROL_VEHICLE, ID, healthStatus));
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
            //TODO should this do more in emergency mode?
        }

        else if(m instanceof ExitEmergencyMode){
            this.isInEmergency = false;
        }
        else if(m instanceof ShutDown){
            System.out.println("Patrol vehicle " + ID + "is shutting down");
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
            dest = new Point2D(
                    rand.nextDouble(0,MapInfo.MAP_WIDTH),
                    rand.nextDouble(
                            MapInfo.UPPER_LEFT_PATROL_BOX.getY(),
                            MapInfo.BOTTOM_RIGHT_PATROL_BOX.getY()));

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
}
