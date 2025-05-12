PLS INSTALL INTELIJI AND UPLOAD ALL FILES!!

There are two packages for this project: mvc vs. MineFields
-----------------------------------------------------------
1. mvc
-----------------------------------------------------------
package mvc;

public interface AppFactory {

    public Model makeModel();

    public View makeView(Model model);

    public String[] getEditCommands();

    public Command makeEditCommand(Model model, String type, Object source);

    public String getTitle();

    public String[] getHelp();

    public String about();
}
-----------------------------------------------------------
package mvc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppPanel extends JPanel implements Subscriber, ActionListener  {

    protected Model model;
    protected AppFactory factory;
    protected View view;
    protected JPanel controlPanel;
    protected JFrame frame;
    public static int FRAME_WIDTH = 900;
    public static int FRAME_HEIGHT = 600;

    public AppPanel(AppFactory factory) {
        this.factory = factory;
        model = factory.makeModel();
        view = factory.makeView(model);

        view.setBackground((Color.GRAY));

        controlPanel = new JPanel();
        controlPanel.setBackground((Color.PINK));
        setLayout(new GridLayout(1, 2));
        add(controlPanel);
        add(view);
        model.subscribe(this);

        frame = new SafeFrame();
        Container cp = frame.getContentPane();
        cp.add(this);
        frame.setJMenuBar(createMenuBar());
        frame.setTitle(factory.getTitle());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void display() { frame.setVisible(true); }

    public void update() {  /* override in extensions if needed */ }

    public Model getModel() { return model; }

    // testing this out as a utility
    private void add(JComponent control, JPanel controlPanel) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(control);
        controlPanel.add(p);
    }

    // called by file/open and file/new
    public void setModel(Model newModel) {
        this.model.unsubscribe(this);
        this.model = newModel;
        this.model.subscribe(this);
        view.setModel(this.model); // view unsubscribes to old model and subscribes to the new one
        model.changed();
        //alternatively: this.model.copy(model);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar result = new JMenuBar();
        // add file, edit, and help menus
        JMenu fileMenu =
                Utilities.makeMenu("File", new String[] {"New",  "Save", "SaveAs", "Open", "Quit"}, this);
        result.add(fileMenu);

        JMenu editMenu =
                Utilities.makeMenu("Edit", factory.getEditCommands(), this);
        result.add(editMenu);

        JMenu helpMenu =
                Utilities.makeMenu("Help", new String[] {"About", "Help"}, this);
        result.add(helpMenu);

        return result;
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String cmmd = ae.getActionCommand();

            if (cmmd.equals("Save")) {
                Utilities.save(model, false);
            } else if (cmmd.equals("SaveAs")) {
                Utilities.save(model, true);
            } else if (cmmd.equals("Open")) {
                Model newModel = Utilities.open(model);
                if (newModel != null) setModel(newModel);
            } else if (cmmd.equals("New")) {
                Utilities.saveChanges(model);
                setModel(factory.makeModel());
                // needed cuz setModel sets to true:
                model.setUnsavedChanges(false);
            } else if (cmmd.equals("Quit")) {
                Utilities.saveChanges(model);
                System.exit(0);
            } else if (cmmd.equals("About")) {
                Utilities.inform(factory.about());
            } else if (cmmd.equals("Help")) {
                Utilities.inform(factory.getHelp());
            } else { // must be from Edit menu
                Command command = factory.makeEditCommand(model, cmmd, ae.getSource());
                command.execute();
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void handleException(Exception e) {
        Utilities.error(e);
    }
}
-----------------------------------------------------------
package mvc;

public abstract class Command {
    protected Model model;

    public Command(Model model) {
        this.model = model;
    }

    public abstract void execute() throws Exception;
}
-----------------------------------------------------------
package mvc;

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
-----------------------------------------------------------
package mvc;

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
-----------------------------------------------------------
package mvc;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class SafeFrame extends JFrame {

    protected void processWindowEvent(WindowEvent ev) {
        super.processWindowEvent(ev);
        if(ev.getID() == WindowEvent.WINDOW_CLOSING) {
            if (Utilities.confirm("Are you sure? Unsaved changes will be lost!")) {
                System.exit(0);
            }
        }
    }

    public SafeFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
-----------------------------------------------------------
package mvc;

public interface Subscriber {
    void update();
}
-----------------------------------------------------------
package mvc;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

public class Utilities {

    public static JMenu makeMenu(String name, String[] items, ActionListener handler) {
        JMenu result;
        int j = name.indexOf('&');
        if ( j != -1) {
            char c = name.charAt(j - 1);
            String s = name.substring(0, j) + name.substring(j + 1);
            result = new JMenu(s);
            result.setMnemonic(c);
        } else {
            result = new JMenu(name);
        }

        for(int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                result.addSeparator();
            } else {
                j = items[i].indexOf('&');
                JMenuItem item;
                if ( j != -1) {
                    char c = items[i].charAt(j + 1);
                    String s = items[i].substring(0, j) +
                            items[i].substring(j + 1);
                    item = new JMenuItem(s, items[i].charAt(j + 1));
                    item.setAccelerator(
                            KeyStroke.getKeyStroke(c, InputEvent.CTRL_MASK));
                } else { // no accelerator or shortcut key
                    item = new JMenuItem(items[i]);
                }
                item.addActionListener(handler);
                result.add(item);
            }
            //result.addMenuListener(this);
        }
        return result;
    }

    public static String ask(String query) {
        return JOptionPane.showInputDialog(query);
    }
	/*
   public static String getFile(String query) {
   	   return JFileChooser
   }
	 */


    public static boolean confirm(String query) {
        int result = JOptionPane.showConfirmDialog(null,
                query, "choose one", JOptionPane.YES_NO_OPTION);
        return result == 0; // or 1?
    }

    public static void error(String gripe) {
        JOptionPane.showMessageDialog(null,
                gripe,
                "OOPS!",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void error(Exception gripe) {
        gripe.printStackTrace();
        JOptionPane.showMessageDialog(null,
                gripe.toString(),
                "OOPS!",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void inform(String info) {
        JOptionPane.showMessageDialog(null, info,
                "information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void inform(String[] items){
        String helpString = "";
        for (int i = 0; i < items.length; i++){
            helpString = helpString + "\n" + items[i];
        }
        inform(helpString);
    }

    public static void saveChanges(Model model) {
        if (model.getUnsavedChanges() && Utilities.confirm("current model has unsaved changes, continue?"))
            Utilities.save(model, false);
    }

    public static Model open(Model model) {
        saveChanges(model);
        String fName = model.getFileName();
        JFileChooser chooser = new JFileChooser();
        Model newModel = null;
        if (fName != null) {
            chooser.setCurrentDirectory(new File(fName));
        }
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fName = chooser.getSelectedFile().getPath();
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(fName));
            model = (Model)is.readObject();
            is.close();
        } catch (Exception err) {
            Utilities.error(err.getMessage());
        }
        return newModel;
    }

    public static Random rng = new Random(System.currentTimeMillis());

    public static void save(Model model, Boolean saveAs) {
        String fName = model.getFileName();
        if (fName == null || saveAs) {
            JFileChooser chooser = new JFileChooser();
            if (fName != null) {
                chooser.setCurrentDirectory(new File(fName));
            }
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                fName = chooser.getSelectedFile().getPath();
                model.setFileName(fName);
            }
        }
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fName));
            os.writeObject(model);
            model.setUnsavedChanges(false);
            os.close();
            model.setUnsavedChanges(false);
        } catch (Exception err) {
            Utilities.error(err.getMessage());
        }
    }
}
-----------------------------------------------------------
package mvc;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class View extends JPanel implements Subscriber {

    protected Model model;
    // static public Dimension dim;

    public View(Model model) {
        super();
        this.model = model;
        model.subscribe(this);
        // optional border around the view component
        setBorder(LineBorder.createGrayLineBorder());//.createBlackLineBorder());
    }

    public Model getModel() { return model; }

    // called by File/Open and File/New
    public void setModel(Model newModel) {
        if (model != null) model.unsubscribe(this);
        this.model = newModel;
        if (newModel != null) {
            model.subscribe(this);
            update();
        }
    }
    @Override
    public void update() {
        this.repaint();
    }
}
-----------------------------------------------------------
2. MineField
-----------------------------------------------------------
package MineField;

import mvc.*;
import javax.swing.*;
import java.awt.*;

public class Cell extends JLabel {
    Patch patch;
}
-----------------------------------------------------------
package MineField;

import mvc.*;
import java.io.Serializable;

public enum Heading {
    N, E, S, W, NW, NE, SE, SW;
    public static Heading parse(String h) {
        if (h.equalsIgnoreCase("NW")) return NW;
        if (h.equalsIgnoreCase("N")) return N;
        if (h.equalsIgnoreCase("NE")) return NE;
        if (h.equalsIgnoreCase("W")) return W;
        if (h.equalsIgnoreCase("E")) return E;
        if (h.equalsIgnoreCase("SE")) return SE;
        if (h.equalsIgnoreCase("S")) return S;
        if (h.equalsIgnoreCase("SW")) return SW;
        return N;
    }
}
-----------------------------------------------------------
package MineField;

import mvc.*;
import java.io.Serializable;

public class MineField extends Model {
    private Patch[][] patches;
    private int dim;
    private int playerXC;
    private int playerYC;
    private boolean playerDead;
    private int numMoves;
    private int maxMoves;
    private int percentMined;

    public MineField() {
        percentMined = 8;
        dim = 20;
        numMoves = 0;
        maxMoves = 50;
        patches = new Patch[dim][dim];
        // create patches
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                patches[row][col] = new Patch();
                if (Utilities.rng.nextInt(100) < percentMined) {
                    patches[row][col].mined = true;
                }
            }
        }
        playerXC = 0;
        playerYC = 0;
        patches[playerXC][playerYC].mined = false;
        patches[playerXC][playerYC].occupied = true;
        patches[dim - 1][dim - 1].goal = true;
        patches[dim - 1][dim - 1].mined = false;

        // count mined neighbors
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (valid(row + i) && valid(col + j) && patches[row + i][col + j].mined) {
                            patches[row][col].numMinedNbrs++;
                        }
                    }
                }

            }
        }
    }

    public boolean valid(int loc) {
        return 0 <= loc && loc < dim;
    }

    public Patch getPatch(int row, int col) {
        return patches[row][col];
    }

    public void move(Heading heading) throws Exception {
        if (isPlayerDead()) {
            throw new Exception("Game Over!");
        }
        patches[getPlayerXC()][getPlayerYC()].occupied = false;
        int xc = playerXC;
        int yc = playerYC;
        switch (heading) {
            case NE: {
                xc = playerXC - 1;
                yc = playerYC + 1;
                break;
            }
            case N: {
                xc = playerXC - 1;
                break;
            }
            case NW: {
                xc = playerXC - 1;
                yc = playerYC - 1;
                break;
            }
            case W: {
                yc = playerYC - 1;
                break;
            }
            case E: {
                yc = playerYC + 1;
                break;
            }
            case SW: {
                xc = playerXC + 1;
                yc = playerYC - 1;
                break;
            }
            case S: {
                xc = playerXC + 1;
                break;
            }
            case SE: {
                xc = playerXC + 1;
                yc = playerYC + 1;
                break;
            }
            default: throw new Exception("invalid heading");
        }

        // finish up
        if (!valid(xc) || !valid(yc))
            throw new Exception("Player cannot move off the field");
        playerXC = xc;
        playerYC = yc;
        patches[getPlayerXC()][getPlayerYC()].occupied = true;
        setNumMoves(getNumMoves() + 1);
        if (patches[playerXC][playerYC].mined || getNumMoves() > getMaxMoves()) {
            setPlayerDead(true);
            throw new Exception("Game Over!");
        }
        if (patches[playerXC][playerYC].goal){
            setPlayerDead(true);
            throw new Exception("You won!");
        }
        changed();
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getPlayerXC() {
        return playerXC;
    }

    public void setPlayerXC(int playerXC) {
        this.playerXC = playerXC;
    }

    public int getPlayerYC() {
        return playerYC;
    }

    public void setPlayerYC(int playerYC) {
        this.playerYC = playerYC;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public void setPlayerDead(boolean playerDead) {
        this.playerDead = playerDead;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }
}
-----------------------------------------------------------
package MineField;

import mvc.*;
import javax.swing.*;
import java.awt.*;

public class MineFieldFactory implements AppFactory {
    @Override
    public Model makeModel() {
        return new MineField();
    }

    @Override
    public View makeView(Model model) {
        return new MineFieldView((MineField)model);
    }

    @Override
    public String[] getEditCommands() {
        return new String[] {"NW", "N", "NE", "W", "E", "SW", "S", "SE"};
    }

    @Override
    public Command makeEditCommand(Model model, String type, Object source) {
        MoveCommand cmmd = new MoveCommand(model);
        cmmd.setHeading(type);
        return cmmd;
    }

    @Override
    public String getTitle() {
        return "Mine Field";
    }

    @Override
    public String[] getHelp() {
        String[] cmmds = new String[1];
        cmmds[0] = "Move player to goal without hitting a mine";
        return cmmds;
    }

    @Override
    public String about() {
        return "Mine FIeld version 1.0. Copyright 2021 by Cyberdellic Designs";
    }

}
-----------------------------------------------------------
package MineField;

import java.awt.*;
import javax.swing.*;
import mvc.*;

public class MineFieldPanel extends AppPanel {
    private JPanel moveControls;
    public MineFieldPanel(AppFactory factory) {
        super(factory);
        moveControls = createControls();
        controlPanel.add(moveControls);
    }
    private JPanel createControls() {
        JPanel panel = new JPanel(new GridLayout(3,3));
        String[] directions = {"N", "E", "S", "W", "NW", "NE", "SE", "SW"};
        for (int i = 0; i < 8; i++) {
            if (i == 8) {
                // make a blank spot in the middle so the grid is a nice and even 3x3
                panel.add(new JLabel());
            } else {
                JButton button = new JButton(directions[i]);
                button.setActionCommand(directions[i]);
                button.addActionListener(this);
                panel.add(button);
            }
        }
        return panel;
    }

    public static void main(String[] args) {
        AppFactory factory = new MineFieldFactory();
        MineFieldPanel panel = new MineFieldPanel(factory);
        panel.display();
    }
}
-----------------------------------------------------------
package MineField;

import mvc.*;
import javax.swing.*;
import java.awt.*;

public class MineFieldView extends View {

    private Cell cells[][];
    public MineFieldView(MineField m) {
        super(m);

        int dim = m.getDim();
        cells = new Cell[dim][dim];
        setLayout(new GridLayout(dim, dim));
        for(int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                cells[row][col] = new Cell();
                cells[row][col].setHorizontalAlignment(JLabel.CENTER);
                cells[row][col].setText("?");
                cells[row][col].patch = m.getPatch(row, col);
                cells[row][col].setBorder(BorderFactory.createLineBorder(Color.black));
                if (cells[row][col].patch.occupied) {
                    cells[row][col].setBackground(Color.RED);
                    cells[row][col].setBorder(BorderFactory.createLineBorder(Color.white));
                    cells[row][col].setText("" + cells[row][col].patch.numMinedNbrs);
                }
                if (cells[row][col].patch.goal) {
                    cells[row][col].setBackground(Color.WHITE);
                    cells[row][col].setBorder(BorderFactory.createLineBorder(Color.green));
                }
                this.add(cells[row][col]);
            }
        }
    }

    private void initView() {


        MineField m = (MineField)model;
        int dim = m.getDim();

        for(int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                // cells[row][col] = new Cell();
                cells[row][col].setText("?");
                cells[row][col].patch = m.getPatch(row, col);
                cells[row][col].setBorder(BorderFactory.createLineBorder(Color.black));
                if (cells[row][col].patch.occupied) {
                    cells[row][col].setBackground(Color.RED);
                    cells[row][col].setBorder(BorderFactory.createLineBorder(Color.white));
                    cells[row][col].setText("" + cells[row][col].patch.numMinedNbrs);
                }
                if (cells[row][col].patch.goal) {
                    cells[row][col].setBackground(Color.WHITE);
                    cells[row][col].setBorder(BorderFactory.createLineBorder(Color.green));
                }
                //  this.add(cells[row][col]);
            }
        }
    }

    public void setModel(Model newModel) {
        super.setModel(newModel);
        initView();
        repaint();
    }



    public void update() {
        MineField mf = (MineField)model;
        int row = mf.getPlayerXC();
        int col = mf.getPlayerYC();
        cells[row][col].setBackground(Color.RED);
        cells[row][col].setBorder(BorderFactory.createLineBorder(Color.white));
        cells[row][col].setText("" + cells[row][col].patch.numMinedNbrs);
        cells[row][col].repaint();
    }
}
-----------------------------------------------------------
package MineField;

import mvc.*;

public class MoveCommand extends Command {

    Heading heading;
    public MoveCommand(Model m) {
        super(m);
        heading = Heading.N;
    }

    public void setHeading(String h) {
        heading = Heading.parse(h);
    }

    public void execute() throws Exception {
        try {
            MineField m = (MineField)model;
            m.move(heading);
        } catch(Exception e) {
            Utilities.error(e);
        }
    }
}
-----------------------------------------------------------
package MineField;

import mvc.*;

import java.io.Serializable;

public class Patch implements Serializable {
    boolean mined = false;
    int numMinedNbrs = 0;
    boolean occupied = false; // needed?
    boolean goal = false;

}
-----------------------------------------------------------
