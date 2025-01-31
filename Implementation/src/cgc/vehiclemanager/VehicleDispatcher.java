package cgc.vehiclemanager;

import cgc.utils.Communicator;
import cgc.utils.LocationStatus;
import cgc.utils.messages.*;

import java.net.Inet4Address;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * this is a HUGE class. It controls when to send a car to a location basically
 * but the amount of logic involved is massive! it includes sepperate timers for the north and south end
 * it also keeps track of the tour cars and their location or if they are driving to and from pickups
 *
 * @version 1
 * @author Anas
 *
 * Skeleton were written by Anas and Siri
 */
public class VehicleDispatcher extends Thread implements Communicator {

    private boolean run;
    private boolean emergencyMode;
    private PriorityBlockingQueue<Message> messages;
    private VehicleManager vehicleManager;
    private Timer southTimer;
    private Timer northTimer;
    private LinkedBlockingQueue<Integer> northCarsIds;
    private LinkedBlockingQueue<Integer> southCarsIds;
    private LinkedBlockingQueue<Integer> northTokensIds;
    private LinkedBlockingQueue<Integer> southTokensIds;
    //activeSouthcar represents car moving from south garage to south pickup
    private Integer activeSouthCar;
    //represents an active car is currently at the south pickup
    private boolean activeSouthCarAtPickup;
    //activeSouthcar represents car moving from North garage to North pickup
    private Integer activeNorthCar;
    //represents an active car is currently at the Northpickup
    private boolean activeNorthCarAtPickup;
    //represents car moving from North DropOff to North Garage
    private Integer activeNorthDropOffCar;
    //represents car moving from South DropOff to South Garage
    private Integer activeSouthDropOffCar;

    public VehicleDispatcher(VehicleManager vehicleManager) {
        this.run = true;
        this.emergencyMode = false;
        this.vehicleManager = vehicleManager;
        this.messages = new PriorityBlockingQueue<>();
        this.northCarsIds = new LinkedBlockingQueue<>();
        this.southCarsIds = new LinkedBlockingQueue<>();
        this.northTokensIds = new LinkedBlockingQueue<>();
        this.southTokensIds = new LinkedBlockingQueue<>();
        this.activeNorthCar = null;
        this.activeSouthCar = null;
        this.activeNorthDropOffCar = null;
        this.activeSouthDropOffCar = null;
        this.activeNorthCarAtPickup = false;
        this.activeSouthCarAtPickup = false;

        this.start();
    }

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

    @Override
    public void sendMessage(Message m) {
        this.messages.put(m);
    }

