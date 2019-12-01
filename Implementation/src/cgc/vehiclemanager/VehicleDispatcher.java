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
    private Integer activeSouthCar;
    private boolean activeSouthCarAtPickup;
    private Integer activeNorthCar;
    private boolean activeNorthCarAtPickup;

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
        this.activeNorthCarAtPickup = false;
        this.activeSouthCarAtPickup = false;
        this.southTimer = new Timer();
        this.northTimer = new Timer();

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
        // when tour car arrives at pickup location and the tokens on pickup location
        // are less than 10, then this method is called and it
        // waits 60 seconds before dispatching a car.
        TimerTask task = new TimerTask() {
            long countDown = 60;
            @Override
            public void run() {
                countDown--;
                if (countDown < 0) {
                    Message readyToDispatch = new ReadyToDispatch(LocationStatus.SOUTH_PICKUP);
                    messages.put(readyToDispatch);
                    southTimer.cancel();
                }
            }
        };
        // runs the task after each second.
        this.southTimer.schedule(task, 0, 1000);
    }

    private void resetSouthDispatcherTimer() {
        this.southTimer.cancel();
        this.southTimer = new Timer();
        startSouthDispatcherTimer();
    }

    private void startNorthDispatcherTimer() {
        // waits 60 seconds before dispatching a car.
        TimerTask task = new TimerTask() {
            long countDown = 60;
            @Override
            public void run() {
                countDown--;
                if (countDown < 0) {
                    Message readyToDispatch = new ReadyToDispatch(LocationStatus.NORTH_PICKUP);
                    messages.put(readyToDispatch);
                    northTimer.cancel();
                }
            }
        };
        // runs the task after each second.
        this.northTimer.schedule(task, 0, 1000);
    }

    private void resetNorthDispatcherTimer() {
        this.northTimer.cancel();
        this.northTimer = new Timer();
        startNorthDispatcherTimer();
    }

    private synchronized void processMessage(Message m) {
        if (m instanceof ShutDown) {
            System.out.println("Dispatcher is shutting down.");
            this.run = false;
            this.southTimer.cancel();
            this.northTimer.cancel();
        }
        else if(m instanceof EnterEmergencyMode) {
            if(!this.emergencyMode) {
                this.emergencyMode = true;
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
                            //TODO: Edge case, what to do if the north garage has no car?
                            // Immediately dispatch the south car and remove.
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
                        activeNorthCarAtPickup = true;
                        startNorthDispatcherTimer();
                    }
                    break;
                case SOUTH_PICKUP:
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

                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        for (int i = 0; i < northTokensIds.size(); i++) {
                            Integer removedToken = this.northTokensIds.poll();
                            tokensToBeAssigned.add(removedToken);
                        }

                        Message dispatchCar = new DispatchCar(activeNorthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

                        activeNorthCar = null;
                    }
                    break;
                case SOUTH_PICKUP:
                    activeSouthCarAtPickup = false;
                    if (this.southTokensIds.size() < 10) {
                        this.southTimer.cancel();

                        LinkedList<Integer> tokensToBeAssigned = new LinkedList<>();

                        for (int i = 0; i < southTokensIds.size(); i++) {
                            Integer removedToken = this.southTokensIds.poll();
                            tokensToBeAssigned.add(removedToken);
                        }

                        Message dispatchCar = new DispatchCar(activeSouthCar, tokensToBeAssigned);
                        this.vehicleManager.sendMessage(dispatchCar);

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
                    Message dispatchToNorthGarage = new DispatchCarToGarage(m2.getCarId(), LocationStatus.NORTH_GARAGE);
                    vehicleManager.sendMessage(dispatchToNorthGarage);
                    break;
                case SOUTH_END:
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
                    try {
                        this.northCarsIds.put(m2.getCarId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case SOUTH_GARAGE:
                    try {
                        this.southCarsIds.put(m2.getCarId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
