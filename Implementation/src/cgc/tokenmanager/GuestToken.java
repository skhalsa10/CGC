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
 * this token encapsulates the behavior os the Guest. in the real world it would just report data to the token
 * manager. but in this simulation it also simulates behavior over time..
 *
 * the guest is similar to the employee but it will view the TREX for awhile and then head back to the south end and deactivate
 *
 * @version 1
 * @author started by matt and finished by Siri
 *
 * Skeleton were written by Anas and Siri
 *
 */
public class GuestToken extends Token
{

    private  int viewingTRexTrigger;
    private int whenToLeaveSouth;
    private boolean isRunning;
    private boolean isInEmergency;
    private boolean readyToDeactivate;
    private LocationStatus currentArea;
    private int counter = 1;
    private Random rand;
    private Point2D walkDest;
    private boolean readyForPickup;
    private boolean isDriving;
    private boolean doneViewingTRex;
    private double distance;


    public GuestToken(int ID, TokenManager tokenManager, Point2D GPSLocation)
    {
        super(ID, tokenManager);
        rand = new Random();
        this.isInEmergency=false;
        this.isRunning = true;
        this.readyToDeactivate = false;
        this.doneViewingTRex = false;
        this.viewingTRexTrigger = rand.nextInt(8000)+3000;//we will add current counter to this
        //lets randomize when this token will leave the south end after spawned
        this.whenToLeaveSouth = rand.nextInt(5000)+1;
        this.currentArea = LocationStatus.SOUTH_END;
        this.readyForPickup=false;
        this.isDriving = false;
        this.location = GPSLocation;
        this.healthStatus = true;
        setRandomSouthDest();
        this.timer = new Timer();
        this.startTokenTimer();
        this.start();
    }

    private void setRandomSouthDest() {

        double xLeftBound = MapInfo.UPPER_LEFT_SOUTH_BULDING.getX()+2;
        double xRightBound = MapInfo.UPPER_LEFT_SOUTH_BULDING.getX() + MapInfo.SOUTHBUILDING_WIDTH;
        double yMinBound = MapInfo.UPPER_LEFT_SOUTH_BULDING.getY() + 2;
        double yMaxBound = MapInfo.UPPER_LEFT_SOUTH_BULDING.getY() + MapInfo.SOUTHBUILDING_HEIGHT;

        walkDest =new Point2D(xLeftBound + (xRightBound - xLeftBound) * rand.nextDouble(),
                yMinBound + (yMaxBound - yMinBound) * rand.nextDouble());

        //System.out.println("Walk Dest initialized to: "+ walkDest);
        distance = location.distance(walkDest);
    }

    @Override
    public void sendMessage(Message m) {
        messages.put(m);
    }

