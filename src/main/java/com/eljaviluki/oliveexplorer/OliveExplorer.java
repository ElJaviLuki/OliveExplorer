package com.eljaviluki.oliveexplorer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;

public class OliveExplorer extends JFrame {
    private JTree tree;
    private JTable table;

    public OliveExplorer() {
        setTitle("Olive Explorer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Create the root node for the file system tree
        File[] roots = File.listRoots();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("File System");
        for (File root : roots) {
            rootNode.add(new DefaultMutableTreeNode(root));
        }

        // Create the tree
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setRootVisible(true);

        // Create the file table
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create a split pane to display the tree and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(table));
        splitPane.setDividerLocation(200);

        // Add a tree selection listener to update the table when a directory is selected
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof File selectedFile) {
                if (selectedFile.isDirectory()) {
                    updateTable(selectedFile);
                }
            }
        });

        // Create a button to open the selected file or directory
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof File selectedFile) {
                if (selectedFile.isDirectory()) {
                    updateTable(selectedFile);
                } else {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(selectedFile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Create the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(openButton, BorderLayout.SOUTH);

        // Set the main panel as the content pane
        setContentPane(mainPanel);
    }

    private void updateTable(File directory) {
        File[] files = directory.listFiles();
        String[] columnNames = {"Name", "Size", "Type"};

        assert files != null;
        Object[][] data = new Object[files.length][3];

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            data[i] = new Object[]{
                    file.getName(),
                    file.length(),
                    FileSystemView.getFileSystemView().getSystemTypeDescription(file)
            };
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OliveExplorer().setVisible(true));
    }
}
