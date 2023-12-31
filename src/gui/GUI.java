package gui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class GUI extends JFrame {
    JLabel errorLabel;
    DBConnection connection = new DBConnection();
    public GUI(){
        JPanel topPanel = new JPanel();
        JPanel middlePanel = new JPanel();
        JPanel verticalTopPanel = new JPanel();

        JComboBox<String> comboBox = new JComboBox<>();
        JComboBox<String> queriesList = new JComboBox<>();
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);

        JButton button = new JButton("Execute query");
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setName("Query selector");
        setSize(1920, 1080);
        setVisible(true);

        verticalTopPanel.setLayout(new BoxLayout(verticalTopPanel, BoxLayout.Y_AXIS));
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

        setFont(comboBox);
        setFont(button);
        setFont(queriesList);
        setFont(errorLabel);
        setFont(textArea);
        setFont(this);
        setFont(table);
        setFont(table.getTableHeader());

        comboBox.setMinimumSize(new Dimension(500,1));
        queriesList.setMinimumSize(new Dimension(500,1));

        loadGroups(comboBox);
        loadQueries(queriesList, comboBox);

        topPanel.add(verticalTopPanel);
        topPanel.add(button);

        verticalTopPanel.add(comboBox);
        verticalTopPanel.add(queriesList);

        middlePanel.add(scrollPane);
        middlePanel.add(errorLabel);

        table.setEnabled(false);
        table.getTableHeader().setEnabled(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorLabel.setText("");
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

    }

    public void setFont(Component component ){

        if (component.getFont() != null) {
            Font currentFont = component.getFont();
            Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), 20);
            component.setFont(newFont);
        }
    }

    public void loadGroups(JComboBox<String> comboBox){
        ResultSet resultSet;

        try {
            resultSet = connection.executeQuerry("SELECT * FROM dbo.\"Group\"");

            while (resultSet.next()){
                comboBox.addItem((String) resultSet.getObject(2));
            }
        } catch (SQLException e){
            errorLabel.setText(e.getMessage());
        }
    }
    public void loadQueries(JComboBox<String> queriesList, JComboBox<String> comboBox){
        ResultSet resultSet;

        try {
            resultSet = connection.executeQuerry("SELECT name FROM dbo.Query q WHERE q.id IN (SELECT Query_id FROM dbo.Query_Group WHERE Group_id = " + (comboBox.getSelectedIndex() + 1) + ")");

            while (resultSet.next()){
                queriesList.addItem((String) resultSet.getObject(1));
            }
        } catch (SQLException e){
            errorLabel.setText(e.getMessage());
        }
    }

    public String getQueryText(int id){
        ResultSet resultSet;

        try {
            resultSet = connection.executeQuerry("SELECT text FROM dbo.Query q WHERE q.id = "+id);

            while (resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (SQLException e){
            errorLabel.setText(e.getMessage());
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
            errorLabel.setText(e.getMessage());
        }
    }

}