/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.graphics;
import java.util.ArrayList;
/**
 * This class describes a figure as a set of points.
 * The only task this class has is to store points as X and Y coordinates and 
 * to return the values.
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public class Figure {
    
    ArrayList<Integer> xPoints = new ArrayList<>();
    ArrayList<Integer> yPoints = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    public Figure(){}
    
    /**
     * Adds a point to the list of points. Requires the x and y component of
     * the point.
     * @param x X component of the point.
     * @param y Y component of the point.
     */
    public void addPoint(int x, int y){
        this.xPoints.add(x);
        this.yPoints.add(y);
    }

    /**
     * Getter method for the x components of all the points.
     * @return A list of x coordinates of all the points contained in the
     * instance of this class.
     */
    public ArrayList<Integer> getxPoints() {
        return xPoints;
    }

    /**
     * Getter method for the y components of all the points.
     * @return A list of y coordinates of all the points contained in the
     * instance of this class.
     */
    public ArrayList<Integer> getyPoints() {
        return yPoints;
    }
    
    
    
   
}
