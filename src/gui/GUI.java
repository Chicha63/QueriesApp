package gui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GUI extends JFrame {
    DBConnection connection = new DBConnection();
    public GUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setName("Query selector");
        JPanel topPanel = new JPanel();
        JComboBox<String> comboBox = new JComboBox<>();
        JComboBox<String> queriesList = new JComboBox<>();
        JButton button = new JButton("Button");
        loadRequests(comboBox, queriesList);
        topPanel.add(comboBox);
        topPanel.add(queriesList);
        topPanel.add(button);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        String[] columnNames = {"Column 1", "Column 2"};
        Object[][] data = {{"Data 1", "Data 2"}, {"Data 3", "Data 4"}};
        JTable table = new JTable(data, columnNames);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection.executeQuerry(textArea.getText());
                loadRequests(comboBox, queriesList);
            }
        });

        setSize(1920, 1080);
        setVisible(true);
    }

    public void loadRequests(JComboBox<String> comboBox, JComboBox<String> queriesList){
        ResultSet resultSet;
        try {
            resultSet = connection.executeQuerry("SELECT name FROM dbo.\"Group\"");
            while (resultSet.next()){
                comboBox.addItem((String) resultSet.getObject(1));
            }
            resultSet = connection.executeQuerry("SELECT name FROM dbo.Querry");
            while (resultSet.next()){
                queriesList.addItem((String) resultSet.getObject(1));
            }
            resultSet.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}