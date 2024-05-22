import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    JTextField jtf_amount, jtf_name, jtf_brand, jtf_type, jtf_size,  jtf_color,  jtf_price;
    JTextArea jta_desc;
    JButton jb_add, jb_delete, jb_update, jb_search;
    JTable jt;
    JFrame frame;
    JLabel lbl_name, lbl_price, lbl_desc, lbl_brand, lbl_type, lbl_size, lbl_color , lbl_amount;
    ArrayList<Product> productlist;
    Product product;
    String header[] = new String[] {
            "id",
            "Nome",
            "Marca",
            "Quantidade",
            "Tipo",
            "Tamanho",
            "Cor",
            "Descrição",
            "Preço",

    };
    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

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
    }


    private void mainInterface() {
        // Componentes da interface gráfica
        frame = new JFrame();
        lbl_name = new JLabel();
        lbl_name.setText("Nome");
        lbl_name.setBounds(10, 10, 100, 50);
        frame.add(lbl_name);

        jtf_name = new JTextField();
        jtf_name.setBounds(100, 25, 250, 25);
        frame.add(jtf_name);

        lbl_amount = new JLabel();
        lbl_amount.setText("Quantidade");
        lbl_amount.setBounds(500, 45, 100, 50);
        frame.add(lbl_amount);

        jtf_amount = new JTextField();
        jtf_amount.setBounds(580, 60, 100, 25);
        frame.add(jtf_amount);

        lbl_price = new JLabel();
        lbl_price.setText("Preço");
        lbl_price.setBounds(10, 45, 100, 50);
        frame.add(lbl_price);

        jtf_price = new JTextField();
        jtf_price.setBounds(100, 60, 100, 25);
        frame.add(jtf_price);

        lbl_brand = new JLabel();
        lbl_brand.setText("Marca");
        lbl_brand.setBounds(10, 80, 100, 50);
        frame.add(lbl_brand);

        jtf_brand = new JTextField();
        jtf_brand.setBounds(100, 95, 250, 25);
        frame.add(jtf_brand);

        lbl_type = new JLabel();
        lbl_type.setText("Tipo");
        lbl_type.setBounds(10, 115, 100, 50);
        frame.add(lbl_type);

        jtf_type = new JTextField();
        jtf_type.setBounds(100, 130, 250, 25);
        frame.add(jtf_type);

        lbl_size = new JLabel();
        lbl_size.setText("Tamanho");
        lbl_size.setBounds(10, 150, 100, 50);
        frame.add(lbl_size);

        jtf_size = new JTextField();
        jtf_size.setBounds(100, 165, 250, 25);
        frame.add(jtf_size);

        lbl_color = new JLabel();
        lbl_color.setText("Cor");
        lbl_color.setBounds(10, 185, 100, 50);
        frame.add(lbl_color);

        jtf_color = new JTextField();
        jtf_color.setBounds(100, 200, 250, 25);
        frame.add(jtf_color);

        lbl_desc = new JLabel();
        lbl_desc.setText("Descrição");
        lbl_desc.setBounds(10, 220, 100, 50);
        frame.add(lbl_desc);

        jta_desc = new JTextArea();
        jta_desc.setBounds(100, 235, 250, 50);
        jta_desc.setBorder(new JTextField().getBorder());
        frame.add(jta_desc);

        jb_add = new JButton();
        jb_add.setText("Adicionar");
        jb_add.setBounds(10, 300, 100, 25);
        frame.add(jb_add);
        jb_add.addActionListener(addFoodListener);

        jb_delete = new JButton();
        jb_delete.setText("Deletar");
        jb_delete.setBounds(120, 300, 100, 25);
        frame.add(jb_delete);
        jb_delete.addActionListener(delFoodListener);

        jb_update = new JButton();
        jb_update.setText("Atualizar");
        jb_update.setBounds(230, 300, 100, 25);
        frame.add(jb_update);
        jb_update.addActionListener(updateFoodListener);

        jb_search = new JButton();
        jb_search.setText("Buscar");
        jb_search.setBounds(340, 300, 100, 25);
        frame.add(jb_search);
        jb_search.addActionListener(searchFoodListener);

        jt = new JTable();
        jt.setModel(dtm);
        dtm.setColumnIdentifiers(header);
        JScrollPane sp = new JScrollPane(jt);
        sp.setBounds(10, 330, 800, 300);
        frame.add(sp);
        jt.addMouseListener(mouseListener);

        frame.setSize(840, 680);
        frame.setLayout(null); // Não está usando gerenciadores de layout
        frame.setVisible(true);

    }

    ActionListener addFoodListener = new ActionListener() {
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
        
            // Verifica se algum campo obrigatório está vazio
            if (name.isEmpty() || price.isEmpty() || desc.isEmpty() || brand.isEmpty() || type.isEmpty() || size.isEmpty() || color.isEmpty() || amount.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, preencha todas as informações");
                return;
            }
        
            // Converte a quantidade para um número inteiro
            int amountValue;
            try {
                amountValue = Integer.parseInt(amount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Quantidade deve ser um número inteiro válido");
                return;
            }
        
            // Converte o preço para um número real
            double priceValue;
            try {
                priceValue = Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Preço deve ser um número real válido");
                return;
            }
        
            // Confirmação da inserção
            int result = JOptionPane.showConfirmDialog(frame, "Inserir os seguintes dados:\n" +
                    "Nome: " + name + "\n" +
                    "Preço: " + price + "\n" +
                    "Descrição: " + desc + "\n" +
                    "Marca: " + brand + "\n" +
                    "Tipo: " + type + "\n" +
                    "Tamanho: " + size + "\n" +
                    "Cor: " + color + "\n" +
                    "Quantidade: " + amount + "\n", "Inserir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // Insere os dados no banco de dados
                    Statement stmt = conn.createStatement();
                    String sql = "INSERT INTO tbl_products (product_name, product_price, product_desc, product_brand, product_type, product_size, product_color, product_amount) " +
                                 "VALUES ('" + name + "', '" + priceValue + "', '" + desc + "', '" + brand + "', '" + type + "', '" + size + "', '" + color + "', '" + amountValue + "')";
                    stmt.executeUpdate(sql);
                    loadData();
                    JOptionPane.showMessageDialog(frame, "Produto inserido com sucesso!");
                } catch (SQLException ex) {
                    System.out.println("Erro ao inserir produto: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Erro ao inserir produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    };

    private void checkTables() { // Cria e verifica a tabela
        System.out.println("Check table");
        String sql = "CREATE TABLE IF NOT EXISTS tbl_products (" +
                "	id integer PRIMARY KEY AUTOINCREMENT," +
                "	product_amount integer NOT NULL," +
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
            stmt.executeUpdate(sql);
        } catch (Exception err) {
            System.out.println(err);
        }
    }

    private void loadData() throws SQLException {
        System.out.println("Load data");
        productlist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from tbl_products");
        productlist.clear();
        while (rs.next()) {
            productlist.add(new Product(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getDouble(9)));
        }
        dtm.setRowCount(0); // reset data model
        for (int i = 0; i < productlist.size(); i++) {
            Object[] objs = {
                    productlist.get(i).id,
                    productlist.get(i).name,
                    productlist.get(i).brand,
                    productlist.get(i).amount,
                    productlist.get(i).type,
                    productlist.get(i).size,
                    productlist.get(i).color,
                    productlist.get(i).description,
                    productlist.get(i).price
            };
            dtm.addRow(objs);
        }
    }

MouseInputAdapter mouseListener = new MouseInputAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = jt.rowAtPoint(evt.getPoint());
        int col = jt.columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
            int id = Integer.parseInt(jt.getValueAt(row, 0).toString());
            String name = jt.getValueAt(row, 1).toString();
            String brand = jt.getValueAt(row, 2).toString();
            String amount = jt.getValueAt(row, 3).toString();
            String type = jt.getValueAt(row, 4).toString();
            String size = jt.getValueAt(row, 5).toString();
            String color = jt.getValueAt(row, 6).toString();
            String desc = jt.getValueAt(row, 7).toString();
            double price = Double.parseDouble(jt.getValueAt(row, 8).toString());

            jtf_name.setText(name);
            jtf_brand.setText(brand);
            jtf_amount.setText(amount);
            jtf_type.setText(type);
            jtf_size.setText(size);
            jtf_color.setText(color);
            jta_desc.setText(desc);
            jtf_price.setText(String.valueOf(price));

            product = new Product(id, Integer.parseInt(amount), name, brand, type, size, color, desc, price);
        }
    }
};


    ActionListener updateFoodListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = jtf_name.getText().trim();
            String brand = jtf_brand.getText().trim();
            String type = jtf_type.getText().trim();
            String size = jtf_size.getText().trim();
            String color = jtf_color.getText().trim();
            String amount = jtf_amount.getText().trim();
            String price = jtf_price.getText().trim();
            String desc = jta_desc.getText().trim();
        
            if (product == null) {
                JOptionPane.showMessageDialog(frame, "Nenhum produto selecionado para atualizar.");
                return;
            }
        
            int result = JOptionPane.showConfirmDialog(frame, "Atualizar " + product.name + "?", "Atualizar Produto",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // Atualiza os dados do produto no banco de dados
                    Statement stmt = conn.createStatement();
                    String sql = "UPDATE tbl_products SET product_name = '" + name + "', product_brand = '" + brand +
                                 "', product_type = '" + type + "', product_size = '" + size + "', product_color = '" + color +
                                 "', product_amount = " + amount + ", product_price = " + price + ", product_desc = '" + desc +
                                 "' WHERE id = " + product.id;
                    stmt.executeUpdate(sql);
                    loadData(); // Recarrega os dados na tabela
                    JOptionPane.showMessageDialog(frame, "Produto atualizado com sucesso!");
                } catch (SQLException ex) {
                    System.out.println("Erro ao atualizar produto: " + ex.getMessage());
                    JOptionPane.showMessageDialog(frame, "Erro ao atualizar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    };

    ActionListener delFoodListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (product == null) {
                System.out.println("Null");
            } else {
                int result = JOptionPane.showConfirmDialog(frame, "Delete " + product.name + "?", "Swing Tester",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        System.out.println("Product " + product.name);
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("DELETE FROM tbl_products WHERE id = " + product.id);
                        loadData();
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }
            }
        }
    };
    

    ActionListener searchFoodListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String search = JOptionPane.showInputDialog("Enter product name or details to search");
            System.out.println(search);
            
            productlist = new ArrayList<>();
            try {
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM tbl_products WHERE ";
                String[] columns = {
                    "product_amount",
                    "product_name",
                    "product_brand",
                    "product_type",
                    "product_size",
                    "product_color",
                    "product_desc",
                    "product_price"
                };
                // Construindo a cláusula WHERE para pesquisar em todas as colunas, exceto product_id
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) {
                        query += " OR ";
                    }
                    query += columns[i] + " LIKE '%" + search + "%'";
                }
                rs = stmt.executeQuery(query);
                productlist.clear();
                while (rs.next()) {
                    productlist.add(new Product(
                        rs.getInt("id"),             // id
                        rs.getInt("product_amount"), // amount
                        rs.getString("product_name"),// name
                        rs.getString("product_brand"),// brand
                        rs.getString("product_type"), // type
                        rs.getString("product_size"), // size
                        rs.getString("product_color"),// color
                        rs.getString("product_desc"), // description
                        rs.getDouble("product_price") // price
                    ));                    
                }
                dtm.setRowCount(0); // reset data model
                for (Product product : productlist) {
                    Object[] objs = {
                        product.id,
                        product.name,
                        product.brand,
                        product.amount,
                        product.type,
                        product.size,
                        product.color,
                        product.description,
                        product.price
                    };
                    dtm.addRow(objs);
                }
            } catch (Exception err) {
                System.out.println(err);
            }
        }
    };
};
