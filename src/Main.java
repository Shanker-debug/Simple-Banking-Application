
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class Main extends JFrame {

    private final Bank bank;

    private JLabel balanceLabel;
    private JLabel nameLabel;
    private JLabel accountLabel;
    private DefaultTableModel tableModel;
    private JTable transactionTable;

    private final Color navy = new Color(20, 39, 73);
    private final Color green = new Color(0, 165, 115);
    private final Color background = new Color(246, 249, 253);
    private final Color muted = new Color(100, 116, 139);

    private final NumberFormat money =
        NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private final DateTimeFormatter dateFormat =
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public Main() {
        bank = new Bank();

        setTitle("Simple Banking Application");
        setSize(1100, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (bank.getAccount() == null) {
            createFirstAccount();
        }

        buildGUI();
        refreshDashboard();
    }

    // First-time account setup
    private void createFirstAccount() {
        JTextField nameField = new JTextField(18);
        JTextField balanceField = new JTextField("0", 18);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Account holder name"));
        form.add(nameField);
        form.add(new JLabel("Opening balance (Rs.)"));
        form.add(balanceField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                null, form, "Create Your Bank Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            try {
                BigDecimal initial =
                    new BigDecimal(balanceField.getText().trim());

                bank.createAccount(nameField.getText(), initial);
                break;

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                    "Enter a valid opening balance.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException |
                     IllegalStateException ex) {
                JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Main window layout
    private void buildGUI() {
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(background);
        content.setBorder(BorderFactory.createEmptyBorder(
            25, 25, 25, 25));

        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createDashboard(), BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    // Left navigation sidebar
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(navy);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(
            25, 15, 20, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  MyBank");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("  Safe  •  Simple  •  Secure");
        subtitle.setForeground(new Color(174, 191, 215));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        sidebar.add(navButton("Dashboard", () -> refreshDashboard()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Deposit", () -> deposit()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Withdraw", () -> withdraw()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Transactions", () -> showHistory()));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("  Simple Banking v1.0");
        footer.setForeground(new Color(174, 191, 215));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private JButton navButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(navy);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 5));
        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(31, 62, 102));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(navy);
            }
        });

        return button;
    }

    // Top header
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel greeting = new JPanel();
        greeting.setLayout(new BoxLayout(greeting, BoxLayout.Y_AXIS));
        greeting.setOpaque(false);

        JLabel welcome = new JLabel("Welcome to MyBank");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 25));
        welcome.setForeground(navy);

        JLabel description = new JLabel(
            "Here's your account summary");
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(muted);

        greeting.add(welcome);
        greeting.add(Box.createVerticalStrut(4));
        greeting.add(description);

        JLabel accountType = new JLabel("SAVINGS ACCOUNT");
        accountType.setFont(new Font("SansSerif", Font.BOLD, 12));
        accountType.setForeground(green);

        header.add(greeting, BorderLayout.WEST);
        header.add(accountType, BorderLayout.EAST);
        return header;
    }

    // Main dashboard
    private JPanel createDashboard() {
        JPanel dashboard = new JPanel();
        dashboard.setOpaque(false);
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));

        // Balance card
        RoundedPanel balanceCard = new RoundedPanel(
            new Color(0, 145, 100), 24);
        balanceCard.setLayout(new BoxLayout(
            balanceCard, BoxLayout.Y_AXIS));
        balanceCard.setBorder(BorderFactory.createEmptyBorder(
            23, 28, 23, 28));
        balanceCard.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 175));

        JLabel balanceTitle = new JLabel("ACCOUNT BALANCE");
        balanceTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        balanceTitle.setForeground(new Color(215, 255, 239));

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        balanceLabel.setForeground(Color.WHITE);

        JPanel details = new JPanel(new GridLayout(1, 2, 15, 0));
        details.setOpaque(false);

        JPanel holder = new JPanel();
        holder.setOpaque(false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));

        JLabel holderTitle = new JLabel("ACCOUNT HOLDER");
        holderTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        holderTitle.setForeground(new Color(215, 255, 239));

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE);

        holder.add(holderTitle);
        holder.add(Box.createVerticalStrut(4));
        holder.add(nameLabel);

        JPanel number = new JPanel();
        number.setOpaque(false);
        number.setLayout(new BoxLayout(number, BoxLayout.Y_AXIS));

        JLabel numberTitle = new JLabel("ACCOUNT NUMBER");
        numberTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        numberTitle.setForeground(new Color(215, 255, 239));

        accountLabel = new JLabel();
        accountLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        accountLabel.setForeground(Color.WHITE);

        number.add(numberTitle);
        number.add(Box.createVerticalStrut(4));
        number.add(accountLabel);

        details.add(holder);
        details.add(number);

        balanceCard.add(balanceTitle);
        balanceCard.add(Box.createVerticalStrut(7));
        balanceCard.add(balanceLabel);
        balanceCard.add(Box.createVerticalStrut(16));
        balanceCard.add(details);

        // Deposit and withdrawal cards
        JPanel actions = new JPanel(new GridLayout(1, 2, 15, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 125));

        actions.add(actionCard(
            "Deposit Money",
            "Add money to your account",
            green, () -> deposit()
        ));

        actions.add(actionCard(
            "Withdraw Money",
            "Withdraw from your account",
            new Color(225, 75, 95), () -> withdraw()
        ));

        // Recent transactions
        JPanel recent = new JPanel(new BorderLayout(0, 10));
        recent.setOpaque(false);

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        recentTitle.setForeground(navy);

        String[] columns = {"Date", "Type", "Amount", "Balance"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(32);
        transactionTable.setFont(
            new Font("SansSerif", Font.PLAIN, 12));
        transactionTable.setSelectionBackground(
            new Color(218, 239, 231));
        transactionTable.setShowVerticalLines(false);
        transactionTable.setFillsViewportHeight(true);

        transactionTable.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 12));
        transactionTable.getTableHeader().setBackground(
            new Color(233, 240, 248));
        transactionTable.getTableHeader().setForeground(navy);
        transactionTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(BorderFactory.createLineBorder(
            new Color(224, 232, 241)));
        scroll.getViewport().setBackground(Color.WHITE);

        JButton viewAll = new JButton("View All");
        viewAll.setForeground(green);
        viewAll.setFont(new Font("SansSerif", Font.BOLD, 12));
        viewAll.setFocusPainted(false);
        viewAll.addActionListener(e -> showHistory());

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAll, BorderLayout.EAST);

        recent.add(recentHeader, BorderLayout.NORTH);
        recent.add(scroll, BorderLayout.CENTER);

        dashboard.add(balanceCard);
        dashboard.add(Box.createVerticalStrut(18));
        dashboard.add(actions);
        dashboard.add(Box.createVerticalStrut(22));
        dashboard.add(recent);

        return dashboard;
    }

    // Reusable action card
    private JPanel actionCard(String title, String description,
                              Color accent, Runnable action) {
        RoundedPanel card = new RoundedPanel(Color.WHITE, 20);
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(
            15, 20, 15, 20));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(accent);

        JLabel desc = new JLabel(description);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(muted);

        JButton button = new JButton("Open  →");
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(accent);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(
            textPanel, BoxLayout.Y_AXIS));
        textPanel.add(heading);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(desc);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(button, BorderLayout.EAST);
        return card;
    }

    // Refresh account information and recent transactions
    private void refreshDashboard() {
        SavingsAccount account = bank.getAccount();

        balanceLabel.setText(money.format(account.getBalance()));
        nameLabel.setText(account.getHolderName());
        accountLabel.setText(account.getAccountNumber());

        tableModel.setRowCount(0);

        List<BankAccount.Transaction> transactions =
            account.getTransactions();

        int start = Math.max(0, transactions.size() - 5);

        for (int i = transactions.size() - 1; i >= start; i--) {
            BankAccount.Transaction t = transactions.get(i);

            tableModel.addRow(new Object[]{
                t.getDateTime().format(dateFormat),
                t.getType(),
                money.format(t.getAmount()),
                money.format(t.getBalanceAfter())
            });
        }
    }

    // Deposit dialog
    private void deposit() {
        String input = JOptionPane.showInputDialog(
            this, "Enter the amount to deposit (Rs.):",
            "Deposit Money", JOptionPane.PLAIN_MESSAGE);

        if (input == null) return;

        try {
            BigDecimal amount = new BigDecimal(input.trim());
            bank.getAccount().deposit(amount);
            bank.saveData();
            refreshDashboard();

            JOptionPane.showMessageDialog(this,
                "Deposit successful!\nNew balance: " +
                money.format(bank.getAccount().getBalance()),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    // Withdrawal dialog
    private void withdraw() {
        String input = JOptionPane.showInputDialog(
            this, "Enter the amount to withdraw (Rs.):",
            "Withdraw Money", JOptionPane.PLAIN_MESSAGE);

        if (input == null) return;

        try {
            BigDecimal amount = new BigDecimal(input.trim());
            bank.getAccount().withdraw(amount);
            bank.saveData();
            refreshDashboard();

            JOptionPane.showMessageDialog(this,
                "Withdrawal successful!\nNew balance: " +
                money.format(bank.getAccount().getBalance()),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    // Complete transaction history
    private void showHistory() {
        List<BankAccount.Transaction> transactions =
            bank.getAccount().getTransactions();

        String[] columns = {"Date", "Type", "Amount", "Balance"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = transactions.size() - 1; i >= 0; i--) {
            BankAccount.Transaction t = transactions.get(i);

            model.addRow(new Object[]{
                t.getDateTime().format(dateFormat),
                t.getType(),
                money.format(t.getAmount()),
                money.format(t.getBalanceAfter())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(650, 300));

        JOptionPane.showMessageDialog(
            this, scroll, "Transaction History",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this, message, "Banking Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // Custom rounded background panel
    private static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        public RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.fillRoundRect(
                0, 0, getWidth(), getHeight(),
                radius, radius
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use default appearance.
            }

            try {
                Main app = new Main();
                app.setVisible(true);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(
                    null, e.getMessage(),
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}