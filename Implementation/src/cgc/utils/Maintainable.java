package cgc.utils;

/**
 * this is not really needed but it enforces that there is a healthstatus boolean to report on.
 * In the real world this may enforce more behavior is defined but it is almost unneeded
 *
 */
public interface Maintainable {
     boolean healthStatus = true;

}
