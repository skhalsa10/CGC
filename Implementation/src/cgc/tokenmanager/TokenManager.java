package cgc.tokenmanager;

import cgc.CGC;
import cgc.Communicator;
import cgc.messages.Message;

/**
 *  This Class will manage the communication with all Token devices this includes Guests and
 *  Employees.
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
public class TokenManager extends Thread implements Communicator {

    public  TokenManager(CGC cgc){

    }


    @Override
    public void run() {

    }

    @Override
    public void sendMessage(Message m) {

    }
}
