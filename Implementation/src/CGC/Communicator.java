package CGC;

import CGC.Messages.Message;

/**
 * the purpose of this interface is to enforce the ability to receive Messages if a class implements this
 * that another class will be able to send a message to it via Class.sendMessage(m). the implementing class SHOULD place
 * this incoming message inside of a blocking queue to be processed when appropriate.
 */
public interface Communicator {
    void sendMessage(Message m);
}
