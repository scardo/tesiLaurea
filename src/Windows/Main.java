package Windows;

import Data.Datasource;
import Data.MapPanel;
import Data.Trajectory;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.postgis.LineString;
import org.postgis.PGgeometry;

/**
 *
 * @author alessandropandolfo
 */
@SuppressWarnings("UnusedAssignment")
public class Main extends JFrame {

    private JFrame legColori;
   
    private static MapPanel map;
    private JButton selTra;
    private JButton showColor;
    private JTextField selLat;
    private JTextField selLon;
    private ButtonGroup timeRangeSelector;
    private JRadioButton dayButton;
    private JRadioButton weekButton;
    private JRadioButton monthButton;
    private JRadioButton yearButton;
    private UtilDateModel dateModel;
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;
    private JButton selTime;
    private Trajectory selectedTrajectory;
    private JMapViewer mapNavigator;
    private JButton detail;
    private JPanel dettaglioTra;
    private JLabel idTr;
    private JLabel nInters;
    private JLabel tRange;
    private JButton spaceInt;
    private JButton tsInt;
    private JButton stats;
    private JComboBox<String> colRange;

    private final Datasource ds;
    //posizione iniziale
    public final static Coordinate DEFAULT_COORD = new Coordinate(40, 116.5);

    public Datasource getDs() {
        return ds;
    }

