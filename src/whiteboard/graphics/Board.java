/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.graphics;


import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.event.*;
import whiteboard.Control;

/**
 * This class describes the whiteboard as seen by any user. The board will
 * display the drawing made by the local user as well as the remote user's if a
 * connection is active. A board is an entity holding two lists of figure
 * representing the local's drawings and the remote's respectively. The board
 * can receive figures from the outside and will add them to the remote's list.
 *
 * @version 1.0
 * @since 1.0
 * @author lsdpirate
 */
public class Board extends JPanel {

    //These lists collect all the figures present on the board (local and host).
    private final static ArrayList<Figure> figures = new ArrayList<>();
    private final static ArrayList<Figure> guestFigures = new ArrayList<>();

    private int currentFigure = -1;
    private int currentGuestFigure = 0;

    /**
     * Made private because for now we don't want a board without contruction
     * infos.
     *
     * @since 1.0
     *
     */
    private Board() {
    }

    /**
     * The official and only constructor used to create a board. It requires the
     * board's size.
     *
     * @param w Width in pixels.
     * @param h Heigth in pixels.
     */
    public Board(int w, int h) {

        MListener mListener = new MListener(this);
        this.addMouseListener(mListener);
        this.addMouseMotionListener(mListener);
        this.newFigure();

        this.setSize(w, h);

        this.setVisible(true);

    }

    /**
     * A method used to add a figure to the remote's list. It's synchronized as
     * we don't want to interfere in any way with the lists and any threads
     * using them.
     *
     * @param f A figure to add.
     */
    public synchronized void addGuestFigure(Figure f) {

        synchronized (guestFigures) {
            guestFigures.add(f);
            this.repaint();
            this.revalidate();

        }

        this.currentGuestFigure++;

    }

    /**
     * Private method used to make space in the local figures list by adding a
     * single spot. Synchronized to avoid interferences.
     */
    private synchronized void newFigure() {
        figures.add(new Figure());
        currentFigure++;
        System.out.println("New figure" + figures.size());

    }

    /**
     * Method used to add a single point to the current drawn figure.
     *
     * @param x X position of the point.
     * @param y Y positoin of the point.
     */
    private synchronized void tracePoint(int x, int y) {

        figures.get(this.currentFigure).addPoint(x, y);

    }

    /**
     * This method is used to notify the board a new drawing has started. This
     * is potentially useless but we will leave this here just in case.
     */
    private synchronized void drawingStarted() {

        this.newFigure();
    }

    /**
     * I don't know how this method got here. Will evaluate it's usefulness.
     *
     * @deprecated
     * @param f A figure to add.
     */
    public synchronized void addFigure(Figure f) {

        figures.add(f);
        this.repaint();
    }

    /**
     * Notification method used to tell the board a drawing just finished. The
     * board will be repainted and the figure will be sent to remote.
     */
    public synchronized void drawingFinished() {

        this.repaint();
        this.revalidate();
        Control.getInstance().sendFigureToRemote(figures.get(this.currentFigure));

    }

    /**
     * This is just an overridden method used to draw figures on the board. It
     * will draw each figure from both lists (Local and remote).
     *
     * @param g Graphics component.
     */
    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        //Draw this board's figures
        synchronized (figures) {
            
            for (Figure f : this.figures) {
                
                g2.setStroke(new BasicStroke (3));
                for (int i = 0; i < f.xPoints.size(); ++i) {
                    int x = f.getxPoints().get(i);
                    int y = f.getyPoints().get(i);

                    if (i > 0) {
                        int px = f.getxPoints().get(i - 1);
                        int py = f.getyPoints().get(i - 1);
                        g2.drawLine(x, y, px, py);

                    }
                    //g.drawOval(x, y, 3, 3);
                    //g.fillOval(x, y, 3, 3);
                }
            }
        }
        //Draw guest figures
        g.setColor(GraphicsConstants.REMOTE_BOARD);
        synchronized (guestFigures) {
            for (Figure f : guestFigures) {

                for (int i = 0; i < f.xPoints.size(); ++i) {
                    int x = f.getxPoints().get(i);
                    int y = f.getyPoints().get(i);
                     g2.drawLine(x, y, x, y);
                    //g.drawOval(x, y, 2, 2);
                    //g.fillOval(x, y, 2, 2);
                }
            }
        }
        MainView.needRepaint();
    }

    /**
     * Private class used to implement events the board will have to listen to.
     * An instance of this class will be added to the events listeners for the
     * board component.
     *
     * @since 1.0
     * @version 1.0
     * @author lsdpirate
     */
    private class MListener implements MouseListener, MouseMotionListener {

        private final Board board;
        private boolean drawing = false;

        public MListener(Board b) {
            this.board = b;

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            this.board.drawingStarted();
            this.board.tracePoint(e.getX(), e.getY());
            this.board.drawingFinished();
        }

        @Override
        public void mousePressed(MouseEvent e) {

            this.drawing = true;
            this.board.drawingStarted();
            System.out.println(this.drawing);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            this.drawing = false;
            this.board.drawingFinished();
            //    System.out.println(this.drawing);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println("Drawing");
//            this.board.drawingStarted();
            this.board.tracePoint(e.getX(), e.getY());
//            this.board.drawingFinished();

        }

        @Override
        public void mouseMoved(MouseEvent e) {

            //  System.out.println(drawing);
//           if(drawing){
//               this.board.tracePoint(e.getX(), e.getY());
//           }
        }

    }

}
