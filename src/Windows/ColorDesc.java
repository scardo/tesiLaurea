/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Windows;

import Data.Trajectory;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author alessandropandolfo
 */
public class ColorDesc extends JFrame {

    private int ColType;

    public ColorDesc(int ColType) {
        this.ColType = ColType;
        initComponents();
    }

    private void initComponents() {
        switch (this.ColType) {
            case 0:
                initHourComp();
                break;
            case 1:
                initWeekComp();
                break;
            case 2:
                initDayComp();
                break;
            case 3:
                initMonthComp();
                break;
            case 4:
                initSelComp();
                break;
        }
        pack();
    }

    private void initHourComp() {
        JPanel[] hours = new JPanel[24];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = new JPanel();
            JLabel text = new JLabel("" + (i + 1));
            text.setForeground(Color.WHITE);
            hours[i].add(text);
            hours[i].setBackground(Trajectory.selectHourColor(i));
        }
        initLayout(hours);
    }

    private void initWeekComp() {
        JPanel[] days = new JPanel[7];
        String[] daysOfWeek = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        for (int i = 0; i < days.length; i++) {
            days[i] = new JPanel();
            JLabel text = new JLabel(daysOfWeek[i]);
            text.setForeground(Color.WHITE);
            days[i].add(text);
            days[i].setBackground(Trajectory.selectDayOfWeekColor(i + 1));
        }
        initLayout(days);
    }

    private void initDayComp() {
        JPanel[] days = new JPanel[31];
        for (int i = 0; i < days.length; i++) {
            days[i] = new JPanel();
            JLabel text = new JLabel("" + (i + 1));
            text.setForeground(Color.WHITE);
            days[i].add(text);
            days[i].setBackground(Trajectory.selectDayOfMonthColor(i + 1));
        }
        initLayout(days);
    }

    private void initMonthComp() {
        JPanel[] months = new JPanel[12];
        String[] monthsName = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        for (int i = 0; i < months.length; i++) {
            months[i] = new JPanel();
            JLabel text = new JLabel(monthsName[i]);
            text.setForeground(Color.WHITE);
            months[i].add(text);
            months[i].setBackground(Trajectory.selectMonthColor(i + 1));
        }
        initLayout(months);
    }

    private void initLayout(JPanel[] panels) {
        if (ColType != 4) {
            GridLayout layout = new GridLayout(4, panels.length / 4);
            getContentPane().setLayout(layout);
            for (JPanel panel : panels) {
                getContentPane().add(panel);
            }
        } else {
            GridLayout layout = new GridLayout(1, 2);
            getContentPane().setLayout(layout);
            for (JPanel panel : panels) {
                getContentPane().add(panel);
            }
        }
    }

    private void initSelComp() {
        JPanel[] panels = new JPanel[2];
        panels[0] = new JPanel();
        JLabel text = new JLabel("Selected");
        text.setForeground(Color.WHITE);
        panels[0].add(text);
        panels[0].setBackground(Color.RED);
        panels[1] = new JPanel();
        JLabel text1 = new JLabel("Others");
        text1.setForeground(Color.WHITE);
        panels[1].add(text1);
        panels[1].setBackground(Color.BLACK);
        initLayout(panels);
    }
}
