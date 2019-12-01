package cgc.tokenmanager;

import cgc.utils.Entity;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * this token encapsulates the behavior os the Guest. in the real world it would just report data to the token
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
public class GuestToken extends Token
{

    //TODO there may also need to be a separate timer and timer task to trigger when a guest visitor is ready to leave exhibit
    private boolean isRunning = true;
    private boolean emergency = false;

    //my variables
    private Timer timer;

    //Borrowed stuff
    private enum Direction {
        EAST, WEST, NORTH, SOUTH, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
    }


    public GuestToken(int ID, TokenManager tokenManager, Point2D GPSLocation)
    {
        super(ID, tokenManager);
        this.location = GPSLocation;
        this.healthStatus = true;
        this.timer = new Timer();
        startTokenTimer();
        start();
    }

    @Override
    public void sendMessage(Message m)
    {
        //TODO place this message in messages queue
        tokenManager.sendMessage(m);
    }


    @Override
    public void run()
    {
        //TODO This should loop and wait on the message queue and shut down only if shutdown is received
        //TODO this will call processMessage(m) to respond accordingly
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
        System.out.println("Guest Token #"+this.tokenID+" is shutting down.");
    }


    //Totally "borrowing" code from TRexMonitor for this.
    //I suppose I would want to have this go to the vehicles from the spawnpoint, then how do they ride in the car?
    //For now I guess I can have them bumble around? Yup for now it will randomly move. Because it's 7 AM and I want sleep.
    @Override
    protected void startTokenTimer()
    {
        //TODO start token timer here and use a timer task with it.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MoveTRex move = new MoveTRex();
                messages.put(move);
            }
        };
        // schedules after every second.
        this.timer.schedule(task, 0, 1000);
    }

    //=========================================================================
    //borrowed
    private Direction randomDirection() {
        Direction[] dir = Direction.values();
        Random rand = new Random();
        // generate random number between 0-7.
        return dir[rand.nextInt(dir.length)];
    }

    private Point2D changeCoordinates(double oldX, double oldY) {
        Direction directionToMove = randomDirection();
        Point2D changedPoint = null;

        switch (directionToMove) {
            case EAST:
                changedPoint = new Point2D(oldX + 3.0, oldY);
                break;
            case WEST:
                changedPoint = new Point2D(oldX - 3.0, oldY);
                break;
            case NORTH:
                changedPoint = new Point2D(oldX, oldY + 3.0);
                break;
            case SOUTH:
                changedPoint = new Point2D(oldX, oldY - 3.0);
                break;
            case NORTHEAST:
                changedPoint = new Point2D(oldX + 3.0, oldY + 3.0);
                break;
            case NORTHWEST:
                changedPoint = new Point2D(oldX - 3.0, oldY + 3.0);
                break;
            case SOUTHEAST:
                changedPoint = new Point2D(oldX + 3.0, oldY - 3.0);
                break;
            case SOUTHWEST:
                changedPoint = new Point2D(oldX - 3.0, oldY - 3.0);
                break;
        }
        return changedPoint;
    }

    private boolean isLegalMove(double x, double y) {
        // creating bounds.
        double leftX = MapInfo.UPPER_LEFT_TREX_PIT.getX();
        double rightX = MapInfo.UPPER_RIGHT_TREX_PIT.getX();

        if (x < leftX || x > rightX || y < 0 || y > MapInfo.TREX_PIT_HEIGHT) {
            return false;
        }

        return true;
    }
    //=============================================================



    @Override
    protected synchronized void processMessage(Message m)
    {
        //TODO process m using instanceof
        if(m instanceof ShutDown)
        {
            isRunning = false;
        }
        else if (m instanceof EnterEmergencyMode)
        {
            emergency = true;
        }
        else if (m instanceof ExitEmergencyMode)
        {
            emergency = false;
        }
        else if (m instanceof CGCRequestHealth)
        {
            //TODO-sendMessage(new UpdatedHealth(this.getName(),this.tokenID,this.healthStatus));
            sendMessage(new UpdatedHealth(Entity.GUEST_TOKEN,this.tokenID,true));
        }
        else if(m instanceof CGCRequestLocation)
        {
            sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,this.tokenID, this.GPSLocation));
        }
        //borrowed to try and get random movement going, honestly It's patchwork to get things working.
        else if (m instanceof MoveToken)
        {
            Point2D pointToBeChanged = changeCoordinates(this.GPSLocation.getX(), this.GPSLocation.getY());
            boolean isLegal = isLegalMove(pointToBeChanged.getX(), pointToBeChanged.getY());
            if (isLegal) {
                this.GPSLocation = pointToBeChanged;
                tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN, this.tokenID, this.GPSLocation));
            }
        }
    }
}