    public Main() {
        ds = Datasource.getInstance();
        initComponents();

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        double time[] = new double[2];
        LineString geom = null;
        ICoordinate p1 = map.getPosition(0, map.getHeight());
        ICoordinate p2 = map.getPosition(map.getWidth(), 0);
        try {
            geom = (LineString) PGgeometry.geomFromString("LINESTRING ("
                    + p1.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p1.getLat() + " "
                    + ")");

        } catch (SQLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (map.getTrajectory() != null && map.getTrajectory().size() > 0) {
            time = ds.getTimeBorder(map.getTrajectory(), geom);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("Trajectoty Viewer");
        this.setExtendedState(MAXIMIZED_BOTH);
        
        /*
         * Init map navigator
         */
        mapNavigator = new JMapViewer() {
            @Override
            public void setZoom(int i, Point p) {
                super.setZoom(13, p);
            }
        };
        mapNavigator.setDisplayPosition(DEFAULT_COORD, 14);
        this.add(mapNavigator);
        mapNavigator.setZoomContolsVisible(false);
        mapNavigator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        //set map navigator controller
        DefaultMapController controlNavigator = new DefaultMapController(mapNavigator);
        controlNavigator.setMovementMouseButton(1);
        controlNavigator.setMovementEnabled(true);
        
        /*
         * Init map 
         */
        map = new MapPanel();
        map.setZoomContolsVisible(false);
        map.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        map.setDisplayPosition(DEFAULT_COORD, 15);
        
        /*
         * Init position selector
         */
        selLat = new JTextField("Latitudine");
        selLat.setSize(20, 5);
        selLon = new JTextField("Longitudine");
        selTra = new JButton();
        selTra.setText("Locate");
        
        
        showColor = new JButton("colors map");
        
        idTr = new JLabel("Trajectory: N/D");
        idTr.setFont(new Font(idTr.getFont().getName(), Font.PLAIN, 15));
        nInters = new JLabel("Number of visible intersections: N/D");
        nInters.setFont(new Font(nInters.getFont().getName(), Font.PLAIN, 15));
        tRange = new JLabel("Time range: N/D");
        tRange.setFont(new Font(tRange.getFont().getName(), Font.PLAIN, 15));        
        spaceInt = new JButton("Calculate spatial intersections");
        spaceInt.setVisible(false);
        tsInt = new JButton("Calculate time-space intersections");
        tsInt.setVisible(false);
        
        
        
        
        dettaglioTra = new JPanel();
        dettaglioTra.setBackground(Color.white);
        dettaglioTra.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        
        colRange = new JComboBox();
        colRange.addItem("Hour");
        colRange.addItem("Day of Week");
        colRange.addItem("Day Of Month");
        colRange.addItem("Month");
        colRange.setSelectedIndex(1);
        
        
 
        detail = new JButton("View Details");
        stats= new JButton("Statistics");
        /*
         * Init time selector
         */
        timeRangeSelector = new ButtonGroup();
        dateModel = new UtilDateModel();
        
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        
        datePanel = new JDatePanelImpl(dateModel, p);
        datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
        dateModel.setYear(2010);
        
        //init time radio button
        (dayButton = new JRadioButton()).setText("Day");
        (weekButton = new JRadioButton()).setText("Week");
        (monthButton = new JRadioButton()).setText("Month");
        (yearButton = new JRadioButton()).setText("Year");
        monthButton.setSelected(true);
        
        timeRangeSelector.add(dayButton);
        timeRangeSelector.add(weekButton);
        timeRangeSelector.add(monthButton);
        timeRangeSelector.add(yearButton);
        
        selTime = new JButton("Time filter");
        
        //set default time
        long tmp = (long) 1230826.919106 * 1000000; //01-01-2009
        dateModel.setValue(new java.sql.Date(tmp));
        this.selezioneTempo();
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        initListener();
        
        initLayout();
       


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    public void selezioneCoordinate() {
        if (selLat.getText().equals("Latitudine") || selLon.getText().equals("Longitudine")) {
            return;
        }
        Coordinate coord = new Coordinate(Double.parseDouble(selLat.getText()),
                Double.parseDouble(selLon.getText()));
        mapNavigator.setDisplayPosition(coord, 14);
    }

    private void trajDetail() {
        map.setDisplayPosition(mapNavigator.getPosition(), 15);
        map.setLoad(true);
        map.setSelected(null);
        selectedTrajectory = null;
    }

    private void selezioneTempo() {
        if (dateModel.getValue() != null) {
            long selectedDate = dateModel.getValue().getTime();
            long finDate = selectedDate;
            if (dayButton.isSelected()) {
                finDate += 86400000;
            }
            if (weekButton.isSelected()) {
                finDate += 86400000 * 7;
            }
            if (monthButton.isSelected()) {
                finDate += (long) 86400000 * 30;
            }
            if (yearButton.isSelected()) {
                finDate += (long) 86400000 * 365;

            }

            double timeRange[] = new double[2];
            timeRange[0] = selectedDate;
            timeRange[1] = finDate;
            map.setTimeRange(timeRange);
            map.setDisplayPosition(map.getPosition(), 15);
        }
    }

    private void displayMousePosition(int x, int y) {
        ICoordinate coord = map.getPosition(x, y);
        selLat.setText(" " + coord.getLat());
        selLon.setText(" " + coord.getLon());
    }

    private void initLayout() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(map, 700, 700, 700)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(selLat)
                                        .addGap(10, 10, 10)
                                        .addComponent(selLon)
                                        .addGap(100, 100, 100)
                                        .addComponent(dayButton)
                                        .addGap(30, 30, 30)
                                        .addComponent(weekButton)
                                        .addGap(30, 30, 30)
                                        .addComponent(monthButton)
                                        .addGap(30, 30, 30)
                                        .addComponent(yearButton)
                                        .addGap(30, 30, 30)
                                        .addComponent(datePicker)
                                        .addGap(30, 30, 30)
                                        .addComponent(colRange)
                                        .addGap(10, 10, 10)
                                        .addComponent(mapNavigator, 450, 450, 450)
                                )
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(selTra)
                                        .addGap(10, 10, 10)
                                        .addComponent(selTime)
                                        .addGap(10, 10, 10)
                                        .addComponent(showColor)
                                        .addGap(100, 100, 100)
                                        .addComponent(detail)
                                        .addGap(20, 20, 20)
                                        .addComponent(stats)
                                        .addGap(20, 20, 20)
                                ))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(432, 432, 432))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(dettaglioTra, 700, 700, 700)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(map, 500, 500, 500)
                                                .addGap(15, 15, 15)
                                                .addComponent(dettaglioTra, 189, 189, 189)
                                        )
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(selLat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(selLon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(70, 70, 70)
                                                .addComponent(dayButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(weekButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(monthButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(yearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(colRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(55, 55, 55)
                                                .addComponent(mapNavigator, 370, 370, 370)
                                        )
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(42, 42, 42)
                                                .addComponent(selTra)
                                                .addGap(70, 70, 70)
                                                .addComponent(selTime)
                                                .addGap(95, 95, 95)
                                                .addComponent(showColor)
                                                .addGap(53, 53, 53)
                                                .addComponent(detail)
                                                .addGap(10, 10, 10)
                                                .addComponent(stats)
                                                
                                        ))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dettaglioTra.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.fill = GridBagConstraints.HORIZONTAL;
        con.weightx = 10;
        con.weighty = 10;
        con.gridx = 0;
        con.gridy = 0;
        dettaglioTra.add(idTr, con);
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridx = 0;
        con.gridy = 2;
        dettaglioTra.add(nInters, con);
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridx = 1;
        con.gridy = 2;
        dettaglioTra.add(spaceInt, con);
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridx = 1;
        con.gridy = 3;
        dettaglioTra.add(tsInt, con);
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridx = 0;
        con.gridy = 1;
        dettaglioTra.add(tRange, con);
    }

    private void initListener() {
        map.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedTrajectory = map.selectTrajectory(e.getX(), e.getY());
                if (selectedTrajectory != null) {
                    map.setSelected(selectedTrajectory);
                    System.out.println("selezione: " + selectedTrajectory.getId_t());
                    selectedTrajectory.spaceIntersectionsWithTrajectory(map);
                    idTr.setText("Trajectory: " + selectedTrajectory.getId_t());
                    nInters.setText("Number of visible intersections: " + selectedTrajectory.getIntersects().size());
                    Timestamp ts1 = null;
                    Timestamp ts2 = null;
                    try {
                        double tr[] = new double[2];
                        tr = selectedTrajectory.timeBoundsOfTraj();
                        ts1 = new Timestamp((long) tr[0]);
                        ts2 = new Timestamp((long) tr[1]);
                    } catch (SQLException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    spaceInt.setVisible(true);
                    tsInt.setVisible(true);
                    tRange.setText("Time range: " + ts1.toString().substring(0, ts1.toString().length() - 2)
                            + "  -  " + ts2.toString().substring(0, ts2.toString().length() - 2));
                    map.setDisplayPosition(mapNavigator.getPosition(), 15);
                    map.setLoad(true);
                }else{
                    JOptionPane.showMessageDialog(null, "Nessuna traiettoria selezionata");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        map.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                displayMousePosition(e.getX(), e.getY());
            }
        });
        selTra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                selezioneCoordinate();
            }
        }
        );
        selTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selezioneTempo();
                map.setVisible(true);
            }
        });
        showColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (legColori instanceof ColorDesc) {
                    legColori.dispose();
                }
                legColori = new ColorDesc((selectedTrajectory != null) ? 4 : colRange.getSelectedIndex());
                legColori.setVisible(true);
                legColori.setAlwaysOnTop(true);
            }
        });
        detail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Trajectory.setColorRapp(colRange.getSelectedIndex());
                trajDetail();
            }
        });
        stats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        selezioneTempo(); //update the current time filter
                        new StatsJFrame(mapNavigator.getPosition(),map.getTimeRange()).setVisible(true);
                        
                    }
                });
                        
                
                
            }
        });
        
        spaceInt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int scelta = JOptionPane.showConfirmDialog(null,"L'operazione richiede molto tempo. Continuare?");
                if(scelta==JOptionPane.YES_OPTION){
                    ArrayList<Trajectory> inters = ds.spaceIntersect(selectedTrajectory);
                    JOptionPane.showMessageDialog(null, "Numero intersezioni spaziali: "+inters.size());
                }
            }
        });
        
        tsInt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int scelta = JOptionPane.showConfirmDialog(null,"L'operazione richiede molto tempo. Continuare?");
                if(scelta==JOptionPane.YES_OPTION){
                    ArrayList<Trajectory> inters = ds.spacetimeIntersect(selectedTrajectory);
                    JOptionPane.showMessageDialog(null, "Numero intersezioni spazio-temporali: "+inters.size());
                }
            }
        });
        
    }
}
