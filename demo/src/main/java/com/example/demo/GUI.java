package com.example.demo;

import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Java Swing to make a GUI. Unfinished.
 */

public class GUI implements ActionListener {

    private int count = 0;
    private JLabel label;
    private JFrame frame;
    private JPanel panel;
    private JTable table;

    public GUI () {
        JFrame frame = new JFrame();

        JButton button = new JButton("Click me");
        button.addActionListener(this);


        ArrayList<Document> recommendedShows = MongoDB.getRelevantShows(MongoDB.topThreeGenres());
        //Make list of Objects to String[][]
        String[][] data = {
            { "value1", "4031", "CSE" },
            { "value2", "6014", "IT" }
        };


        String[] columnNames = {"name1", "name2"};

        table = new JTable(data, columnNames);
        table.setBounds(30, 40, 200, 300);

        JScrollPane sp = new JScrollPane(table);
        frame.add(sp);
        // Frame Size
        frame.setSize(500, 400);
        // Frame Visible = true
        frame.setVisible(true);

        label = new JLabel("Number of clicks :");

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(500,500,500,500));
        panel.setLayout(new GridLayout(0,3));
        panel.add(button);
        panel.add(label);
        panel.add(sp);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Our GUI");
        frame.pack();
        frame.setVisible(true);


    }

    public static void main(String[] args) {
        new GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        label.setText("Clicks: " + count);
    }
}
