package cgc.vehiclemanager;

/**
 * I am not sure exactly how this class behaves. I think it will act as Dispatching logic
 * that the Vehicle Manager can access. I will leave the implementation up to the person that implements
 * the Vehicle Manager to determine how to best use this.
 *
 *  NOTE: if you decide to make this class extends Thread you should do the following:
 *      1. add a PriorityBlockingQueue<Message>
 *      2. make sure to implement the Interface Communicator
 *      3. And pass in a reference to the VehicleManager inside the Constructor
 *
 */
public class Dispatcher {

    public Dispatcher() {
    }
}
