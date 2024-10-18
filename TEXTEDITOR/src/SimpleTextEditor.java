import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleTextEditor extends JFrame implements ActionListener {
    // Components
    JTextArea textArea;
    JMenuBar menuBar;
    JMenu fileMenu, editMenu, viewMenu;
    JMenuItem newItem, openItem, saveItem, exitItem;
    JMenuItem cutItem, copyItem, pasteItem, undoItem, redoItem, fontItem, themeItem, wordCountItem;
    JMenuItem zoomInItem, zoomOutItem, toggleStatusBarItem;
    JScrollPane scrollPane;
    JLabel statusBar;
    UndoManager undoManager;
    boolean isStatusBarVisible = true;
    int fontSize = 18;  // Default font size

    public SimpleTextEditor() {
        // Set up the frame
        setTitle("Simple Text Editor");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, fontSize));
        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Create status bar
        statusBar = new JLabel("Line: 1, Column: 1");
        add(statusBar, BorderLayout.SOUTH);

        // Create menu bar
        menuBar = new JMenuBar();

        // Create file menu
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Create edit menu
        editMenu = new JMenu("Edit");
        cutItem = new JMenuItem("Cut");
        copyItem = new JMenuItem("Copy");
        pasteItem = new JMenuItem("Paste");
        undoItem = new JMenuItem("Undo");
        redoItem = new JMenuItem("Redo");
        fontItem = new JMenuItem("Font");
        wordCountItem = new JMenuItem("Word Count");

        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());

        // Undo/Redo functionality using UndoManager
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        undoItem.addActionListener(e -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        });
        redoItem.addActionListener(e -> {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        });

        wordCountItem.addActionListener(e -> {
            String text = textArea.getText();
            int wordCount = text.split("\\s+").length;
            int charCount = text.length();
            JOptionPane.showMessageDialog(null, "Words: " + wordCount + "\nCharacters: " + charCount);
        });

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(fontItem);
        editMenu.add(wordCountItem);
        menuBar.add(editMenu);

        // Create view menu
        viewMenu = new JMenu("View");
        themeItem = new JMenuItem("Toggle Dark Mode");
        zoomInItem = new JMenuItem("Zoom In");
        zoomOutItem = new JMenuItem("Zoom Out");
        toggleStatusBarItem = new JMenuItem("Toggle Status Bar");

        // Add action listeners for View menu items
        themeItem.addActionListener(e -> {
            if (textArea.getBackground() == Color.WHITE) {
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.WHITE);
            } else {
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
            }
        });

        zoomInItem.addActionListener(e -> {
            fontSize += 2;
            textArea.setFont(new Font("Arial", Font.PLAIN, fontSize));
        });

        zoomOutItem.addActionListener(e -> {
            if (fontSize > 8) {
                fontSize -= 2;
                textArea.setFont(new Font("Arial", Font.PLAIN, fontSize));
            }
        });

        toggleStatusBarItem.addActionListener(e -> {
            isStatusBarVisible = !isStatusBarVisible;
            statusBar.setVisible(isStatusBarVisible);
        });

        // Add items to view menu
        viewMenu.add(themeItem);
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(toggleStatusBarItem);
        menuBar.add(viewMenu);

        // Set menu bar
        setJMenuBar(menuBar);

        // Add caret listener for status bar updates
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int pos = textArea.getCaretPosition();
                try {
                    int line = textArea.getLineOfOffset(pos);
                    int column = pos - textArea.getLineStartOffset(line);
                    statusBar.setText("Line: " + (line + 1) + ", Column: " + (column + 1));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Add action listeners for file menu
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(e -> System.exit(0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) {
            textArea.setText("");
        } else if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    textArea.setText("");
                    while ((line = br.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    br.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showSaveDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(textArea.getText());
                    bw.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Run the text editor
        SimpleTextEditor editor = new SimpleTextEditor();
        editor.setVisible(true);
    }
}
