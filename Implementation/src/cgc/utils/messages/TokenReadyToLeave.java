package cgc.utils.messages;

import javafx.geometry.Point2D;

/**
 * Whenever the guest token is build, it should send the message to tokenManager and tokenManager should
 * send a message to CGC so that the vehicle manager can receive this message.
 */
public class TokenReadyToLeave implements Message {
    private int tokenId;
    private Point2D tokenLocation;

    public TokenReadyToLeave(int tokenId, Point2D tokenLocation) {
        this.tokenId = tokenId;
        this.tokenLocation = tokenLocation;
    }

    public int getTokenId() {
        return tokenId;
    }

    public Point2D getTokenLocation() {
        return tokenLocation;
    }
}
