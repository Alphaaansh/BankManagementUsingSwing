package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class Main {
    static double balance = 1000.00;
    static Connection conn;

    static void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:A:\\bank.db");
            System.out.println("Database connected.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        connectToDatabase();

        JFrame frame = new JFrame("Login");

        JButton username = new JButton("UserName");
        username.setBounds(30, 30, 100, 30);
        JTextField userTextField = new JTextField();
        userTextField.setBounds(140, 30, 150, 30);

        JButton passText = new JButton("Password");
        passText.setBounds(30, 80, 100, 30);
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setBounds(140, 80, 150, 30);

        JButton login = new JButton("Login");
        login.setBounds(140, 130, 150, 30);

        JLabel msg = new JLabel();
        msg.setBounds(30, 170, 300, 30);
        msg.setForeground(Color.WHITE);

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = userTextField.getText();
                String pass = new String(passwordTextField.getPassword());

                try {
                    var pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                    pstmt.setString(1, name);
                    pstmt.setString(2, pass);
                    var rs = pstmt.executeQuery();

                    if (rs.next()) {
                        frame.dispose();
                        dashboard();
                    } else {
                        msg.setText("Invalid Username or Password");
                    }

                    rs.close();
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    msg.setText("Database error: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Login error: " + ex.getMessage());
                }
            }
        });

        frame.add(username);
        frame.add(userTextField);
        frame.add(passText);
        frame.add(passwordTextField);
        frame.add(msg);
        frame.add(login);
        frame.getContentPane().setBackground(Color.darkGray);

        frame.setSize(350, 300);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void dashboard() {
        JFrame dashFrame = new JFrame("Dashboard");

        JButton depositb = new JButton("Deposit");
        depositb.setBounds(100, 30, 150, 30);

        JButton withDraw = new JButton("Withdraw");
        withDraw.setBounds(100, 80, 150, 30);

        JButton checkBalance = new JButton("Check Balance");
        checkBalance.setBounds(100, 130, 150, 30);

        JButton logout = new JButton("Log Out");
        logout.setBounds(100, 180, 150, 30);

        depositb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });


        withDraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });


        checkBalance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dashFrame, "Balance: $" + balance);
            }
        });


        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashFrame.dispose();
                main(null);
            }
        });


        dashFrame.add(depositb);
        dashFrame.add(withDraw);
        dashFrame.add(checkBalance);
        dashFrame.add(logout);
        dashFrame.getContentPane().setBackground(Color.darkGray);

        dashFrame.setLayout(null);
        dashFrame.setSize(350, 300);
        dashFrame.setVisible(true);
    }

    public static void deposit() {
        JFrame depositFrame = new JFrame("Deposit Money");

        JLabel depositLabel = new JLabel("Enter amount to deposit");
        depositLabel.setBounds(30, 30, 200, 30);
        depositLabel.setForeground(Color.WHITE);

        JTextField depositTextField = new JTextField();
        depositTextField.setBounds(30, 70, 200, 30);

        JButton depositSubmit = new JButton("Submit");
        depositSubmit.setBounds(30, 110, 200, 30);

        depositSubmit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(depositTextField.getText());
                balance += amount;
                JOptionPane.showMessageDialog(depositFrame, "$" + amount + " deposited successfully.");
                depositFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(depositFrame, "Please enter a valid number");
                depositFrame.dispose();
            }
        });

        depositFrame.add(depositLabel);
        depositFrame.add(depositTextField);
        depositFrame.add(depositSubmit);
        depositFrame.getContentPane().setBackground(Color.darkGray);

        depositFrame.setLayout(null);
        depositFrame.setSize(300, 250);
        depositFrame.setVisible(true);
    }

    public static void withdraw() {
        JFrame withdrawFrame = new JFrame("Withdraw Money");

        JLabel withdrawLabel = new JLabel("Enter the amount to withdraw");
        withdrawLabel.setBounds(30, 30, 200, 30);
        withdrawLabel.setForeground(Color.WHITE);

        JTextField withdrawTextField = new JTextField();
        withdrawTextField.setBounds(30, 70, 200, 30);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(30, 110, 100, 30);

        withdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(withdrawTextField.getText());
                if (balance >= amount) {
                    balance -= amount;
                    JOptionPane.showMessageDialog(withdrawFrame, "$" + amount + " withdrawn successfully.");
                } else {
                    JOptionPane.showMessageDialog(withdrawFrame, "Insufficient balance. Current: $" + balance);
                }
                withdrawFrame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(withdrawFrame, "Invalid amount");
                withdrawFrame.dispose();
            }
        });

        withdrawFrame.add(withdrawLabel);
        withdrawFrame.add(withdrawTextField);
        withdrawFrame.add(withdrawButton);
        withdrawFrame.getContentPane().setBackground(Color.darkGray);

        withdrawFrame.setLayout(null);
        withdrawFrame.setSize(300, 250);
        withdrawFrame.setVisible(true);
    }
}