    private void startSouthDispatcherTimer() {
        southTimer = new Timer();
        // when tour car arrives at pickup location and the tokens on pickup location
        // are less than 10, then this method is called and it
        // waits 60 seconds before dispatching a car.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message readyToDispatch = new ReadyToDispatch(LocationStatus.SOUTH_PICKUP);
                messages.put(readyToDispatch);
                southTimer.cancel();

                System.out.println("SouthTimer is shutting down." + run);
            }
        };
        // runs after a min, counting down for 1 min.
        this.southTimer.schedule(task, 60000, 60000);
    }

    private void resetSouthDispatcherTimer() {
        this.southTimer.cancel();
        //this.southTimer = new Timer();
        startSouthDispatcherTimer();
    }

    private void startNorthDispatcherTimer() {
        northTimer = new Timer();
        // waits 60 seconds before dispatching a car.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message readyToDispatch = new ReadyToDispatch(LocationStatus.NORTH_PICKUP);
                messages.put(readyToDispatch);
                northTimer.cancel();
                System.out.println("NorthTimer is shutting down." + run);
            }
        };
        this.northTimer.schedule(task, 60000, 60000);
    }

    private void resetNorthDispatcherTimer() {
        this.northTimer.cancel();
        //this.northTimer = new Timer();
        startNorthDispatcherTimer();
    }

    private synchronized void processMessage(Message m) {
        if (m instanceof ShutDown) {
            System.out.println("Dispatcher is shutting down.");
            this.run = false;
            try {
                this.southTimer.cancel();
            } catch (NullPointerException e) {
                System.out.println("No need to cancel timer, already cancelled.");
            }
            try {
                this.northTimer.cancel();
            } catch (NullPointerException e) {
                System.out.println("No need to cancel timer, already cancelled.");
            }

        }
        else if(m instanceof EnterEmergencyMode) {
            if(!this.emergencyMode) {
                this.emergencyMode = true;
                this.southTokensIds.clear();
                if(activeSouthCar!=null) {
                    try {
                        southTimer.cancel();
                    }catch(NullPointerException e){
                        System.out.println("no need to cancel a null timer");
                    }
                    DispatchCarToGarage m2 = new DispatchCarToGarage(activeSouthCar,LocationStatus.SOUTH_GARAGE);
                    vehicleManager.sendMessage(m2);
                    activeSouthCar = null;
                    activeSouthCarAtPickup = false;
                }
            }

        }
        else if(m instanceof ExitEmergencyMode) {
            if(this.emergencyMode) {
                this.emergencyMode = false;
            }
        }
        else if (m instanceof SouthCarId) {
            try {
                SouthCarId m2 = (SouthCarId) m;
                this.southCarsIds.put(m2.getSouthCarId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (m instanceof TokenReadyToLeave) {
            TokenReadyToLeave m2 = (TokenReadyToLeave) m;
            LocationStatus tokenDirection = m2.getTokenEnd();
            //System.out.println("DISPATCHER TOKENREADYTOLEAVE for ID: " + m2.getTokenId());

            switch (tokenDirection) {
                case NORTH_END:
                    try {
                        this.northTokensIds.put(m2.getTokenId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (activeNorthCar == null) {
                        Integer northCarId = this.northCarsIds.poll();
                        if (northCarId != null) {
                            activeNorthCar = northCarId;

                            Message dispatchToPickUp = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                            vehicleManager.sendMessage(dispatchToPickUp);
                        }
                        else {
                            //TODO: Edge case, what to do if the north garage has no car?
                            // Immediately dispatch the south car and remove.
                            //this will make the ActiveNorthDropOff car turn back around
                            System.out.println("activeNorthDropOffCar ID is "+ activeNorthDropOffCar);
                            if(emergencyMode){
                                if(activeNorthDropOffCar != null){
                                    activeNorthCar = activeNorthDropOffCar;
                                    Message dispatchToNorthPickUp = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                                    vehicleManager.sendMessage(dispatchToNorthPickUp);
                                    activeNorthDropOffCar = null;
                                }else{
                                    activeNorthCar = this.southCarsIds.poll();
                                    if (activeNorthCar != null) {
                                        Message dispatchToNorthPickUp = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                                        vehicleManager.sendMessage(dispatchToNorthPickUp);
                                    }
                                }
                            }
                            else{
                                if(activeNorthDropOffCar == null){
                                    activeNorthCar = this.southCarsIds.poll();
                                    if (activeNorthCar != null) {
                                        Message dispatchToNorthPickUp = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                                        vehicleManager.sendMessage(dispatchToNorthPickUp);
                                    }
                                }
                            }
                            //we ignore the case where there IS an ActiveNorthDropOffCar which is driving
                            // to the north garage AND we are NOT in emergencyMode
                            //in this case we want to let the car drive to the garage before
                            // it can turn around and go to the north pickup
                        }
                    }
                    else if (activeNorthCarAtPickup) {
                        // the car is already at pickup and token ready to leave message comes, check
                        // if there are 10 or more tokens now, if yes then we immediately dispatch the car
                        // and tell the car to begin driving now.

                        // since this method is synchronized we can check the size as no other thread will be able to access
                        // this method except one thread at a time.
                        if (northTokensIds.size() >= 10) {
                            activeNorthCarAtPickup = false;
                            this.northTimer.cancel();

                            LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                            // take out 10 tokens from tokens list.
                            for (int i = 0; i < 10; i++) {
                                Integer removedToken = this.northTokensIds.poll();
                                tokensToBeAssigned.add(removedToken);
                            }

                            Message dispatchCar = new DispatchCar(activeNorthCar, tokensToBeAssigned);
                            this.vehicleManager.sendMessage(dispatchCar);

                            activeNorthCar = null;
                        }
                        else {
                            resetNorthDispatcherTimer();
                        }
                    }
                    break;
                case SOUTH_END:
                    try {
                        this.southTokensIds.put(m2.getTokenId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (activeSouthCar == null) {
                        Integer southCarId = this.southCarsIds.poll();
                        if (southCarId != null) {
                            activeSouthCar = southCarId;

                            Message dispatchToPickUp = new DispatchCarToPickup(activeSouthCar, LocationStatus.SOUTH_PICKUP);
                            vehicleManager.sendMessage(dispatchToPickUp);
                        }
                        else {
                            //TODO: Edge case, what to do if the south garage has no car?
                            // Immediately dispatch the north car and remove.
                            //there is no car in the south garage and there NO car driving to the south garage
                            //but we have tokens ready to go to the north? we need to get a car from the north garage
                            if(activeSouthDropOffCar == null && !emergencyMode){
                                activeSouthCar = this.northCarsIds.poll();
                                if (activeSouthCar != null) {
                                    Message dispatchToSouthPickUp = new DispatchCarToPickup(activeSouthCar, LocationStatus.SOUTH_PICKUP);
                                    vehicleManager.sendMessage(dispatchToSouthPickUp);
                                }
                            }
                        }
                    }
                    else if (activeSouthCarAtPickup) {
                        if (southTokensIds.size() >= 10) {
                            activeSouthCarAtPickup = false;
                            this.southTimer.cancel();

                            LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                            // take out 10 tokens from tokens list.
                            for (int i = 0; i < 10; i++) {
                                Integer removedToken = this.southTokensIds.poll();
                                tokensToBeAssigned.add(removedToken);
                            }

                            Message dispatchCar = new DispatchCar(activeSouthCar, tokensToBeAssigned);
                            this.vehicleManager.sendMessage(dispatchCar);

                            activeSouthCar = null;
                        }
                        else {
                            resetSouthDispatcherTimer();
                        }
                    }
                    break;
            }
        }
        else if (m instanceof TourCarArrivedAtPickup) {
            TourCarArrivedAtPickup m2 = (TourCarArrivedAtPickup) m;
            LocationStatus arrivalLocation = m2.getCarDirection();

            switch (arrivalLocation) {
                case NORTH_PICKUP:
                    if (northTokensIds.size() >= 10) {
                        activeNorthCarAtPickup = false;
                        try {
                            this.northTimer.cancel();
                        } catch (NullPointerException e) {
                            System.out.println("No need to cancel timer, already cancelled.");
                        }

                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        // take out 10 tokens from tokens list.
                        for (int i = 0; i < 10; i++) {
                            Integer removedToken = this.northTokensIds.poll();
                            tokensToBeAssigned.add(removedToken);
                        }

                        Message dispatchCar = new DispatchCar(activeNorthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

                        activeNorthCar = null;

                        // if the car reaches at the pickup and
                        // there are so many tokens waiting, then we want to dispatch another car.
                        if (northTokensIds.size() > 0) {
                            Integer northCarId = this.northCarsIds.poll();
                            if (northCarId != null) {
                                activeNorthCar = northCarId;
                                Message dispatchAnotherCar = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                                this.vehicleManager.sendMessage(dispatchAnotherCar);
                            }else{
                                northCarId = this.southCarsIds.poll();
                                if(northCarId != null){
                                    activeNorthCar = northCarId;
                                    Message dispatchAnotherCar = new DispatchCarToPickup(activeNorthCar, LocationStatus.NORTH_PICKUP);
                                    this.vehicleManager.sendMessage(dispatchAnotherCar);
                                }else{
                                    System.out.println("some error that should never happen");
                                }
                            }

                        }
                    }
                    else {
                        activeNorthCarAtPickup = true;
                        startNorthDispatcherTimer();
                    }
                    break;
                case SOUTH_PICKUP:
                    if (southTokensIds.size() >= 10) {
                        activeSouthCarAtPickup = false;
                        try {
                            this.southTimer.cancel();
                        } catch (NullPointerException e) {
                            System.out.println("No need to cancel timer, already cancelled.");
                        }

                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        // take out 10 tokens from tokens list.
                        for (int i = 0; i < 10; i++) {
                            Integer removedToken = this.southTokensIds.poll();
                            tokensToBeAssigned.add(removedToken);
                        }
                        System.out.println("active south car driving to North Dropoff ID is "+ activeSouthCar);
                        Message dispatchCar = new DispatchCar(activeSouthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

                        activeSouthCar = null;

                        // if the car reaches at the pickup and
                        // there are so many tokens waiting, then we want to dispatch another car.
                        if (southTokensIds.size() > 0) {
                            Integer southCarId = this.southCarsIds.poll();
                            if (southCarId != null) {
                                activeSouthCar = southCarId;
                                Message dispatchAnotherCar = new DispatchCarToPickup(activeSouthCar, LocationStatus.SOUTH_PICKUP);
                                this.vehicleManager.sendMessage(dispatchAnotherCar);
                            }
                        }
                    }
                    else {
                        activeSouthCarAtPickup = true;
                        startSouthDispatcherTimer();
                    }
                    break;
            }
        }
        else if (m instanceof ReadyToDispatch) {
            ReadyToDispatch m2 = (ReadyToDispatch) m;
            LocationStatus dispatchFrom = m2.getCarLocation();

            // there should always be < 10 tokens when the timer goes off.
            switch (dispatchFrom) {
                case NORTH_PICKUP:
                    activeNorthCarAtPickup = false;
                    if (this.northTokensIds.size() < 10) {
                        this.northTimer.cancel();

                        // the car is ready to dispatch, so we need to assign tokens to the car.
                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        // making a copy/clone of all tokens before clearing the list.
                        for (Integer id : this.northTokensIds) {
                            tokensToBeAssigned.add(id);
                        }

                        Message dispatchCar = new DispatchCar(activeNorthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

                        // remove everything from this list as the car has all those now.
                        northTokensIds.clear();

                        activeNorthCar = null;
                    }
                    break;
                case SOUTH_PICKUP:
                    activeSouthCarAtPickup = false;
                    if (this.southTokensIds.size() < 10) {
                        this.southTimer.cancel();

                        // the car is ready to dispatch, so we need to assign tokens to the car.
                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        // making a copy/clone of all tokens before clearing the list.
                        for (Integer id : this.southTokensIds) {
                            tokensToBeAssigned.add(id);
                        }
                        Message dispatchCar = new DispatchCar(activeSouthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

                        // remove everything from this list as the car has all those now.
                        southTokensIds.clear();

                        activeSouthCar = null;
                    }
                    break;
            }
        }
        // once arrived at the dropoff, dispatch the car to garage.
        else if (m instanceof TourCarArrivedAtDropOff) {
            TourCarArrivedAtDropOff m2 = (TourCarArrivedAtDropOff) m;
            LocationStatus dropOffLocation = m2.getDropOffLocation();

            switch (dropOffLocation) {
                case NORTH_END:
                    activeNorthDropOffCar = m2.getCarId();
                    Message dispatchToNorthGarage = new DispatchCarToGarage(m2.getCarId(), LocationStatus.NORTH_GARAGE);

                    vehicleManager.sendMessage(dispatchToNorthGarage);
                    break;
                case SOUTH_END:
                    activeSouthDropOffCar = m2.getCarId();
                    Message dispatchToSouthGarage = new DispatchCarToGarage(m2.getCarId(), LocationStatus.SOUTH_GARAGE);
                    vehicleManager.sendMessage(dispatchToSouthGarage);
                    break;
            }
        }
        else if (m instanceof TourCarArrivedAtGarage) {
            TourCarArrivedAtGarage m2 = (TourCarArrivedAtGarage) m;
            LocationStatus garageLocation = m2.getGarageLocation();

            switch (garageLocation) {
                case NORTH_GARAGE:
                    //the tour car arrived at north garage but has not been registered yet
                    //into the northCarIDs list and there as least one token waiting at the north pickup
                    // there is currently no active car going to pickup the waiting token so
                    //we need to send this car back to the north pickup
                    if (this.northCarsIds.size() == 0 && this.northTokensIds.size() > 0 && activeNorthCar == null) {
                        Message dispatchCarToNorthPickUp = new DispatchCarToPickup(m2.getCarId(), LocationStatus.NORTH_PICKUP);
                        vehicleManager.sendMessage(dispatchCarToNorthPickUp);
                        activeNorthCar = m2.getCarId();
                    } else {
                        try {
                            this.northCarsIds.put(m2.getCarId());
                            this.activeNorthDropOffCar = null;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SOUTH_GARAGE:
                    try {
                        this.southCarsIds.put(m2.getCarId());
                        this.activeSouthDropOffCar = null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
