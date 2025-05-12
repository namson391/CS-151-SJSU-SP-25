PLS INSTALL INTELIJI AND UPLOAD ALL FILES!!

There are two packages for this project: tools vs. tg

----------------------------------------------------------
1. tools
----------------------------------------------------------
package tools;

public enum Heading {
    WEST, EAST, SOUTH, NORTH;
    public static Heading parse(String h) {
        if (h.equalsIgnoreCase("WEST")) return WEST;
        if (h.equalsIgnoreCase("EAST")) return EAST;
        if (h.equalsIgnoreCase("SOUTH")) return SOUTH;
        if (h.equalsIgnoreCase("NORTH")) return NORTH;
        return NORTH;
    }
}
----------------------------------------------------------
package tools;

import java.io.Serializable;

public abstract class Model extends Publisher implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName = null;
    private Boolean unsavedChanges = false;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Boolean getUnsavedChanges() {
        return unsavedChanges;
    }
    public void setUnsavedChanges(Boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
    }

    public void changed() {
        unsavedChanges = true;
        notifySubscribers();
    }

}
----------------------------------------------------------
package tools;

import java.util.*;

public class Publisher {
    private Collection<Subscriber> subscribers = new HashSet<Subscriber>();
    public void subscribe(Subscriber s) { subscribers.add(s); }
    public void unsubscribe(Subscriber s) { subscribers.remove(s); }
    public void notifySubscribers() {
        for(Subscriber s: subscribers) {
            s.update();
        }
    }
}
----------------------------------------------------------
package tools;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class RestPoint implements Serializable {
    private int xc, yc;
    private Color color;
    private boolean penUp;

    public RestPoint(int xc, int yc, Color color, boolean penUp) {
        this.xc = xc;
        this.yc = yc;
        this.color = color;
        this.penUp = penUp;
    }

    public RestPoint() {
        this(0, 0, Color.BLACK, false);
    }

    public int getXc() {
        return xc;
    }

    public int getYc() {
        return yc;
    }

    public Color getColor() {
        return color;
    }

    public boolean isPenUp() {
        return penUp;
    }

    public void setPenUp(boolean penUp) {
        this.penUp = penUp;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
----------------------------------------------------------
package tools;

public interface Subscriber {
    void update();
}
----------------------------------------------------------
package tools;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class Utilities {

    public static boolean confirm(String query) {
        int result = JOptionPane.showConfirmDialog((Component)null, query, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        return result == JOptionPane.OK_OPTION;
    }

    public static String ask(String query) {
        return JOptionPane.showInputDialog((Component)null, query);
    }

    public static void inform(String info) {
        JOptionPane.showMessageDialog((Component)null, info);
    }

    public static void inform(String[] items) {
        String helpString = "";

        for(int i = 0; i < items.length; ++i) {
            helpString = helpString + "\n" + items[i];
        }

        inform(helpString);
    }

    public static void error(String gripe) {
        JOptionPane.showMessageDialog((Component)null, gripe, "OOPS!", 0);
    }

    public static void error(Exception gripe) {
        gripe.printStackTrace();
        JOptionPane.showMessageDialog((Component)null, gripe.getMessage(), "OOPS!", 0);
    }

    public static JMenu makeMenu(String name, String[] items, ActionListener handler) {
        JMenu result = new JMenu(name);

        for(int i = 0; i < items.length; ++i) {
            JMenuItem item = new JMenuItem(items[i]);
            item.addActionListener(handler);
            result.add(item);
        }

        return result;
    }

    public static String getFileName(String fName, Boolean open) {
        JFileChooser chooser = new JFileChooser();
        String result = null;
        if (fName != null) {
            chooser.setCurrentDirectory(new File(fName));
        }

        int returnVal;
        if (open) {
            returnVal = chooser.showOpenDialog((Component)null);
            if (returnVal == 0) {
                result = chooser.getSelectedFile().getPath();
            }
        } else {
            returnVal = chooser.showSaveDialog((Component)null);
            if (returnVal == 0) {
                result = chooser.getSelectedFile().getPath();
            }
        }

        return result;
    }
}

----------------------------------------------------------
2. tg
----------------------------------------------------------
package tg;

import java.util.List;
import tools.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Turtle extends Publisher implements Serializable {
    public static int WORLD_SIZE = 250;
    private List<RestPoint> path;

    public Turtle() {
        path = new ArrayList<RestPoint>();
        RestPoint location = new RestPoint(WORLD_SIZE / 2, WORLD_SIZE / 2, Color.RED, false);
        path.add(location);
    }

    public RestPoint getLocation() {
        RestPoint result = null;
        if (path.isEmpty()) {
            // probably should throw an exception here but then
            // paintComponent must catch it and do what?
            result = new RestPoint(0, 0, Color.BLACK,true);
        } else {
            result = path.get(path.size() - 1);
        }
        return result;
    }

    public void move(Heading heading, Integer steps) throws Exception {
        RestPoint dest = null; // destination
        RestPoint location = getLocation();
        switch (heading) {
            case WEST: {
                dest = new RestPoint((Math.max(0, location.getXc() - steps)), location.getYc(), location.getColor(), location.isPenUp());
                break;
            }
            case EAST: {
                dest = new RestPoint(Math.min(WORLD_SIZE, location.getXc() + steps), location.getYc(), location.getColor(), location.isPenUp());
                break;
            }
            case SOUTH: {
                dest = new RestPoint(location.getXc(), Math.min(WORLD_SIZE, location.getYc() + steps), location.getColor(), location.isPenUp());
                break;
            }
            case NORTH: {
                dest = new RestPoint(location.getXc(), Math.max(0, location.getYc() - steps)  % WORLD_SIZE, location.getColor(), location.isPenUp());
                break;
            }
            default: {
                throw new Exception("Invalid command in Turtle.move");
            }
        }
        path.add(dest);
        notifySubscribers();
    }

    public void clear() {
        path.clear();
        RestPoint location = new RestPoint(WORLD_SIZE / 2, WORLD_SIZE / 2, Color.RED, false);
        path.add(location); // start over
        notifySubscribers();
    }

    public void setPenUp(boolean penUp) {
        this.getLocation().setPenUp(penUp);
        notifySubscribers();
    }

    public boolean isPenUp() {
        return getLocation().isPenUp();
    }

    public void setColor(Color newColor) {
        getLocation().setColor(newColor);
    }

    public Color getColor() {
        return getLocation().getColor();
    }

    public Iterator<RestPoint> iterator() {
        return path.iterator();
    }
}
----------------------------------------------------------
package tg;

import tools.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class TurtlePanel extends JPanel implements ActionListener {

    private Turtle turtle;
    private TurtleView view;

    public TurtlePanel() {
        turtle = new Turtle();
        view = new TurtleView(turtle);
        view.setBackground((Color.WHITE));
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground((Color.cyan));

        JButton north, south, east, west, clear, pen, color;

        JPanel p = new JPanel();
        p.setOpaque(false);
        north = new JButton("NORTH");
        north.addActionListener(this);
        p.add(north);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        east = new JButton("EAST");
        east.addActionListener(this);
        p.add(east);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        west = new JButton("WEST");
        west.addActionListener(this);
        p.add(west);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        south = new JButton("SOUTH");
        south.addActionListener(this);
        p.add(south);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        p.add(clear);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        pen = new JButton("Pen");
        pen.addActionListener(this);
        p.add(pen);
        controlPanel.add(p);

        p = new JPanel();
        p.setOpaque(false);
        color = new JButton("Color");
        color.addActionListener(this);
        p.add(color);
        controlPanel.add(p);

        JFrame frame = new JFrame();
        Container cp = frame.getContentPane();
        cp.add(this);
        frame.setJMenuBar(this.createMenuBar());
        frame.setDefaultCloseOperation(3);
        frame.setTitle("Turtle Graphics");
        frame.setSize(500, 300);

        this.setLayout((new GridLayout(1, 2)));
        this.add(controlPanel);
        this.add(view);

        frame.setVisible(true);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar result = new JMenuBar();
        JMenu fileMenu = Utilities.makeMenu("File", new String[]{"New", "Save", "Open", "Quit"}, this);
        result.add(fileMenu);
        JMenu editMenu = Utilities.makeMenu("Edit", new String[]{"NORTH", "SOUTH", "EAST", "WEST", "Clear", "Pen", "Color"}, this);
        result.add(editMenu);
        JMenu helpMenu = Utilities.makeMenu("Help", new String[]{"About", "Help"}, this);
        result.add(helpMenu);
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String cmmd = e.getActionCommand();
            // using the new switch command:
            switch(cmmd) {
                case "NORTH", "EAST", "SOUTH", "WEST" -> {
                    int steps = Integer.parseInt(Utilities.ask("How many steps?"));
                    turtle.move(Heading.parse(cmmd), steps);
                }
                case "Clear" -> turtle.clear();
                case "Pen" -> turtle.setPenUp(!turtle.isPenUp());
                case "Color" -> {
                    Color newColor = JColorChooser.showDialog(null, "Pick a color", turtle.getColor());
                    turtle.setColor(newColor);
                }
                case "Save" -> {
                    String fName = Utilities.getFileName((String) null, false);
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fName));
                    os.writeObject(this.turtle);
                    os.close();
                }
                case "Open" -> {
                    String fName = Utilities.getFileName((String) null, true);
                    ObjectInputStream is = new ObjectInputStream(new FileInputStream(fName));
                    this.turtle = (Turtle) is.readObject();
                    this.view.setTurtle(turtle);
                    is.close();
                }
                case "New" -> {
                    turtle = new Turtle();
                    view.setTurtle(turtle);
                }
                case "Quit" -> System.exit(0); // normal exit
                case "About" -> Utilities.inform("Cyberdellic Designs Turtle Graphics, 2021. All rights reserved.");
                case "Help" -> Utilities.inform(
                        new String[] {
                                "North, South, East, West prompts user for number of steps, then moves turtle in the specified heading",
                                "Clear erases the turtle's path and resets its location",
                                "Pen toggles the turtle's pen up and down",
                                "Color changes the color of the turtle's pen"}
                );
                default -> throw new Exception("Unrecognized command: " + cmmd);
            }
        } catch (Exception gripe) {
            Utilities.error(gripe);
        }
    }

    public static void main(String[] args) {
        TurtlePanel app = new TurtlePanel();
    }
}
----------------------------------------------------------
package tg;

