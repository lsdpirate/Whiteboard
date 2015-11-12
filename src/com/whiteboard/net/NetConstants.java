
package com.whiteboard.net;

/**
 *  Utility class used to store constants for the whiteboard.net package.
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public class NetConstants {
    
    /**
     * Flag used to tell that the following data is a simple figure.
     */
    public final static String FIGURE_DATA_FLAG = "#f";
    
    /**
     * Flag used to tell that the following data is a keep alive.
     */
    public final static String KEEP_ALIVE_FLAG = "#k";
    
    public final static String KEEP_ALIVE_RESPONSE_FLAG = "#K";
    
    
    public final static String FIGURE_FORMAT = "(\\d){1,4}\\,(\\d){1,4};";
    
    /**
     * Default port.
     */
    public final static int DEF_PORT = 22451;
    
    
  
}
