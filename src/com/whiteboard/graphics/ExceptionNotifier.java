
package com.whiteboard.graphics;
import javax.swing.JOptionPane;

/**
 *
 * @author lsdpirate
 */
public class ExceptionNotifier {
    
    public static void notifyException(String s){
        
       JOptionPane.showMessageDialog(null, s, "Error",JOptionPane.ERROR_MESSAGE);
       
        
    }
}
