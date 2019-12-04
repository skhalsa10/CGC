package cgc.tokenmanager;

import cgc.utils.Entity;
import cgc.utils.LocationStatus;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * this token encapsulates the behavior os the Employee. in the real world it would just report data to the token
 * manager. but in this simulation it also simulates behavior over time..
 *
 * To simulate the behavior over time it will use a Timer and Timer task to generate location movement over
 * time.
 *
 * This Token Must sendMessage() to the Token Manager with its updated healthstatus Every time it changes
 *
 * This Token Must sendMessage() to the Token Manager with its updated Location EVERY time it changes
 *
 * The Token  may receive EmergencyMode message
 *    2. Put itself in emergency mode.
 *
 * The Token  may receive ExitEmergencyMode message
 *    2. it must exit emergency mode.
 *
 * The Token may receive a Shutdown Message from the Token Manager
 *
 *
 */
public class EmployeeToken extends Token
{
    private boolean isRunning;
    private boolean isInEmergency;
    private boolean isWorkingNorth;
    private LocationStatus currentArea;
    private int counter = 1;
    private Random rand;
    private Point2D walkDest;
    private boolean readyForPickup;
    private boolean isDriving;

    /**
     * this is the constructor for the Employee token. the employee should spawn at the south entrance
     * it has a unique token ID and a reference to its generator the tokenManager.
     * This implements Maintainable and Locatable so it can report its health and location
     * @param ID
     * @param tokenManager
     * @param GPSLocation
     */
    public EmployeeToken(int ID, TokenManager tokenManager, Point2D GPSLocation) {

        super(ID, tokenManager);
        rand = new Random();
        readyForPickup = false;
        isDriving = false;
        setRandomSouthDest();
        isRunning = true;
        isInEmergency = false;
        if(ID%2==0){
            isWorkingNorth = true;
        }
        else{
            isWorkingNorth = false;
        }
        this.location = GPSLocation;
        currentArea = LocationStatus.SOUTH_END;
        this.startTokenTimer();
        this.start();
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }


