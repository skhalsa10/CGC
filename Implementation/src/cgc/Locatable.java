package cgc;

/**
 * The purpose of this interface is to enforce location services.
 * This forces the behavior of having simulated GPS. This interfaces enforces the ping behavior of the gps
 */
public interface Locatable {

    /**
     * This will give the behavior to check for the current location at any time. It is up to the
     * implementing object to keep track of the point and also how to update the point location.
     * @return a Point representing the current x and y location of the implementing object.
     * This Originally returned a Point ^^^^ but because of the threaded nature of this project.
     * This has changed to void. What this should really do is place a message intot he implementing classes
     * blocking queue to tell it to update the location with the cgc when it finds time to.
     */
    void getLocation();
}
