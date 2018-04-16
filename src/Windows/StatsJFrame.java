package Windows;

import Data.Stats.BarPlotChart;
import Data.Stats.PieChart;
import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.postgis.Point;

/**
 *
 * @author insan3
 */
public class StatsJFrame extends javax.swing.JFrame {

    private PieChart pie; //
    private BarPlotChart startBarPlot;
    private BarPlotChart endBarPlot;

    /**
     * Constructor of the jframe
     *
     * timeRange must have 2 elements only,the first as the start, the second as
     * the end.
     *
     * @param ICoordinate c
     * @param double timeRange[]
     */
    public StatsJFrame(ICoordinate c, double timeRange[]) {
        initComponents();

        //set map navigator controller
        DefaultMapController controlNavigator = new DefaultMapController(map) {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {

            }

        };

        controlNavigator.setMovementMouseButton(3);
        controlNavigator.setMovementEnabled(true);

        map.setZoomContolsVisible(false);
        map.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //set the position given by the main frame
        map.setDisplayPosition(c, 13);

        //apply the time filter of the main jframe
        dateTimePicker1.setDate(new Date((long) timeRange[0]));
        dateTimePicker2.setDate(new Date((long) timeRange[1]));

        //reload stats
        this.reload();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pieChart = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        textField = new javax.swing.JTextField();
        dateTimePicker1 = new Data.DateTimePicker();
        dateTimePicker2 = new Data.DateTimePicker();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        startBarPlotChart = new javax.swing.JLabel();
        startBarPlotChart1 = new javax.swing.JLabel();
        endBarPlotChart = new javax.swing.JLabel();
        map = new Data.Stats.HeatMap();
        jComboBoxPrecision = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Statistics");
        setResizable(false);

        jScrollPane1.setHorizontalScrollBar(null);

        jPanel1.setBackground(new java.awt.Color(153, 255, 255));

        pieChart.setBackground(new java.awt.Color(0, 0, 0));

        searchButton.setText("Reload");
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
        });

        textField.setEditable(false);
        textField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        textField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel1.setText("From:");

        jLabel2.setText("To:");

        map.setPreferredSize(new java.awt.Dimension(384, 214));
        map.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                mapMouseWheelMoved(evt);
            }
        });
        map.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mapMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mapMouseReleased(evt);
            }
        });

        jComboBoxPrecision.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "low precision heat map", "medium precision heat map", "high precision heat map", "heat map with dynamic precision" }));
        jComboBoxPrecision.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(map, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dateTimePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                    .addComponent(dateTimePicker2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jComboBoxPrecision, 0, 0, Short.MAX_VALUE)
                            .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(startBarPlotChart, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endBarPlotChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(pieChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 771, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(startBarPlotChart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(1037, 1037, 1037)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateTimePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateTimePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPrecision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)
                        .addGap(90, 90, 90))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(map, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(startBarPlotChart, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(endBarPlotChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(719, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(414, 414, 414)
                    .addComponent(startBarPlotChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(703, Short.MAX_VALUE)))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked
        this.reload();
    }//GEN-LAST:event_searchButtonMouseClicked

    private void mapMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_mapMouseWheelMoved
        map.setZoom(map.getZoom() - evt.getWheelRotation());
        map.clearRasterList();

    }//GEN-LAST:event_mapMouseWheelMoved

    private void mapMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mapMousePressed
        //right button
        if (evt.getButton() == 3) {
            map.clearRasterList();

        }
    }//GEN-LAST:event_mapMousePressed

    private void mapMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mapMouseReleased
        //right button
        if (evt.getButton() == 3) {
            //this.reload();
        }
    }//GEN-LAST:event_mapMouseReleased

    /**
     * Reload the stats
     */
    private void reload() {

        boolean confirmed = false;
        double dayTime[] = {0, 0};

        //refresh the date
        dayTime[0] = (double) dateTimePicker1.getDate().getTime();
        dayTime[1] = (double) dateTimePicker2.getDate().getTime();
        this.map.setDayTime(dayTime);
        Point p[] = new Point[2];
        ICoordinate p0 = map.getPosition(0, map.getSize().height);
        ICoordinate p1 = map.getPosition(map.getSize().width, 0);

        p[0] = new Point(p0.getLon(), p0.getLat());
        p[1] = new Point(p1.getLon(), p1.getLat());

        if (jComboBoxPrecision.getSelectedIndex() == 0) {
            map.updateRasterList(p, map.getZoom(), 2);
            confirmed = true;
        } else if (jComboBoxPrecision.getSelectedIndex() == 1) {
            map.updateRasterList(p, map.getZoom(), 3);
            confirmed = true;
        } else if (jComboBoxPrecision.getSelectedIndex() == 2) {
            map.updateRasterList(p, map.getZoom(), 4);
            confirmed = true;
        } else if (jComboBoxPrecision.getSelectedIndex() == 3) {
            if (JOptionPane.YES_OPTION
                    == JOptionPane.showConfirmDialog(null, "It may take a long time, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION)) {
                map.dynamicUpdateRasterList(p);
                confirmed = true;
            }

        }

        if (confirmed) {

            this.pie = null;
            this.startBarPlot = null;
            this.endBarPlot = null;
            this.pie = new PieChart("distribution over the week", this.map.getVisibleZone(), dayTime);
            this.startBarPlot = new BarPlotChart("start time", this.map.getVisibleZone(), dayTime, true);
            this.endBarPlot = new BarPlotChart("end time", this.map.getVisibleZone(), dayTime, false);
            //write image
            pieChart.setIcon(pie.getImageIcon(pieChart.getSize().width, pieChart.getSize().height));
            startBarPlotChart.setIcon(startBarPlot.getImageIcon(startBarPlotChart.getSize().width, startBarPlotChart.getSize().height));
            endBarPlotChart.setIcon(endBarPlot.getImageIcon(endBarPlotChart.getSize().width, endBarPlotChart.getSize().height));

            textField.setText("Num of traj: " + pie.getTrajNum());

            this.map.paintComponent(map.getGraphics());
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Data.DateTimePicker dateTimePicker1;
    private Data.DateTimePicker dateTimePicker2;
    private javax.swing.JLabel endBarPlotChart;
    private javax.swing.JComboBox<String> jComboBoxPrecision;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private Data.Stats.HeatMap map;
    private javax.swing.JLabel pieChart;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel startBarPlotChart;
    private javax.swing.JLabel startBarPlotChart1;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables

}