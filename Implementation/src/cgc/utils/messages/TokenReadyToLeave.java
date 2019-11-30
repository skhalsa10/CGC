package cgc.utils.messages;

import cgc.utils.LocationStatus;

/**
 * Whenever the guest token is build, it should send the message to tokenManager and tokenManager should
 * send a message to CGC so that the vehicle manager can receive this message.
 * The LocationStatus is an enum which is used both for cars and guest tokens.
 * It represents on which end the token is.
 * Also, for the actual south or north end location (a Point2D), please refer to MapInfo, it has those south/north
 * pickup locations defined.
 */
public class TokenReadyToLeave implements Message {
    private int tokenId;
    private LocationStatus tokenEnd;

    public TokenReadyToLeave(int tokenId, LocationStatus tokenLocation) {
        this.tokenId = tokenId;
        this.tokenEnd = tokenLocation;
    }

    public int getTokenId() {
        return tokenId;
    }

    public LocationStatus getTokenEnd() {
        return tokenEnd;
    }
}
