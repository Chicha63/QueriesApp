package gui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
        JButton button = new JButton("Execute query");
        loadGroups(comboBox);
        loadQueries(queriesList, comboBox);
        topPanel.add(comboBox);
        topPanel.add(queriesList);
        topPanel.add(button);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);


        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchData(tableModel,textArea.getText());
            }
        });

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queriesList.removeAllItems();
                loadQueries(queriesList,comboBox);
                queriesList.showPopup();
            }
        });
        queriesList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(getQueryText(queriesList.getSelectedIndex()+1));
            }
        });
        setSize(1920, 1080);
        setVisible(true);
    }

    public void loadGroups(JComboBox<String> comboBox){
        ResultSet resultSet;
        try {
            resultSet = connection.executeQuerry("SELECT * FROM dbo.\"Group\"");
            while (resultSet.next()){
                comboBox.addItem((String) resultSet.getObject(2));
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
    public void loadQueries(JComboBox<String> queriesList, JComboBox<String> comboBox){
        ResultSet resultSet;
        try {
            resultSet = connection.executeQuerry("SELECT name FROM dbo.Query q WHERE q.id IN (SELECT Query_id FROM dbo.Query_Group WHERE Group_id = " + (comboBox.getSelectedIndex() + 1) + ")");
            while (resultSet.next()){
                queriesList.addItem((String) resultSet.getObject(1));
            }
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public String getQueryText(int id){
        ResultSet resultSet;
        try {
            resultSet = connection.executeQuerry("SELECT text FROM dbo.Query q WHERE q.id = "+id);
            while (resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return "";
    }

    private void fetchData(DefaultTableModel tableModel, String request){
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        try{
            ResultSet resultSet = connection.executeQuerry(request);
            if (resultSet != null){
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++){
                    tableModel.addColumn(rsmd.getColumnName(i));
                }

                while (resultSet.next()){
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++){
                        row[i] = resultSet.getObject(i+1);
                    }
                    tableModel.addRow(row);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}