    @Override
    public void run() {
        tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,this.tokenID,location));

        while (isRunning)
        {
            try
            {
                Message m = this.messages.take();
                processMessage(m);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Employee Token #"+this.tokenID+" is shutting down.");
    }


    @Override
    protected void startTokenTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                messages.put(new MoveToken());
            }
        };

        this.timer.schedule(task, 0, 17);
    }

    /**
     * Here is the main Logic for the employee behavior
     *
     * what the goal is to do
     *
     * 1. the employee gets spawned at the entrance location
     * 2. an employee will be randomly assigned to work on north or south end
     * 3. if at south end since it is already here it will just walk around randomly
     * 4. if the token works at north end
     *      a it walks around for 30 seconds before making its way to the pickup location
     *      b. it requests a pickup
     *      c. it waits here until it can get in a car
     *      d. it gets in the car and drives to the North end
     * 5. once at north it it just performs a random walk
     */
    private void handleTimerTask() {
        //first check to see if we are ready for pickup
        if(readyForPickup){
            //we can ignore this message and return:
            //this was generated before we could cancel the timer;
            return;
        }
        if(!isInEmergency) {
            //if the employee is working the north end but at south it should walk to the pickup location
            if (currentArea == LocationStatus.SOUTH_END && isWorkingNorth) {
                //but only after some time has gone by
                Point2D sp = MapInfo.SOUTH_PICKUP_LOCATION;
                //TODO set this to be Randomly generated number
                if (counter % 1809 == 0) {
                    if (walkDest != sp) {
                        walkDest = sp;
                    }
                }
                //if the walk dest is the pickup location and we are close enough to it
                //we should send tokenready message and cancel the timer
                if (walkDest == sp) {
                    //check how close we are
                    if (isCloseToLoc(sp) ) {
                        readyForPickup = true;
                        tokenManager.sendMessage(new TokenReadyToLeave(this.tokenID, currentArea));
                        timer.cancel();
                        return;
                    }
                }
            }
        }else {

            //if the walk dest is the pickup location and we are close enough to it
            //we should send tokenready message and cancel the timer
            if (walkDest == MapInfo.NORTH_PICKUP_LOCATION) {
                //check how close we are
                if (isCloseToLoc(MapInfo.NORTH_PICKUP_LOCATION)) {
                    readyForPickup = true;
                    tokenManager.sendMessage(new TokenReadyToLeave(this.tokenID, currentArea));
                    timer.cancel();
                    return;
                }
            }
        }

        //lets finish walking now
        moveToken();
        tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,tokenID, location));
        counter++;
        if(counter ==0 ){
            counter =1;
        }
    }

    /**
     * move the token around
     */
    private void moveToken() {

        double xinc;
        double yinc;
        if(walkDest.getX()-location.getX()>=0){
            xinc = .1;
        }else{
            xinc = -.1;
        }
        if(walkDest.getY()-location.getY()>=0){
            yinc = .1;
        }else{
            yinc = -.1;
        }



        if(currentArea == LocationStatus.SOUTH_END){
            //get a new walk dest
            //System.out.println("are we close to walkdest after stepping? " +isCloseToLoc(walkDest));
            if(location.getX()<walkDest.getX()+1 &&location.getX()>walkDest.getX()-1 &&
                    location.getY()>walkDest.getY()-1&&location.getY()<walkDest.getY()+1) {
                //System.out.println("we are setting the new point?");

                setRandomSouthDest();
            }

        }
        else{//we are on the north end


            //get a new walk dest
            if(location.getX()<walkDest.getX()+1 &&location.getX()>walkDest.getX()-1 &&
                    location.getY()>walkDest.getY()-1&&location.getY()<walkDest.getY()+1) {
                setRandomNorthDest();
            }

        }

        location = location.add(xinc,yinc);
    }

    private void setRandomSouthDest() {
        double x = rand.nextDouble() * MapInfo.SOUTHBUILDING_WIDTH + MapInfo.UPPER_LEFT_SOUTH_BULDING.getX();
        double y = rand.nextDouble() * MapInfo.SOUTHBUILDING_HEIGHT + MapInfo.UPPER_LEFT_SOUTH_BULDING.getY();
        walkDest = new Point2D(x, y);
    }

    /**
     * process the input message accordingly
     * @param m
     */
    @Override
    protected synchronized void processMessage(Message m)
    {

        if(m instanceof ShutDown) {
            isRunning = false;
            timer.cancel();
        }
        else if (m instanceof EnterEmergencyMode) {
            if(!isInEmergency){
                isInEmergency = true;
                if(currentArea == LocationStatus.NORTH_END && !isDriving){
                    walkDest = MapInfo.NORTH_PICKUP_LOCATION;
                }
                if(currentArea == LocationStatus.SOUTH_END&& walkDest==MapInfo.SOUTH_PICKUP_LOCATION){
                    if(readyForPickup) {
                        readyForPickup = false;
                        startTokenTimer();
                    }
                    setRandomSouthDest();
                }

            }
        }
        else if (m instanceof ExitEmergencyMode) {
            if(isInEmergency){
                isInEmergency=false;
                if(currentArea == LocationStatus.NORTH_END && isWorkingNorth && !isDriving){
                    readyForPickup = false;

                    setRandomNorthDest();
                }
            }
        }
        else if (m instanceof CGCRequestHealth) {
            tokenManager.sendMessage(new UpdatedHealth(Entity.EMPLOYEE_TOKEN,this.tokenID,healthStatus));
        }
        else if(m instanceof CGCRequestLocation){
            tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,this.tokenID,this.location));
        }
        else if(m instanceof MoveToken){
            //ignore if driving
            if(isDriving){
                return;
            }
            handleTimerTask();
        }
        else if(m instanceof UpdatedDrivingLocation){
            UpdatedDrivingLocation m2 = (UpdatedDrivingLocation)m;
            isDriving = true;
            location = new Point2D(m2.getCurrentCarLocation().getX(),m2.getCurrentCarLocation().getY());
            tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,tokenID, location));
        }
        else if(m instanceof TourCarArrivedAtDropOff){
            //System.out.println("employee Token" + tokenID +"received TourCarArrivedAtDropOff");
            TourCarArrivedAtDropOff m2 = (TourCarArrivedAtDropOff)m;
            isDriving=false;
            readyForPickup=false;
            if(m2.getDropOffLocation()==LocationStatus.NORTH_END){
                location = MapInfo.NORTH_PICKUP_LOCATION;
                currentArea = LocationStatus.NORTH_END;
                tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,tokenID, location));
                if(!isInEmergency) {
                    setRandomNorthDest();
                }
                else{
                    walkDest = MapInfo.NORTH_PICKUP_LOCATION;
                }
                //System.out.println("new north destination is: " + walkDest);
                this.startTokenTimer();
            }else{
                location = MapInfo.SOUTH_PICKUP_LOCATION;
                currentArea = LocationStatus.SOUTH_END;
                tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,tokenID, location));
                setRandomSouthDest();
                this.startTokenTimer();
            }
        }
        else {
            System.out.println("The employee Token can not handle Message: " + m);
        }
    }

    /**
     * this should set the walkDest to a random destination allowed on the north end.
     */
    private void setRandomNorthDest() {
        double x = rand.nextDouble()*MapInfo.MAP_WIDTH;
        double y = rand.nextDouble()*(MapInfo.UPPER_LEFT_PATROL_BOX.getY()-MapInfo.BOTTOM_RIGHT_TREX_PIT.getY())+MapInfo.BOTTOM_RIGHT_TREX_PIT.getY();
        walkDest = new Point2D(x,y);
    }

    private boolean isCloseToLoc(Point2D p) {


        return ((location.getX() < p.getX() + 1) && (location.getX() > p.getX() - 1) &&
                (location.getY() > p.getY() - 1) && (location.getY() < p.getY() + 1));
    }
}