    @Override
    public void run() {
        tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,this.tokenID,location));
        while (isRunning) {
            //System.out.println("inside guest token run at count " + counter);
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
        System.out.println("Guest Token #"+this.tokenID+" is shutting down. at count " + counter);
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

        this.timer.schedule(task, 17, 17);
    }




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
                if(currentArea == LocationStatus.NORTH_END && !doneViewingTRex && !isDriving){
                    readyForPickup = false;
                    //to fix a big maybe not the best way but actually makes it look more realistic
                    //when we exit emergency and the token is on the left or right side of trex loop until
                    // we get a point on the same side.
                    if(location.getY()>MapInfo.TREX_PIT_HEIGHT+1) {
                        setRandomNorthDest();
                    }
                    //if on left side of pit make sure point is on left side
                    else if(location.getX()<=MapInfo.UPPER_LEFT_TREX_PIT.getX()){
                        do{
                            setRandomNorthDest();
                        }while(walkDest.getX()!= MapInfo.UPPER_LEFT_TREX_PIT.getX()-10);
                    }
                    else if(location.getX()>=MapInfo.UPPER_RIGHT_TREX_PIT.getX()){
                        do{
                            setRandomNorthDest();
                        }while(walkDest.getX()!= MapInfo.UPPER_RIGHT_TREX_PIT.getX()+10);
                    }
                    else{
                        System.out.println("error picking somethign in exit emergency mode");
                    }

                    //set the visiting stay timer
                    viewingTRexTrigger+=counter;
                }
            }
        }
        else if (m instanceof CGCRequestHealth) {
            tokenManager.sendMessage(new UpdatedHealth(Entity.GUEST_TOKEN,this.tokenID,healthStatus));
        }
        else if(m instanceof CGCRequestLocation) {
            tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,this.tokenID, this.location));
        }
        else if (m instanceof MoveToken) {
            //ignore if driving
            if(isDriving || readyForPickup){
                return;
            }
            handleMoveToken();
        }
        else if(m instanceof UpdatedDrivingLocation){
            UpdatedDrivingLocation m2 = (UpdatedDrivingLocation)m;
            isDriving = true;
            location = new Point2D(m2.getCurrentCarLocation().getX(),m2.getCurrentCarLocation().getY());
            tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,tokenID, location));
        }
        else if(m instanceof TourCarArrivedAtDropOff){
            TourCarArrivedAtDropOff m2 = (TourCarArrivedAtDropOff)m;
            isDriving=false;
            readyForPickup = false;
            if(m2.getDropOffLocation()==LocationStatus.NORTH_END){
                currentArea = LocationStatus.NORTH_END;
                location = MapInfo.NORTH_PICKUP_LOCATION;
                tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,tokenID, location));
                //if the emergency mode was not initaited while driving we can go see the trex
                if(!isInEmergency) {
                    setRandomNorthDest();
                    viewingTRexTrigger+=counter;
                }
                else{
                    walkDest = MapInfo.NORTH_PICKUP_LOCATION;
                }
                this.startTokenTimer();
            }else{
                currentArea = LocationStatus.SOUTH_END;
                location = MapInfo.SOUTH_PICKUP_LOCATION;
                tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,tokenID, location));
                if(doneViewingTRex) {
                    readyToDeactivate = true;
                    walkDest = MapInfo.ENTRANCE.add(0,50);
                }else{
                    setRandomSouthDest();
                }
                startTokenTimer();
            }
        }
    }

    /**
     * when a guest arrive to the north end they will hover around the fence
     */
    private void setRandomNorthDest() {
        int side = rand.nextInt(3);
        double x;
        double y;
        if(side ==0){
            x = MapInfo.UPPER_LEFT_TREX_PIT.getX()-10;
            y = rand.nextDouble() * MapInfo.TREX_PIT_HEIGHT;
        }else if(side ==1){
            x= rand.nextDouble()*MapInfo.TREX_PIT_WIDTH+MapInfo.UPPER_LEFT_TREX_PIT.getX();
            y= MapInfo.BOTTOM_RIGHT_TREX_PIT.getY()+10;
        }else{
            x = MapInfo.UPPER_RIGHT_TREX_PIT.getX()+10;
            y = rand.nextDouble() * MapInfo.TREX_PIT_HEIGHT;
        }
        walkDest = new Point2D(x,y);
    }

    private void handleMoveToken() {
        if(!isInEmergency) {
            //if the guest is on the south side and not ready to deactivate it
            // should explore the building before getting ready to leave to the north end
            if (currentArea == LocationStatus.SOUTH_END && !readyToDeactivate) {
                Point2D sp = MapInfo.SOUTH_PICKUP_LOCATION;

                if ((counter % whenToLeaveSouth) == 0) {
                    if (walkDest != sp) {
                        walkDest = sp;
                        distance = location.distance(walkDest);
                    }
                }
                //if the walk dest is the pickup location and we are close enough to it
                //we should send tokenready message and cancel the timer
                if (walkDest == sp) {
                    //check how close we are
                    if (isCloseToLoc(sp)) {
                        readyForPickup = true;
                        tokenManager.sendMessage(new TokenReadyToLeave(this.tokenID, currentArea));
                        timer.cancel();
                        return;
                    }
                }
            }
        }
            //will perform similar check for if we ar eon the north end.
            if (currentArea == LocationStatus.NORTH_END) {
                Point2D np = MapInfo.NORTH_PICKUP_LOCATION;
                //first check to see if we are done seeing the trex
                if (counter % viewingTRexTrigger == 0) {
                    doneViewingTRex = true;
                    if (walkDest != np) {
                        walkDest = np;
                        distance = location.distance(walkDest);
                    }
                }
                if (isCloseToLoc(np) && walkDest == np) {
                    readyForPickup = true;
                    tokenManager.sendMessage(new TokenReadyToLeave(this.tokenID, currentArea));
                    timer.cancel();
                    return;
                }

            }

        moveToken();
        tokenManager.sendMessage(new UpdatedLocation(Entity.GUEST_TOKEN,tokenID, location));
        counter++;
        if(counter ==0 ){
            counter =1;
        }
    }

    private void moveToken() {
       // System.out.println("moving guest token current loc: " +location + "and Walkdest" +walkDest);
       if(currentArea==LocationStatus.SOUTH_END) {
           double xinc = (walkDest.getX()-location.getX())/distance;
           double yinc = (walkDest.getY()-location.getY())/distance;
           //System.out.println("distance is "+distance + " xinc "+xinc);
           //lets see if we can deactivate the token
           if(isCloseToLoc(walkDest)) {
               if (readyToDeactivate) {
                   tokenManager.sendMessage(new DeactivateToken(this.tokenID, Entity.GUEST_TOKEN));
                   this.timer.cancel();
                   this.isRunning = false;
               } else {
                   //if we are not ready to deactivate pick a random dest
                   setRandomSouthDest();
                   distance = location.distance(walkDest);

               }
           }
           // we must move after all checks are performed to eliminate bug where token gets stuck at pickup location
           location = location.add(xinc,yinc);
       }else if(currentArea==LocationStatus.NORTH_END){
           Point2D pinc = genPointInc();
          location = location.add(pinc.getX(),pinc.getY());
       }else{
            System.out.println("Error in Move token for guest");
        }


    }

    private Point2D genPointInc() {
        double xinc;
        double yinc;
        if(walkDest == MapInfo.NORTH_PICKUP_LOCATION){
            yinc = (walkDest.getY()-location.getY())/distance;
            xinc = getNorthX();

        }else{
            xinc = (walkDest.getX()-location.getX())/distance*3;
            yinc = getNorthY();
        }

        return new Point2D(xinc,yinc);
    }

    private double getNorthY() {
        double xinc = (walkDest.getX()-location.getX())/distance*3;
        double yinc = (walkDest.getY()-location.getY())/distance;
        if(location.getX()+xinc<MapInfo.UPPER_RIGHT_TREX_PIT.getX()&& location.getX()+xinc>MapInfo.UPPER_LEFT_TREX_PIT.getX()){
            if(location.getY()+yinc<MapInfo.TREX_PIT_HEIGHT+10){
                return 0;
            }
        }
        return yinc;
    }

    private double getNorthX() {
        double xinc =(walkDest.getX()-location.getX())/distance*3;
        double yinc = (walkDest.getY()-location.getY())/distance;
        if(location.getY()+yinc<MapInfo.TREX_PIT_HEIGHT){
            if(location.getX()+xinc<MapInfo.UPPER_RIGHT_TREX_PIT.getX()+5&&
                    location.getX()+xinc>MapInfo.UPPER_LEFT_TREX_PIT.getX()-5){
                return 0;
            }
        }
        return xinc;
    }

    private boolean isCloseToLoc(Point2D np) {
        return location.getX() < np.getX() + 1 && location.getX() > np.getX() - 1 &&
                location.getY() > np.getY() - 1 && location.getY() < np.getY() + 1;
    }
}
