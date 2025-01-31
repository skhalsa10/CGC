package cgc.utils.messages;

/**
 * This interface defines an object that we can send accross blocking Queue or sockets. The goal
 * of the message is that it can encode and encapsulate a message and unifies it as type Message. This will allow
 * for BlockingQueue<Message> the Message will need to be decoded by Class that uses them by checking if the
 * Message m instanceof Implementing Message class.
 *
 * This will also allow each team member to define messages they expect to receive in their class. for example the
 * T-Rex Monitor may expect a Message that is an instanceof RequestGPS the implemeneting class will decode this and
 * may respond by sending a message to the cgc with updated Location
 *
 * The Message interface needs to be comparable so that the priorityBlockingQueue can have the messages in order.
 *
 * @author Siri
 * @version 1
 *
 * Skeleton were written by Anas and Siri
 *
 * */
public interface Message extends Comparable<Message> {
    long timeStamp = System.nanoTime();

    /**
     * Will be used to sort messages in priority blocking queue.
     *
     *
     *
     * The earlier message should be processed first. Emergency Mode should set this to -1 (this should work)
     * This will guarantee top priority. *If we do not want to use a timestamp(which should work) we will
     * need to identify a priority protocol that we can all follow.
     *
     * @return timestamp in nanoseconds
     */
    default long getTimeStamp(){
        return this.timeStamp;
    }


    @Override
    default int compareTo(Message o) {
        long result = this.getTimeStamp() - o.getTimeStamp();
        if (result > 0 ) { return 1; }
        else if (result == 0) { return 0; }
        else { return -1; }
    }
}
