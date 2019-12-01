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
    private int counter = 0;
    private Random rand;
    private Point2D walkDest;
    private boolean readyForPickup;


    public EmployeeToken(int ID, TokenManager tokenManager, Point2D GPSLocation)
    {

        super(ID, tokenManager);
        rand = new Random();
        readyForPickup = false;
        double x = rand.nextDouble()*MapInfo.SOUTHBUILDING_WIDTH+MapInfo.UPPER_LEFT_SOUTH_BULDING.getX();
        double y = rand.nextDouble()*MapInfo.SOUTHBUILDING_HEIGHT+MapInfo.UPPER_LEFT_SOUTH_BULDING.getY();
        walkDest = new Point2D(x,y);
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
        this.run();
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }


    @Override
    public void run() {

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
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendMessage(new TokenTimerTask());
            }
        };

        this.timer.schedule(task, 0, 17);
    }

    private void handleTimerTask() {
        //first check to see if we are ready for pickup
        if(readyForPickup){
            //we can ignore this message and return:
            //this was generated before we could cancel the timer;
            return;
        }
        //if the employee is working the north end but at south it should walk to the pickup location
        else if(currentArea== LocationStatus.SOUTH_END && isWorkingNorth){
            //but only after some time has gone by
            Point2D sp =MapInfo.SOUTH_PICKUP_LOCATION;
            if(counter % 3309 ==0){
                if(walkDest!= sp){
                    walkDest = sp;
                }
            }
            //if the walk dest is the pickup location and we are close enough to it
            //we should send tokenready message and cancel the timer
            if(walkDest == sp){
                //check how close we are
                if(walkDest.getX()<sp.getX()+1 &&walkDest.getX()>sp.getX()-1 &&
                walkDest.getY()>sp.getY()-1&&walkDest.getY()<sp.getY()+1){
                    readyForPickup = true;
                    
                    timer.cancel();
                }
            }
        }
        else if(currentArea == LocationStatus.SOUTH_END){

        }
        else{

        }

        counter++;
    }


    @Override
    protected synchronized void processMessage(Message m)
    {

        if(m instanceof ShutDown) {
            isRunning = false;
        }
        else if (m instanceof EnterEmergencyMode) {
            if(!isInEmergency){
                isInEmergency = true;
                //TODO here we wmay need to do more
            }
        }
        else if (m instanceof ExitEmergencyMode) {
            if(isInEmergency){
                isInEmergency=false;
            }
        }
        else if (m instanceof CGCRequestHealth) {
            tokenManager.sendMessage(new UpdatedHealth(Entity.EMPLOYEE_TOKEN,this.tokenID,healthStatus));
        }
        else if(m instanceof CGCRequestLocation){
            tokenManager.sendMessage(new UpdatedLocation(Entity.EMPLOYEE_TOKEN,this.tokenID,this.location));
        }
        //borrowed to try and get random movement going, honestly It's patchwork to get things working.
        else if(m instanceof TokenTimerTask){
            handleTimerTask();
        }
        else if (m instanceof MoveToken) {

        }
        else {
            System.out.println("The employee Token can not handle Message: " + m);
        }
    }
}
