package cgc.surveillancesystem;

import cgc.utils.*;
import cgc.utils.messages.*;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * the T-Rex Monitor class simulates how the real T-Rex monitor would be. This class
 * Will simulate the health it will nto read the biometrics exactly but will keep track of that as a property.
 * It will also simulate the movement of the T-Rex. It is up to the implementor to decide how the T-rex will wonder
 * around the enclosure. The T-REX will NOT leave the enclosure! this can be a feature added AFTER the fact if there is
 * time. There is a Timer and TimerTask to be used for changing data over time, like the x and y coordinates.
 * The timer and timer task might place a message in the blocking queue to perform an action. the main threads run will loop using the
 * blocking queue this will make the thread wait efficiently without using a busy wait.
 *
 * The TRexMonitor may receive EmergencyMode message from surveillance
 *     1. it will inject Dino, put itself in emergency mode.
 *     2. After injecting dino, the TRex Monitor will sendMessage back to SurveillanceSystem which then will send back
 *        message to cgc and cgc will update the gui appropriately.
 */
public class TRexMonitor extends Thread implements Maintainable, Locatable, Communicator {
    private Point2D GPS;
    private SurveillanceSystem surveillanceSystem;
    private boolean isTranquilized;
    private boolean healthStatus;
    private PriorityBlockingQueue<Message> messages;
    private Timer timer;
    private boolean run;
    private boolean emergencyMode;

    private enum Direction {
        EAST, WEST, NORTH, SOUTH, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
    }

    public TRexMonitor(SurveillanceSystem surveillanceSystem) {
        this.run = true;
        this.emergencyMode = false;
        this.healthStatus = true;
        this.surveillanceSystem = surveillanceSystem;
        this.messages = new PriorityBlockingQueue<>();
        // initially place trex at the center of electric fence.
        this.GPS = MapInfo.CENTER_TREX_PIT;

        this.timer = new Timer();
        startTRexTimer();
        start();
    }

    /**
     * Be in a loop and check messages, it will block
     * and wait for messages. That way, the thread is not in a busy wait.
     *
     */
    @Override
    public void run() {
        while (run) {
            try {
                Message m = this.messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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


    /**
     * Instantiates timer and schedules timer tasks to
     * change x,y coordinates, and anything else that needs to happen over time
     */
    private void startTRexTimer() {
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

    private void restartTimer() {
        //this.timer.cancel();
        this.timer = new Timer();
        startTRexTimer();
    }

    /**
     *  This will inject the T-Rex with the tranq if there is one available.
     */
    private void inject() {
        this.healthStatus = false;
        // stop timer and trex movement.
        this.timer.cancel();
        // do we send UpdateLocation and health??
        reportHealth(this.healthStatus);
        updateLocation(this.GPS);
    }


    /**
     * send message to surveillance system.
     */
    private void reportHealth(boolean healthStatus) {
        UpdatedHealth updatedHealth = new UpdatedHealth(Entity.TREX, 1, healthStatus);
        this.surveillanceSystem.sendMessage(updatedHealth);
    }

    /**
     * send message to surveillanceSystem.
     */
    private void updateLocation(Point2D loc) {
        UpdatedLocation updatedLocation = new UpdatedLocation(Entity.TREX, 1, loc);
        this.surveillanceSystem.sendMessage(updatedLocation);
    }

    /**
     * this will take a message and store it in the blocking queue to be processed later.
     * @param m message to be stored.
     */
    @Override
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    private synchronized void processMessage(Message message) {
        if (message instanceof ShutDown) {
            this.run = false;
            this.timer.cancel();
            System.out.println("TRexMonitor is shutting down.");
        }
        else if (message instanceof EnterEmergencyMode) {
            if (!emergencyMode) {
                this.emergencyMode = true;
                inject();
            }
        }
        else if (message instanceof ExitEmergencyMode) {
            this.emergencyMode = false;
            this.healthStatus = true;
            reportHealth(this.healthStatus);
            // resume timer and trex movement.
            restartTimer();
        }
        else if (message instanceof CGCRequestHealth) {
            reportHealth(this.healthStatus);
        }
        else if (message instanceof CGCRequestLocation) {
            updateLocation(this.GPS);
        }
        else if (message instanceof MoveTRex) {
            Point2D pointToBeChanged = changeCoordinates(this.GPS.getX(), this.GPS.getY());
            boolean isLegal = isLegalMove(pointToBeChanged.getX(), pointToBeChanged.getY());
            if (isLegal) {
                this.GPS = pointToBeChanged;
                // Once TRex moves, should update.
                updateLocation(this.GPS);
            }
        }
    }
}

