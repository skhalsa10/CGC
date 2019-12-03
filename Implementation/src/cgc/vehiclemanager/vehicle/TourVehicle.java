package cgc.vehiclemanager.vehicle;

import cgc.utils.Entity;
import cgc.utils.LocationStatus;
import cgc.utils.MapInfo;
import cgc.utils.messages.*;
import cgc.vehiclemanager.VehicleManager;
import javafx.geometry.Point2D;

import java.util.*;

/**
 * this is a speciel extension of the Vehicle class the generates Tour related behavior.
 * for example this cars movemove may only move towards pickup locations and back and fourht on the main highway
 *
 * the Tour vehicle may receive a message from the Vehicle Manager to give it a destination
 *  1. set destination and behave accordingly
 *
 * the Tour vehicle may receive a message from the Vehicle Manager to update it with it's location
 *  1. Send message to VehicleManager with updated location
 *
 * the Tour Vehicle may receive a message from the vehicle manager to update it with its current health status
 *  1. send message to vehicleManager with current healthstatus.
 *
 * the Tour vehicle may receive a message from the Vehicle Manager
 *
 * The Tour Vehicle may send a message to the Vehicle manager that it has arrived at destination
 *
 * The Tour vehicle may send a message to the Vehicle Manager that is ready to pick up at South/North
 *
 * The Tour Vehicle may receive message from VehicleManager EnterEmergencyMode
 *  1. it will respond by following emergency protocal behavior
 *
 * the Tour Vehicle May receive message from VehicleManager to ExitEmergencyMode
 *  1. respond accordingly
 *
 * The Tour Vehicle may receive a message from VehicleManager to ShutDown
 *  1. all threads will shut down gracefully.
 */
public class TourVehicle extends Vehicle {

    private boolean run;
    private boolean emergencyMode;
    private boolean isDriving;
    private final double DISTANCE = MapInfo.SOUTH_PICKUP_LOCATION.distance(MapInfo.NORTH_PICKUP_LOCATION);
    private LocationStatus carEnd;
    // keep track when driving, when it arrives, empty the queue.
    private LinkedList<Integer> tokensInCar;
    private Point2D randomGarageDestination;
    private Timer timer;

    public TourVehicle(int ID, VehicleManager vehicleManager, Point2D location) {
        super(ID, vehicleManager, location);
        this.run = true;
        this.emergencyMode = false;
        this.isDriving = false;
        // initially when created, the tour vehicles are in south garage.
        this.carEnd = LocationStatus.SOUTH_GARAGE;
        this.randomGarageDestination = null;

        this.start();
    }

    @Override
    public void run() {
        vehicleManager.sendMessage(new UpdatedLocation(Entity.TOUR_VEHICLE,this.ID,location));

        while (run) {
            try {
                Message m = this.messages.take();
                processMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void startVehicleTimer() {
        //TODO build the timer and timer task here.
        // this can be used to update the location of the car over time
    }

    private void startDrivingToNorthPickupTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToNorthPickUp = new MoveCarToNorthPickUp();
                messages.put(moveCarToNorthPickUp);
            }
        };
        // this task is been triggered 60 fps, so the car moves 60 times per second.
        this.timer.schedule(task, 0, 17);
    }

    private void startDrivingToSouthPickupTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToSouthPickUp = new MoveCarToSouthPickUp();
                messages.put(moveCarToSouthPickUp);
            }
        };
        // this task is been triggered 60 fps, so the car moves 60 times per second.
        this.timer.schedule(task, 0, 17);
    }

    private void startDrivingToNorthDropOffTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToNorthDropOff = new MoveCarToNorthDropOff();
                messages.put(moveCarToNorthDropOff);
            }
        };
        // this task is been triggered 60 fps, so the car moves 60 times per second.
        this.timer.schedule(task, 0, 17);
    }

    private void startDrivingToSouthDropOffTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToSouthDropOff = new MoveCarToSouthDropOff();
                messages.put(moveCarToSouthDropOff);
            }
        };
        // this task is been triggered 60 fps, so the car moves 60 times per second.
        this.timer.schedule(task, 0, 17);
    }

    private void startDrivingToNorthGarageTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToNorthGarage = new MoveCarToNorthGarage();
                messages.put(moveCarToNorthGarage);
            }
        };

        this.timer.schedule(task, 0, 17);
    }

    private void startDrivingToSouthGarageTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message moveCarToSouthGarage = new MoveCarToSouthGarage();
                messages.put(moveCarToSouthGarage);
            }
        };

        this.timer.schedule(task, 0, 17);
    }

    private Point2D getRandomNorthGarageDestination() {
        // incrementing by 2 to (inclusive) random bounds just so the random numbers dont end up at the
        // corner of the garage box and we can't see the car.
        Random randomBounds = new Random();
        double xLeftBound = MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getX() + 5;
        double xRightBound = MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getX() + MapInfo.GARAGE_WIDTH - 5;
        double yMinBound = MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getY() + 5;
        double yMaxBound = MapInfo.UPPER_LEFT_TOURVEHICLE_NORTH_GARAGE.getY() + MapInfo.GARAGE_HEIGHT - 5;

        // random destination in north garage.
        return new Point2D(xLeftBound + (xRightBound - xLeftBound) * randomBounds.nextDouble(),
                yMinBound + (yMaxBound - yMinBound) * randomBounds.nextDouble());
    }

    private Point2D getRandomSouthGarageDestination() {
        // incrementing by 2 to (inclusive) random bounds just so the random numbers dont end up at the
        // corner of the garage box and we can't see the car.
        Random randomBounds = new Random();
        double xLeftBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + 5;
        double xRightBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getX() + MapInfo.GARAGE_WIDTH - 5;
        double yMinBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + 5;
        double yMaxBound = MapInfo.UPPER_LEFT_TOURVEHICLE_SOUTH_GARAGE.getY() + MapInfo.GARAGE_HEIGHT - 5;

        // random destination in south garage.
        return new Point2D(xLeftBound + (xRightBound - xLeftBound) * randomBounds.nextDouble(),
                yMinBound + (yMaxBound - yMinBound) * randomBounds.nextDouble());
    }

    @Override
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    @Override
    protected synchronized void processMessage(Message m) {
        if (m instanceof ShutDown) {
            System.out.println("Tour Vehicle " + this.ID + " is shutting down.");
            this.run = false;
            try {
                this.timer.cancel();
            } catch (NullPointerException e) {
                System.out.println("No need to cancel timer, already cancelled.");
            }
        }
        else if(m instanceof CGCRequestHealth) {
            Message updatedHealth = new UpdatedHealth(Entity.TOUR_VEHICLE, this.ID, this.healthStatus);
            this.vehicleManager.sendMessage(updatedHealth);
        }
        else if(m instanceof CGCRequestLocation) {
            Message updatedLocation = new UpdatedLocation(
                    Entity.TOUR_VEHICLE, this.ID, new Point2D(this.location.getX(), this.location.getY()));
            this.vehicleManager.sendMessage(updatedLocation);
        }
        else if(m instanceof EnterEmergencyMode) {
            if (!this.emergencyMode) {
                this.emergencyMode = true;
                // TODO: Handle the cases in emergency mode.
            }
        }
        else if(m instanceof ExitEmergencyMode) {
            if (this.emergencyMode) {
                this.emergencyMode = false;
                // TODO: Handle the cases of resuming the timer and going back to normal mode.
            }
        }
        else if (m instanceof BeginDrivingToPickup) {
            BeginDrivingToPickup m2 = (BeginDrivingToPickup) m;
            LocationStatus pickUpLocation = m2.getPickupLocation();

            switch (pickUpLocation) {
                case NORTH_PICKUP:
                    if (!isDriving) {
                        isDriving = true;
                        startDrivingToNorthPickupTimer();
                    }
                    break;
                case SOUTH_PICKUP:
                    if (!isDriving) {
                        isDriving = true;
                        startDrivingToSouthPickupTimer();
                    }
                    break;
            }
        }
        else if (m instanceof MoveCarToNorthPickUp) {
            // the car's location is at the north garage currently.
            // no need to change x since its straight vertical line.
            Point2D dest = MapInfo.NORTH_PICKUP_LOCATION;

            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);
            // check if we are close to the destination, inside a 1 pixel circle of radius, then we have arrived.
            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = MapInfo.NORTH_PICKUP_LOCATION;
                carEnd = LocationStatus.NORTH_PICKUP;
                isDriving = false;
                this.timer.cancel();

                Message carArrivedAtPickUp = new TourCarArrivedAtPickup(this.ID, LocationStatus.NORTH_PICKUP);
                vehicleManager.sendMessage(carArrivedAtPickUp);
            }
            // updated Driving location with empty tokens, no tokens yet.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, new LinkedList<>());
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
        else if (m instanceof MoveCarToSouthPickUp) {
            // the car's current location is at south garage.
            Point2D dest = MapInfo.SOUTH_PICKUP_LOCATION;

            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);

            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = MapInfo.SOUTH_PICKUP_LOCATION;
                carEnd = LocationStatus.SOUTH_PICKUP;
                isDriving = false;
                this.timer.cancel();

                Message carArrivedAtPickUp = new TourCarArrivedAtPickup(this.ID, LocationStatus.SOUTH_PICKUP);
                vehicleManager.sendMessage(carArrivedAtPickUp);
            }

            // updated Driving location with empty tokens, no tokens yet since the car is going from garage
            // to pickup location.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, new LinkedList<>());
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
        else if (m instanceof BeginDrivingToDropOff) {
            BeginDrivingToDropOff m2 = (BeginDrivingToDropOff) m;
            this.tokensInCar = m2.getTokensInCarId();

            switch (carEnd) {
                case NORTH_PICKUP:
                    if (!isDriving) {
                        isDriving = true;
                        startDrivingToSouthDropOffTimer();
                    }
                    break;
                case SOUTH_PICKUP:
                    if (!isDriving) {
                        isDriving = true;
                        startDrivingToNorthDropOffTimer();
                    }
                    break;
            }
        }
        else if (m instanceof MoveCarToNorthDropOff) {
            //System.out.println("MoveCarToNorthDropOff has been received by car " + ID);
            // car's initial location is the South_PickUp_location when this message is called initially.
            Point2D dest = MapInfo.NORTH_PICKUP_LOCATION;

            LinkedList<Integer> tokensInsideCar = this.tokensInCar;


            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);

            // close to north drop off location.
            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = MapInfo.NORTH_PICKUP_LOCATION;
                carEnd = LocationStatus.NORTH_END;
                isDriving = false;
                this.timer.cancel();

                System.out.println("ABout to generate the TourCarArrivedAtDropOff with list of ids: " + tokensInsideCar);
                Message carArrivedAtDropOff = new TourCarArrivedAtDropOff(this.ID,LocationStatus.NORTH_END, tokensInsideCar);
                vehicleManager.sendMessage(carArrivedAtDropOff);

                // load of passengers (tokens) inside the car, need to remove member variable list here
                // that's why stored inside the local list, so later i can send message with updatedDrivingLocation.
                if (tokensInCar.size() > 0) {
                    tokensInCar.clear();
                }
            }

            // update driving location as the car moves to north dropoff with all the tokens in the car.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, tokensInsideCar);
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
        else if (m instanceof MoveCarToSouthDropOff) {
            // car's initial location is the NORTH_PICKUP_LOCATION when this message is called initially.
            Point2D dest = MapInfo.SOUTH_PICKUP_LOCATION;
            LinkedList<Integer> tokensInsideCar = this.tokensInCar;

            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);

            // close to south drop off location.
            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = MapInfo.SOUTH_PICKUP_LOCATION;
                carEnd = LocationStatus.SOUTH_END;
                isDriving = false;
                this.timer.cancel();

                Message carArrivedAtDropOff = new TourCarArrivedAtDropOff(this.ID,LocationStatus.SOUTH_END, tokensInsideCar);
                vehicleManager.sendMessage(carArrivedAtDropOff);

                // load of passengers (tokens) inside the car, need to remove member variable list here
                // that's why stored inside the local list, so later i can send message with updatedDrivingLocation.
                if (tokensInCar.size() > 0) {
                    tokensInCar.clear();
                }
            }
            // update driving location as the car moves to south dropoff with all the tokens in the car.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, tokensInsideCar);
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
        else if (m instanceof BeginDrivingToGarage) {
            BeginDrivingToGarage m2 = (BeginDrivingToGarage) m;
            LocationStatus garageLocation = m2.getGarageLocation();

            switch (garageLocation) {
                case NORTH_GARAGE:
                    if (!isDriving) {
                        isDriving = true;
                        this.randomGarageDestination = getRandomNorthGarageDestination();
                        startDrivingToNorthGarageTimer();
                    }
                    break;
                case SOUTH_GARAGE:
                    if (!isDriving) {
                        isDriving = true;
                        this.randomGarageDestination = getRandomSouthGarageDestination();
                        startDrivingToSouthGarageTimer();
                    }
                    break;
            }
        }
        else if (m instanceof MoveCarToNorthGarage) {
            // car's initial location is NORTH_PICKUP_LOCATION when this message is called initially.
            // dest should be random location inside north garage, its fixed for this message triggering events.
            Point2D dest = this.randomGarageDestination;

            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);

            // close to north garage location.
            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = dest;
                carEnd = LocationStatus.SOUTH_END;
                isDriving = false;
                this.timer.cancel();

                Message carArrivedAtDropOff = new TourCarArrivedAtGarage(this.ID,LocationStatus.NORTH_GARAGE);

                vehicleManager.sendMessage(carArrivedAtDropOff);
            }
            // update driving location as the car moves to south dropoff with all the tokens in the car.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, this.tokensInCar);
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
        else if (m instanceof MoveCarToSouthGarage) {
            // car's initial location is SOUTH_PICKUP_LOCATION when this message is called initially.
            // destination should be random location inside south garage, its fixed for this message triggering events.
            Point2D dest = this.randomGarageDestination;

            location = location.add((dest.getX()-location.getX())/DISTANCE*2, (dest.getY()-location.getY())/DISTANCE*2);

            // close to south garage location.
            if(dest.getX()-1<location.getX() &&
                    location.getX()<dest.getX()+1 &&
                    dest.getY()-1<location.getY() &&
                    location.getY()<dest.getY()+1) {

                location = dest;
                carEnd = LocationStatus.SOUTH_GARAGE;
                isDriving = false;
                this.timer.cancel();

                Message carArrivedAtGarage = new TourCarArrivedAtGarage(this.ID, LocationStatus.SOUTH_GARAGE);
                vehicleManager.sendMessage(carArrivedAtGarage);
            }
            // update driving location as the car moves to south garage without any tokens.
            Message updatedDrivingLocation = new UpdatedDrivingLocation(this.ID, location, new LinkedList<>());
            vehicleManager.sendMessage(updatedDrivingLocation);
        }
    }
}