import tools.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class TurtleView extends JPanel implements Subscriber {

    private Turtle turtle;

    public TurtleView(Turtle turtle) {
        this.turtle = turtle;
        turtle.subscribe(this);
        setSize(Turtle.WORLD_SIZE, Turtle.WORLD_SIZE);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        setBorder(blackline);
    }

    // called from file/open and file/new
    public void setTurtle(Turtle newTurtle) {
        turtle.unsubscribe(this);
        turtle = newTurtle;
        turtle.subscribe(this);
        repaint();
    }

    public void update() { repaint(); }

    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        Color oldColor = gc.getColor();
        Iterator<RestPoint> it = turtle.iterator();
        if (it.hasNext()) {
            RestPoint p1 = it.next();
            while(it.hasNext()) {
                RestPoint p2 = it.next();
                if (!p1.isPenUp()) {
                    gc.setColor(p1.getColor());
                    gc.drawLine(p1.getXc(), p1.getYc(), p2.getXc(), p2.getYc());
                }
                p1 = p2;
            }
        }
        // draw the turtle
        gc.setColor(Color.GREEN);
        RestPoint location = turtle.getLocation();
        if (location.isPenUp()) {
            gc.drawOval(location.getXc(),location.getYc() , 10, 10);
        } else {
            gc.fillOval(location.getXc(),location.getYc(), 10, 10);
        }

        gc.setColor(oldColor);
    }
}
----------------------------------------------------------
