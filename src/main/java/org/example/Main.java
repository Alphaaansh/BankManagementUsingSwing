package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.Timer;

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
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(34, 49, 63));
        panel.setLayout(null);

        JLabel title = new JLabel("Welcome to MyBank");
        title.setBounds(90, 20, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 80, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        JTextField userTextField = new JTextField();
        userTextField.setBounds(150, 80, 200, 30);
        userTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        userTextField.setBackground(new Color(236, 240, 241));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 130, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setBounds(150, 130, 200, 30);
        passwordTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        passwordTextField.setBackground(new Color(236, 240, 241));

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 190, 200, 40);
        styleButton(loginButton, new Color(46, 204, 113));

        JLabel msg = new JLabel();
        msg.setBounds(50, 240, 350, 25);
        msg.setForeground(Color.RED);

        loginButton.addActionListener(e -> {
            String name = userTextField.getText();
            String pass = new String(passwordTextField.getPassword());

            try {
                var pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                pstmt.setString(1, name);
                pstmt.setString(2, pass);
                var rs = pstmt.executeQuery();

                if (rs.next()) {
                    frame.dispose();
                    dashboard(name);
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
        });

        panel.add(title);
        panel.add(usernameLabel);
        panel.add(userTextField);
        panel.add(passwordLabel);
        panel.add(passwordTextField);
        panel.add(loginButton);
        panel.add(msg);

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void dashboard(String username) {
        JFrame dashFrame = new JFrame();
        dashFrame.setSize(600, 550);
        dashFrame.setLocationRelativeTo(null);
        dashFrame.setUndecorated(true); // Allows fade-in
        dashFrame.setOpacity(0f); // Start transparent
        dashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 73, 94));
        panel.setLayout(null);

        // Custom close button
        JButton closeButton = new JButton("X");
        closeButton.setBounds(560, 10, 30, 30);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(192, 57, 43));
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> dashFrame.dispose());
        panel.add(closeButton);

        JLabel title = new JLabel("Dashboard - " + username);
        title.setBounds(180, 20, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        panel.add(title);

        // Cards
        String[] actions = {"Deposit 💰", "Withdraw 💳", "Check Balance 💹", "Log Out 🔒"};
        Color[] colors = {new Color(52, 152, 219), new Color(52, 152, 219),
                new Color(46, 204, 113), new Color(149, 165, 166)};

        JPanel[] cards = new JPanel[4];
        int y = 100;
        for (int i = 0; i < 4; i++) {
            final int index = i;
            JPanel card = new RoundedPanel(colors[i], 20);
            card.setLayout(null);
            card.setBounds(-300, y, 170, 90);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel(actions[i]);
            label.setBounds(20, 20, 150, 40);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            card.add(label);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    card.setBackground(card.getBackground().brighter());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    card.setBackground(colors[index]);
                }
                public void mouseClicked(MouseEvent e) {
                    switch (index) {
                        case 0: deposit(); break;
                        case 1: withdraw(); break;
                        case 2: JOptionPane.showMessageDialog(dashFrame, "Balance: $" + balance); break;
                        case 3: dashFrame.dispose(); main(null); break;
                    }
                }
            });

            panel.add(card);
            cards[i] = card;
            y += 120;
        }

        dashFrame.add(panel);
        dashFrame.setVisible(true);

        // Fade-in animation
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            float opacity = dashFrame.getOpacity();
            opacity += 0.05f;
            if (opacity >= 1f) {
                dashFrame.setOpacity(1f);
                fadeTimer.stop();
            } else dashFrame.setOpacity(opacity);
        });
        fadeTimer.start();

        // Slide-in animation for cards
        Timer slideTimer = new Timer(10, null);
        slideTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean done = true;
                for (JPanel c : cards) {
                    int targetX = 50;
                    if (c.getX() < targetX) {
                        c.setLocation(c.getX() + 10, c.getY());
                        done = false;
                    }
                }
                if (done) ((Timer)e.getSource()).stop();
            }
        });
        slideTimer.start();
    }

    public static void deposit() {
        JFrame depositFrame = new JFrame("Deposit Money");
        depositFrame.setSize(400, 300);
        depositFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(52, 152, 219));

        JLabel label = new JLabel("Enter amount to deposit:");
        label.setBounds(30, 30, 300, 25);
        label.setForeground(Color.WHITE);

        JTextField amountField = new JTextField();
        amountField.setBounds(30, 70, 250, 35);
        amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        amountField.setBackground(new Color(236, 240, 241));

        JButton submitButton = new JButton("Deposit");
        submitButton.setBounds(30, 120, 250, 40);
        styleButton(submitButton, new Color(46, 204, 113));

        submitButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                balance += amount;
                JOptionPane.showMessageDialog(depositFrame, "$" + amount + " deposited successfully.");
                depositFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(depositFrame, "Please enter a valid number");
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(submitButton);
        depositFrame.add(panel);
        depositFrame.setVisible(true);
    }

    public static void withdraw() {
        JFrame withdrawFrame = new JFrame("Withdraw Money");
        withdrawFrame.setSize(400, 300);
        withdrawFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(52, 152, 219)); // Same as Deposit

        JLabel label = new JLabel("Enter amount to withdraw:");
        label.setBounds(30, 30, 300, 25);
        label.setForeground(Color.WHITE);

        JTextField amountField = new JTextField();
        amountField.setBounds(30, 70, 250, 35);
        amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        amountField.setBackground(new Color(236, 240, 241));

        JButton submitButton = new JButton("Withdraw");
        submitButton.setBounds(30, 120, 250, 40);
        styleButton(submitButton, new Color(46, 204, 113));

        submitButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (balance >= amount) {
                    balance -= amount;
                    JOptionPane.showMessageDialog(withdrawFrame, "$" + amount + " withdrawn successfully.");
                } else {
                    JOptionPane.showMessageDialog(withdrawFrame, "Insufficient balance. Current: $" + balance);
                }
                withdrawFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(withdrawFrame, "Please enter a valid number");
            }
        });

        panel.add(label);
        panel.add(amountField);
        panel.add(submitButton);
        withdrawFrame.add(panel);
        withdrawFrame.setVisible(true);
    }

    private static void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    // Rounded panel class for card-style buttons
    static class RoundedPanel extends JPanel {
        private Color bgColor;
        private int radius;
        public RoundedPanel(Color bgColor, int radius) {
            super();
            this.bgColor = bgColor;
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }
}
