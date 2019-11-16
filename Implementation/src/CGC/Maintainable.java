package CGC;

/**
 * This interface encorces Maintainable behvavior. This allows for the ability
 *  to check the health in this case. In a more complex implementation it may define other functions for
 *  Maintainability but for the simple version we will only have a boolean to keep track of current health
 *  It is up to the implementing class to keep track of the health and produce a boolean when health is checked.
 */
public interface Maintainable {

    /**
     * We will only enforce the ability to get a true or false value for the health of the implementing class.
     * In a real world this is obviously more complex and can not be kept track with a single boolean.
     * @return a boolean on whether the implementing class is currently in a healthy state or if it is in need of maintanance
     *      * This Originally returned a boolean ^^^^ but because of the threaded nature of this project.
     *      * This has changed to void. What this should really do is place a message into the implementing classes
     *      * blocking queue to tell it to update the healthstatus with the CGC when it finds time to.
     */
    void checkHealth();
}
