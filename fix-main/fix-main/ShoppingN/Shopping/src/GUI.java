    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.util.List;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import javax.swing.event.ListSelectionEvent;
    import javax.swing.event.ListSelectionListener;

    public class GUI {
        private final WestminsterShoppingManager shoppingManager;

        private static final String WINDOW_TITLE = "Westminster Shopping Centre";
        private static final String SHOPPING_CART_TITLE = "Shopping cart";

        // Sample data and column names for JTable

        private static final String[] COLUMN_NAMES = {"Product ID", "Name", "Category", "Price(£)", "info"};

        public JFrame f = new JFrame(WINDOW_TITLE);
        final JPanel mainPanel = new JPanel();
        final JPanel headerPanel = new JPanel(new FlowLayout());
        final JPanel tablePanel = new JPanel();
        JPanel detailsPanel = new JPanel();

        public ShoppingCartJFrame shoppingCartJFrame;

        JComboBox<String> cb;
        JTable table;
        DefaultTableModel tableModel;


        public GUI(WestminsterShoppingManager shoppingManager) {
            this.shoppingManager = shoppingManager;
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            // Create JComboBox and add it to the headerPanel
            JLabel label1 = new JLabel("Select product category: ");
            String[] choices = {"All", "Electronics", "Clothing"};
            cb = new JComboBox<>(choices);

            // Create "shopping cart" button
            JButton shoppingcart = new JButton("Shopping Cart");
            shoppingcart.addActionListener(e -> {
                if (shoppingCartJFrame != null) return;
                shoppingCartJFrame = new ShoppingCartJFrame(shoppingManager.getShoppingCart());

                shoppingCartJFrame.setVisible(true);

                // We opened a child window, disable parent
                f.setEnabled(false);
            });

            headerPanel.add(label1);
            headerPanel.add(cb);

            // Create JTable                                                           //Referenced Oracle help center for the table
            tableModel = new DefaultTableModel(ListToArray(), COLUMN_NAMES);
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            // Enable sorting for the table
            table.setAutoCreateRowSorter(true);

            // Set up JTable properties
            table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setFillsViewportHeight(true);

            // Add the JTable to the tablePanel
            tablePanel.add(scrollPane);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            // add shopping cart button to header
            headerPanel.add(shoppingcart);

            // Add sub-panels to the main panel
            mainPanel.add(headerPanel);
            mainPanel.add(tablePanel);
            mainPanel.add(detailsPanel);

            // Add main panel to the frame
            f.add(mainPanel);

            // Set frame properties
            f.setSize(900, 600);
            f.setLocation(0, 600);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
            // To detect changes in the selection state of the table when user selects a product
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        DetailsPanel();
                    }
                }
            });
            // when you click the details panel will be called and updated                               //referenced stackOverflow
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        int selectedRowIndex = table.getSelectedRow();
                        if (selectedRowIndex >= 0) {
                            DetailsPanel();
                        }
                    }
                }
            });
            //
            ListSelectionModel selectionModel = table.getSelectionModel();
            selectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        DetailsPanel();
                    }
                }
            });
        }
        // To add info the to table
        private String getInfo(Product product) {
            if (product instanceof Clothing) {
                Clothing clothing = (Clothing) product;
                return clothing.getSize() + ", " + clothing.getColour();
            } else if (product instanceof Electronics) {
                Electronics electronics = (Electronics) product;
                return electronics.getBrand()+", "+ electronics.getWarranty()+" weeks warranty" ;
            } else {
                return "";
            }
        }

        private Object[][] ListToArray() {
            List<Product> productList = shoppingManager.getListOfProducts();
            Object[][] array = new Object[productList.size()][WestminsterShoppingManager.COLUMN_NAMES.length];

            for (int i = 0; i < productList.size(); i++) {
                Product product = productList.get(i);
                array[i] = new Object[]{
                    product.getProductID(),
                    product.getProductName(),
                    product.getType(),
                    product.getPrice(),
                    getInfo(product)
                };
            }
            return array;
        }

        public void updateTable() {
            SwingUtilities.invokeLater(() -> {
                Object[][] data = ListToArray();
                tableModel.setDataVector(data, COLUMN_NAMES);
                tableModel.fireTableDataChanged();
            });
        }

        private void DetailsPanel() {
            // Clear the existing components in the details panel
            detailsPanel.removeAll();


            // Get the selected row index from the JTable
            int selectedRowIndex = table.getSelectedRow();

            if (selectedRowIndex >= 0) {
                // Get the product details with the row u selected
                List<Product> productList = shoppingManager.getListOfProducts();
                if (selectedRowIndex < productList.size()) {
                    Product selectedProduct = productList.get(selectedRowIndex);
                    Clothing selectedClothing = null;
                    Electronics selectedElectronics = null;

                    if (selectedProduct instanceof Clothing) {
                        selectedClothing = (Clothing) selectedProduct;
                    }
                    if (selectedProduct instanceof Electronics) {
                        selectedElectronics = (Electronics) selectedProduct;
                    }


                    // Add details to the details panel
                    detailsPanel.add(new JLabel("Selected Product - Details"+"\n"));
                    detailsPanel.add(new JLabel("Product ID: " + selectedProduct.getProductID()+"\n"));
                    detailsPanel.add(new JLabel("Category: " + selectedProduct.getType()+"\n"));
                    detailsPanel.add(new JLabel("Name: " + selectedProduct.getProductName()+"\n"));



                    if (selectedClothing != null) {
                        detailsPanel.add(new JLabel("Size: " + selectedClothing.getSize()+"\n"));
                        detailsPanel.add(new JLabel("Color: " + selectedClothing.getColour()+"\n"));
                    }

                    if (selectedElectronics != null) {
                        detailsPanel.add(new JLabel("Warranty: " + selectedElectronics.getWarranty()+"\n"));
                        detailsPanel.add(new JLabel("Brand: " + selectedElectronics.getBrand()+"\n"));
                    }

                    JButton cartBtn = new JButton("Add to cart");
                    cartBtn.addActionListener(e -> {
                        shoppingManager.getShoppingCart().add(selectedProduct);
                    });
                    detailsPanel.add(cartBtn);

                    // Repaint and validate the details panel to reflect the changes
                    detailsPanel.revalidate();
                    detailsPanel.repaint();
                }
            }
        }
    }
