package cgc;

import cgc.Car.Car;
import cgc.Messages.Message;
import cgc.PayKiosk.PayKiosk;
import cgc.Station.Station;
import cgc.Token.Token;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class CGC extends Thread implements Communicator {
    // When designing cgc, threads can only communicate through blocking queue with
    // messages.
    private HashMap<Integer, Token> registeredTokens;
    private HashMap<Integer, PayKiosk> activePayKiosks;
    private HashMap<Boolean, HashMap<Integer, Car>> cars;
    private PriorityBlockingQueue<Message> messages;
    //TODO: Need to keep track of transaction logs.
    private Station stationGUI;
    // TODO: Keep track of health status of every component.
    private int carID;

    public CGC(Station station) {

    }

    @Override
    public void sendMessage(Message m) {

    }

    @Override
    public void run() {

    }

    private void processMessage(Message message) {
        // TODO: check for all instanceof messages and do appropriate work.
    }

    private void logTransaction(Message transaction) {
        // TODO: Deconstruct the message, Cast it to appropriate transaction message type.
        // sendMessage to GUI queue.
    }

    private void sendFinanceStateToStation(Message financeInfo) {
        // TODO: from sendMessage, construct a message containing finance info and send it
        // Station.sendMessage.
    }

    //private void send



}
