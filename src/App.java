import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;

public class App {
    JTextField jtf_amount, jtf_min_amount, jtf_name, jtf_brand, jtf_type, jtf_size, jtf_color, jtf_price;
    JTextArea jta_desc;
    JButton jb_add, jb_delete, jb_update, jb_search, jb_check;
    JTable jt;
    JFrame frame;
    ArrayList<Product> productlist;
    Product product;
    String header[] = new String[]{
            "id", "Nome", "Marca", "Quantidade", "Quantidade Mínima", "Tipo", "Tamanho", "Cor", "Descrição", "Preço"
    };
    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private void checkMinimumQuantity() {
        ArrayList<Product> lowStockProducts = new ArrayList<>();
        for (Product product : productlist) {
            if (product.getAmount() < product.getMinAmount()) {
                lowStockProducts.add(product);
            }
        }
    
        if (!lowStockProducts.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Os seguintes produtos estão com quantidade abaixo do mínimo:\n\n");
            for (Product product : lowStockProducts) {
                message.append("ID: ").append(product.getId()).append("\n");
                message.append("Nome: ").append(product.getName()).append("\n");
                message.append("Quantidade Atual: ").append(product.getAmount()).append("\n");
                message.append("Quantidade Mínima: ").append(product.getMinAmount()).append("\n\n");
            }
            JOptionPane.showMessageDialog(frame, message.toString(), "Produtos com Quantidade Baixa", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Todos os produtos estão com quantidade acima do mínimo.", "Estoque OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static Connection conn;
    ResultSet rs;
    int row, col;

    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:data.db";
        conn = DriverManager.getConnection(url);
        App app = new App();
        app.mainInterface();
        app.checkTables();
        app.loadData();
        app.checkMinimumQuantity();
        
    }

    private void mainInterface() {
        frame = new JFrame("Gerenciamento de Estoque ");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Nome"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_name = new JTextField(20);
        inputPanel.add(jtf_name, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Quantidade Atual"), gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_amount = new JTextField(5);
        inputPanel.add(jtf_amount, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Quantidade Mínima"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_min_amount = new JTextField(5);
        inputPanel.add(jtf_min_amount, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Preço"), gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_price = new JTextField(10);
        inputPanel.add(jtf_price, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Marca"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_brand = new JTextField(20);
        inputPanel.add(jtf_brand, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Tipo"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_type = new JTextField(20);
        inputPanel.add(jtf_type, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Tamanho"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_size = new JTextField(20);
        inputPanel.add(jtf_size, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Cor"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jtf_color = new JTextField(20);
        inputPanel.add(jtf_color, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Descrição"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 3;
        jta_desc = new JTextArea(3, 20);
        jta_desc.setBorder(new JTextField().getBorder());
        inputPanel.add(new JScrollPane(jta_desc), gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        jb_add = new JButton("Adicionar");
        jb_add.addActionListener(addProductListener);
        buttonPanel.add(jb_add);

        jb_delete = new JButton("Deletar");
        jb_delete.addActionListener(delProductListener);
        buttonPanel.add(jb_delete);

        jb_update = new JButton("Atualizar");
        jb_update.addActionListener(updateProductListener);
        buttonPanel.add(jb_update);

        jb_search = new JButton("Buscar");
        jb_search.addActionListener(searchProductListener);
        buttonPanel.add(jb_search);

        jb_check = new JButton("Verificar Estoque");
        jb_check.addActionListener(checkQuantityListener);
        buttonPanel.add(jb_check);


        jt = new JTable();
        jt.setModel(dtm);
        dtm.setColumnIdentifiers(header);
        JScrollPane sp = new JScrollPane(jt);
        jt.addMouseListener(mouseListener);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(sp, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane);

        frame.setTitle("Gerenciamento de Estoque");
        frame.setSize(840, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    ActionListener addProductListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = jtf_name.getText().trim();
            String price = jtf_price.getText().trim();
            String desc = jta_desc.getText().trim();
            String brand = jtf_brand.getText().trim();
            String type = jtf_type.getText().trim();
            String size = jtf_size.getText().trim();
            String color = jtf_color.getText().trim();
            String amount = jtf_amount.getText().trim();
            String min_amount = jtf_min_amount.getText().trim();

            if (name.isEmpty() || price.isEmpty() || desc.isEmpty() || brand.isEmpty() || type.isEmpty() || size.isEmpty() || color.isEmpty() || amount.isEmpty() || min_amount.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, preencha todas as informações");
                return;
            }

            int amountValue;
            int minAmountValue;
            try {
                amountValue = Integer.parseInt(amount);
                minAmountValue = Integer.parseInt(min_amount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Quantidade e Quantidade Mínima devem ser números inteiros válidos");
                return;
            }

            double priceValue;
            try {
                priceValue = Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Preço deve ser um número real válido");
                return;
            }

            int result = JOptionPane.showConfirmDialog(frame, "Inserir os seguintes dados:\n" +
                    "Nome: " + name + "\n" +
                    "Preço: " + price + "\n" +
                    "Descrição: " + desc + "\n" +
                    "Marca: " + brand + "\n" +
                    "Tipo: " + type + "\n" +
                    "Tamanho: " + size + "\n" +
                    "Cor: " + color + "\n" +
                    "Quantidade: " + amount + "\n" +
                    "Quantidade Mínima: " + min_amount + "\n", "Inserir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = conn.createStatement();
                    String sql = "INSERT INTO tbl_products (product_name, product_price, product_desc, product_brand, product_type, product_size, product_color, product_amount, product_min_amount) VALUES ('" +
                            name + "', '" +
                            price + "', '" +
                            desc + "', '" +
                            brand + "', '" +
                            type + "', '" +
                            size + "', '" +
                            color + "', '" +
                            amount + "', '" +
                            min_amount + "')";
                    stmt.executeUpdate(sql);
                    loadData();
                    JOptionPane.showMessageDialog(frame, "Produto adicionado com sucesso!");
                } catch (SQLException ex) {
                    System.out.println("Erro ao adicionar produto: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Erro ao adicionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    };

    private void checkTables() {
        String sql = "CREATE TABLE IF NOT EXISTS tbl_products (" +
                "	id integer PRIMARY KEY AUTOINCREMENT," +
                "	product_amount integer NOT NULL," +
                "	product_min_amount integer NOT NULL," +
                "	product_name text NOT NULL," +
                "	product_brand text NOT NULL," +
                "	product_type text NOT NULL," +
                "	product_size text NOT NULL," +
                "	product_color text NOT NULL," +
                "	product_desc text NOT NULL," +
                "	product_price real NOT NULL" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar a tabela: " + e.getMessage());
        }
    }

    private void loadData() {
        productlist = new ArrayList<>();
        dtm.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM tbl_products");
            productlist.clear();
            while (rs.next()) {
                productlist.add(new Product(
                        rs.getInt("id"),
                        rs.getInt("product_amount"),
                        rs.getInt("product_min_amount"),
                        rs.getString("product_name"),
                        rs.getString("product_brand"),
                        rs.getString("product_type"),
                        rs.getString("product_size"),
                        rs.getString("product_color"),
                        rs.getString("product_desc"),
                        rs.getDouble("product_price")
                ));
            }

            for (Product product : productlist) {
                Object[] objs = {product.id, product.name, product.brand, product.amount, product.minAmount, product.type, product.size, product.color, product.description, product.price};
                dtm.addRow(objs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    ActionListener updateProductListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (product == null) {
                JOptionPane.showMessageDialog(frame, "Nenhum produto selecionado para atualizar.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(frame, "Atualizar " + product.name + "?", "Atualizar Produto", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = conn.createStatement();
                    String sql = "UPDATE tbl_products SET product_name = '" + jtf_name.getText().trim() + "', " +
                            "product_price = '" + jtf_price.getText().trim() + "', " +
                            "product_desc = '" + jta_desc.getText().trim() + "', " +
                            "product_brand = '" + jtf_brand.getText().trim() + "', " +
                            "product_type = '" + jtf_type.getText().trim() + "', " +
                            "product_size = '" + jtf_size.getText().trim() + "', " +
                            "product_color = '" + jtf_color.getText().trim() + "', " +
                            "product_amount = '" + jtf_amount.getText().trim() + "', " +
                            "product_min_amount = '" + jtf_min_amount.getText().trim() + "' " +
                            "WHERE id = " + product.id;
                    stmt.executeUpdate(sql);
                    loadData();
                    JOptionPane.showMessageDialog(frame, "Produto atualizado com sucesso!");
                } catch (SQLException ex) {
                    System.out.println("Erro ao atualizar produto: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Erro ao atualizar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    };

    ActionListener delProductListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (product == null) {
                JOptionPane.showMessageDialog(frame, "Nenhum produto selecionado para deletar.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(frame, "Deletar " + product.name + "?", "Deletar Produto", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM tbl_products WHERE id = " + product.id);
                    loadData();
                } catch (SQLException ex) {
                    System.out.println("Erro ao deletar produto: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Erro ao deletar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    };

    ActionListener searchProductListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String search = JOptionPane.showInputDialog("Digite o nome ou detalhes do produto a ser buscado");

            productlist = new ArrayList<>();
            try {
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM tbl_products WHERE ";
                String[] columns = {"product_amount", "product_min_amount", "product_name", "product_brand", "product_type", "product_size", "product_color", "product_desc", "product_price"};
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) query += " OR ";
                    query += columns[i] + " LIKE '%" + search + "%'";
                }
                rs = stmt.executeQuery(query);
                productlist.clear();
                while (rs.next()) {
                    productlist.add(new Product(rs.getInt("id"), rs.getInt("product_amount"), rs.getInt("product_min_amount"), rs.getString("product_name"), rs.getString("product_brand"), rs.getString("product_type"), rs.getString("product_size"), rs.getString("product_color"), rs.getString("product_desc"), rs.getDouble("product_price")));
                }
                dtm.setRowCount(0);
                for (Product product : productlist) {
                    Object[] objs = {product.id, product.name, product.brand, product.amount, product.minAmount, product.type, product.size, product.color, product.description, product.price};
                    dtm.addRow(objs);
                }
            } catch (SQLException ex) {
                System.out.println("Erro ao buscar produtos: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame, "Erro ao buscar produtos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    ActionListener checkQuantityListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkMinimumQuantity();
        }
    };
    
    MouseInputAdapter mouseListener = new MouseInputAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            row = jt.rowAtPoint(evt.getPoint());
            col = jt.columnAtPoint(evt.getPoint());

            if (row >= 0 && col >= 0) {
                product = productlist.get(row);
                jtf_name.setText(product.name);
                jtf_price.setText(String.valueOf(product.price));
                jta_desc.setText(product.description);
                jtf_brand.setText(product.brand);
                jtf_type.setText(product.type);
                jtf_size.setText(product.size);
                jtf_color.setText(product.color);
                jtf_amount.setText(String.valueOf(product.amount));
                jtf_min_amount.setText(String.valueOf(product.minAmount));
            }
        }
    };
}

