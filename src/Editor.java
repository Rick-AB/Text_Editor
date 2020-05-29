import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Editor extends JFrame {
    JTextField SearchField = new JTextField(15);
    JTextArea TextArea = new JTextArea();
    JPanel panel = new JPanel();
    JCheckBox box = new JCheckBox("Use regex");
    Deque<Integer> queue = new ArrayDeque<>();
    Deque<Integer> regexQueue = new ArrayDeque<>();
    Deque<Integer> stack = new ArrayDeque<>();
    Deque<Integer> regexStack = new ArrayDeque<>();
    final JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    public Editor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setTitle("Text Editor");
        initUI();
        setVisible(true);
    }

    public void initUI() {

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setName("MenuFile");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setName("MenuOpen");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setName("MenuSave");

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");

        menu.add(openItem);
        menu.add(saveItem);
        menu.addSeparator();
        menu.add(exitItem);

        openItem.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Open File");
            fileChooser.setVisible(true);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (fileChooser.getSelectedFile().isFile()) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileInputStream stream = new FileInputStream(file);
                        byte[] arr = stream.readAllBytes();
                        String text = new String(arr, StandardCharsets.UTF_8);
                        stream.close();
                        TextArea.setText(text);
                        TextArea.setCaretPosition(1);
                    }  catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }else {
                    TextArea.setText("");
                }
            }
        });

        saveItem.addActionListener(e -> {
            int returnValue = fileChooser.showSaveDialog(null);
            fileChooser.setSize(200,200);
            if (returnValue == JFileChooser.APPROVE_OPTION){
                Path path = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    final BufferedWriter writer = Files.newBufferedWriter(path);
                    writer.write(TextArea.getText());
                    writer.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });


        exitItem.addActionListener(e -> {
            System.exit(0);
            dispose();
        });

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        menuBar.add(searchMenu);

        JMenuItem startSearch = new JMenuItem("Start Search");
        startSearch.setName("MenuStartSearch");

        JMenuItem previousMatch = new JMenuItem("Previous Match");
        previousMatch.setName("MenuPreviousMatch");

        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("MenuNextMatch");

        JMenuItem useRegex = new JMenuItem("Use Regex");
        useRegex.setName("MenuUseRegExp");

        searchMenu.add(startSearch);
        searchMenu.add(previousMatch);
        searchMenu.add(nextMatch);
        searchMenu.add(useRegex);

        startSearch.addActionListener(e -> {
            search(SearchField.getText(), TextArea.getText());
        });

        previousMatch.addActionListener(e -> {
            getPrevious(SearchField.getText());
        });

        nextMatch.addActionListener(e -> {
            getNext(SearchField.getText());
        });

        useRegex.addActionListener(e -> {
            box.setSelected(true);
        });

        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 7));
        panel.setBounds(0, 0, 500, 40);
        add(panel, BorderLayout.NORTH);

        setButtons();

        Dimension d = new Dimension(70, 25);
        SearchField.setMaximumSize(d);
        SearchField.setMinimumSize(d);
        SearchField.setPreferredSize(d);
        SearchField.setName("SearchField");
        SearchField.setBounds(20, 45, 100, 20);
        panel.add(SearchField);

        setSearchButtons();

        setCheckBox();

        TextArea.setName("TextArea");
        TextArea.setBounds(7, 50, 370, 270);
        JScrollPane ScrollPane = new JScrollPane(TextArea);
        ScrollPane.setName("ScrollPane");
        ScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ScrollPane.setBounds(7, 40, 520, 390);
        add(ScrollPane, BorderLayout.CENTER);

        fileChooser.setName("FileChooser");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Open File");
        add(fileChooser);
