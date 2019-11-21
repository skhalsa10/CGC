package cgc.messages;

/**
 * Once the token is created then, The tokenManager will pass on token info such as location and tokenID to CGC.
 */
public class TokenInfo implements Message {

    // TODO: Add tokenID, location and other appropriate properties that needs to be send to CGC.

    public TokenInfo() {

    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public int compareTo(Message o) {
        return 0;
    }
}
