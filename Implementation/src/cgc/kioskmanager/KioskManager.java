package cgc.kioskmanager;

import cgc.CGC;
import cgc.Communicator;
import cgc.messages.Message;

/**
 * This Class will manage the communication with all Kiosks. It will also manage the finance logic.
 *
 * The Kiosk Manager will receive a message from the Pay Kiosk that a token was purchased.
 *      1. It will then log the transaction with the transaction analyzer.
 *      2. it will send a message to the CGC to generate a new Guest Token
 *
 * The Kiosk Manager May get a Message from the CGC to retrieve updated Finance information
 *      1. it should respond by sending updated FinanceInfo Message
 *
 * The Kiosk Manager May receive a Shutdown Message from the CGC
 *      1. it will need to send a Shutdown Message to all Pay Kiosk
 *      2. it will then shut down gracefully itself.
 *
 * The Kiosk Manager may receive a message from the CGC to report the Health of all the Pay Kiosks
 *      1. It will respond with a message with the current health of all Pay Kiosks
 *
 */
public class KioskManager extends Thread implements Communicator {
    //this is needed to send messages back up to the CGC
    private CGC cgc;
    private FinancialAnalyzer financialAnalyzer;
    //TODO need datastructure to keep track of active kiosks
    //TODO need a data structure that associated the Kiosk ID with its associated Health status


    public KioskManager(CGC cgc){
        this.cgc = cgc;
    }

    /**
     * This is enforced by the Communicator interface it allows classes that have a reference to the Kiosk
     * Manager to be able to send a message to it.
     * @param m
     */
    @Override
    public void sendMessage(Message m) {
        //TODO place message into Priority blocking queue
    }

    @Override
    public void run() {
        //TODO loop and wait on blocking queue until Shutdown message received.
        //TODO when a message is receive it will call processMessage(m)
    }


    private void processMessage(Message m){
        //TODO check what instance m is and take appropriate action
    }
}