//        fileChooser.setVisible(false);
    }
    public void setButtons(){
        Dimension d = new Dimension(25,25);
        ImageIcon openIcon = new ImageIcon("C:\\Users\\richa\\Downloads\\folder.PNG");
        JButton OpenButton = new JButton(openIcon);
        OpenButton.setMaximumSize(d);
        OpenButton.setMinimumSize(d);
        OpenButton.setPreferredSize(d);
        OpenButton.setName("OpenButton");
        OpenButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            fileChooser.setSize(200,200);
            fileChooser.setVisible(true);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (fileChooser.getSelectedFile().isFile()) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileInputStream stream = new FileInputStream(file);
                        byte[] arr = stream.readAllBytes();
                        String text = new String(arr, StandardCharsets.UTF_8);
                        stream.close();
                        TextArea.setText(text);
                        TextArea.setCaretPosition(1);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }else {
                    TextArea.setText("");
                }
            }
        });
        panel.add(OpenButton);

        ImageIcon saveIcon = new ImageIcon("C:\\Users\\richa\\Downloads\\save.PNG");
        JButton SaveButton = new JButton(saveIcon);
        SaveButton.setMaximumSize(d);
        SaveButton.setMinimumSize(d);
        SaveButton.setPreferredSize(d);
        SaveButton.setName("SaveButton");
        SaveButton.addActionListener(e -> {
            int returnValue = fileChooser.showSaveDialog(null);
            fileChooser.setSize(200,200);
            if (returnValue == JFileChooser.APPROVE_OPTION){
                Path path = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    final BufferedWriter writer = Files.newBufferedWriter(path);
                    writer.write(TextArea.getText());
                    writer.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        panel.add(SaveButton);
    }

    public void search(String word, String text){
        if (!box.isSelected()) {
            queue.clear();
            stack.clear();
            Pattern pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                queue.add(matcher.start());
            }

            int index = queue.isEmpty()? 0 : queue.peekFirst();
            TextArea.setCaretPosition(index + word.length());
            TextArea.select(index, index + word.length());
            TextArea.grabFocus();
        }else {
            queue.clear();
            stack.clear();
            Pattern pattern = Pattern.compile(word);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                queue.add(matcher.start());
                regexQueue.add(matcher.group().length());
            }

            int index = queue.isEmpty()? 0 : queue.peekFirst();
            int length = regexQueue.isEmpty() ? 0 : regexQueue.peekFirst();
            TextArea.setCaretPosition(index + length);
            TextArea.select(index, index + length);
            TextArea.grabFocus();
        }
    }
    public void getNext(String word){
        if (!queue.isEmpty() && !box.isSelected()) {
            stack.push(queue.poll());
            int index = queue.isEmpty()? 0: queue.peekFirst();
            TextArea.setCaretPosition(index + word.length());
            TextArea.select(index,index+word.length());
            TextArea.grabFocus();
        }else {
            if (!queue.isEmpty()) {
                stack.push(queue.poll());
                regexStack.push(regexQueue.poll());
                int index = queue.isEmpty() ? 0 : queue.peekFirst();
                int length = regexQueue.isEmpty() ? 0 : regexQueue.peekFirst();
                TextArea.setCaretPosition(index + length);
                TextArea.select(index, index + length);
                TextArea.grabFocus();
            }

        }

    }
    public void getPrevious(String word){
        if (!stack.isEmpty() && !box.isSelected()) {
            int index = stack.pop();
            queue.addFirst(index);
            if (index != 0) {
                TextArea.setCaretPosition(index + word.length());
                TextArea.select(index, index + word.length());
                TextArea.grabFocus();
            }
        }else if (stack.isEmpty() && !box.isSelected()){
            Deque<Integer> another = new ArrayDeque<>(queue);
            while (!another.isEmpty()){
                stack.push(another.pop());
            }
            int index = stack.pop();
            queue.addFirst(index);
            TextArea.setCaretPosition(index + word.length());
            TextArea.select(index, index + word.length());
            TextArea.grabFocus();
        }
        else if (!stack.isEmpty() && box.isSelected()){
            int index = stack.pop();
            int length = regexStack.pop();
            queue.addFirst(index);
            regexQueue.addFirst(length);

            TextArea.setCaretPosition(index + length);
            TextArea.select(index , index + length);
            TextArea.grabFocus();
        }else {
            Deque<Integer> another = new ArrayDeque<>(queue);
            Deque<Integer> another1 = new ArrayDeque<>(regexQueue);
            while (!another.isEmpty() && !another1.isEmpty()){
                stack.push(another.pop());
                regexStack.push(another1.pop());
            }
            int index = stack.pop();
            int length = regexStack.pop();
            queue.addFirst(index);
            regexQueue.addFirst(length);
            TextArea.setCaretPosition(index + length);
            TextArea.select(index, index + length);
            TextArea.grabFocus();
        }
    }
    public void setSearchButtons(){
        Dimension d = new Dimension(25, 25);
        ImageIcon searchIcon = new ImageIcon("C:\\Users\\richa\\Downloads\\search.PNG");
        JButton searchButton = new JButton(searchIcon);
        searchButton.setName("StartSearchButton");
        searchButton.setMaximumSize(d);
        searchButton.setMinimumSize(d);
        searchButton.setPreferredSize(d);
        searchButton.addActionListener(e -> {
            search(SearchField.getText(),TextArea.getText());
        });
        panel.add(searchButton);

        ImageIcon previousIcon = new ImageIcon("C:\\Users\\richa\\Downloads\\lessthan.PNG");
        JButton previous = new JButton(previousIcon);
        previous.setName("PreviousMatchButton");
        previous.setMinimumSize(d);
        previous.setMaximumSize(d);
        previous.setPreferredSize(d);
        previous.addActionListener(e -> {
            getPrevious(SearchField.getText());
        });
        panel.add(previous);

        ImageIcon nextIcon = new ImageIcon("C:\\Users\\richa\\Downloads\\greaterthan.PNG");
        JButton next = new JButton(nextIcon);
        next.setName("NextMatchButton");
        next.setMinimumSize(d);
        next.setMaximumSize(d);
        next.setPreferredSize(d);
        next.addActionListener(e -> {
            getNext(SearchField.getText());
        });
        panel.add(next);
    }
    public void setCheckBox(){

        box.setName("UseRegExCheckbox");
        box.setSize(7,7);
        panel.add(box);
    }

}
