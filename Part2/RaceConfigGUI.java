import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.*;

/**
 * Shows screens to configure race and customise typists.
 * 
 * @author Zahra Bint Afzal Asghar
 * @version 0.1
 */
public class RaceConfigGUI
{
    private final JFrame configFrame;
    private final JPanel cardPanel; // Panel for card layout that switches from race configuration to customising typists
    private TypingRaceGUI Race; // Starts the race
    private TypistGUI[] TypistList; // List of typists
    private int seatCount;
    private JPanel configPanel;
    private int pageIndex = 0; //Keeps track of how many times to reset the customise typist page.


    // Constructor for objects of Class RaceConfigGUI.
    // Creates a card layout to switch between pages.
    public RaceConfigGUI(){
        this.configFrame = new JFrame("Typing Race");
        configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configFrame.setSize(800,650);
        configFrame.getContentPane().setBackground(new Color(247, 237, 255));
        this.cardPanel = new JPanel(new CardLayout());
        cardPanel.setOpaque(false);
        configFrame.add(cardPanel);
    }

    /**
     * UI design for race configuration options. Choosing passage length, how many typists and difficulty modifiers.
     */
    private void startRaceGUI()
    {
        final String[] PASSAGE_OPTIONS = {"Short", "Medium", "Long", "Custom"};
        configPanel = new JPanel();
        configPanel.setOpaque(false);
        configPanel.setBorder(new EmptyBorder(25,25,25,25));
        BoxLayout boxLayoutManager = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
        configPanel.setLayout(boxLayoutManager);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Race Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(124, 90, 174));
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));
        titlePanel.add(titleLabel);
        configPanel.add(titlePanel);

        // Option to choose passage length from pre-defined list.
        JComboBox selectedPassage = choosePassage(configPanel, PASSAGE_OPTIONS);

        JPanel customPanel = new JPanel();
        customPanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(customPanel, BoxLayout.Y_AXIS);
        customPanel.setLayout(boxLayout);
        customPanel.setBorder(new EmptyBorder(10,0,10,0));
        JLabel customPassageLabel = new JLabel("If chose custom, enter the custom passage:");
        customPassageLabel.setBorder(new EmptyBorder(10,0,10,0));
        customPassageLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customPassageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        customPanel.add(customPassageLabel);
        JTextArea customPassageField = new JTextArea(8,50);
        customPassageField.setMaximumSize(customPassageField.getPreferredSize());
        customPassageField.setAlignmentX(Component.CENTER_ALIGNMENT);
        customPanel.add(customPassageField);
        configPanel.add(customPanel);

        // Option to choose how many typists from a slider (2-6).
        JSlider seatSlider = chooseSeatCount(configPanel);

        // Option to choose difficulty modifiers, can choose more than one option.
        JCheckBox[] diffArray = chooseDiffModifiers(configPanel);

        JButton nextButton = new JButton("Next");
        configPanel.add(nextButton);
        nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String chosenPassage = selectedPassage.getSelectedItem().toString();
                String customPassage = null;
                int index = 0;
                if(chosenPassage.equals("Custom")){
                    customPassage = customPassageField.getText();
                }
                seatCount = seatSlider.getValue();
                for(JCheckBox b: diffArray){
                    if(b.isSelected()){
                        index++;
                    }
                }
                String[] difficultyChosenArray = new String[index];
                int arindex = 0;
                //checks which checkbox is selected
                for(JCheckBox c: diffArray){
                    if(c.isSelected()){
                        String modifier = c.getText();
                        difficultyChosenArray[arindex] = modifier;
                        arindex++;
                    }
                }
                Race = new TypingRaceGUI(chosenPassage, seatCount, difficultyChosenArray, customPassage, cardPanel);
                TypistList = new TypistGUI[seatCount];
                CardLayout cardLayout = (CardLayout)(cardPanel.getLayout());
                cardLayout.next(cardPanel);
                cardPanel.repaint();
                cardPanel.revalidate();
            }
        });
        // Adds the race configuration panel to the panel that has card layout.
        cardPanel.add(configPanel, "panel");
        showCustomiseTypist();
        configFrame.setVisible(true);
    }

    /**
     * Displays the options for passage selection using a comboBox. Users can only choose one option.
     * 
     * @param configPanel the panel that will display the race configuration options.
     * @param PASSAGE_OPTIONS Array of options for passage length.
     */
    private JComboBox choosePassage(JPanel configPanel, String[] PASSAGE_OPTIONS){
        JPanel passagePanel = new JPanel();
        passagePanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(passagePanel, BoxLayout.Y_AXIS);
        passagePanel.setLayout(boxLayout);
        passagePanel.setBorder(new EmptyBorder(10,0,10,0));
        JLabel passageLabel = new JLabel("Choose passage length:");
        passageLabel.setBorder(new EmptyBorder(10,0,10,0));
        passageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        passageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passagePanel.add(passageLabel);
        JComboBox<String> passageSelect = new JComboBox<>(PASSAGE_OPTIONS);
        passageSelect.setMaximumSize(passageSelect.getPreferredSize());
        passageSelect.setBackground(Color.WHITE);
        passageSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        passagePanel.add(passageSelect);
        configPanel.add(passagePanel);
        return passageSelect;
    }

    /**
     * Displays a slider so user can choose how many typists. Users can only choose one option.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private JSlider chooseSeatCount(JPanel configPanel){
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(seatPanel, BoxLayout.Y_AXIS);
        seatPanel.setLayout(boxLayout);
        seatPanel.setBorder(new EmptyBorder(10,0,10,0));
        JLabel countLabel = new JLabel("How many typists?");
        countLabel.setBorder(new EmptyBorder(10,0,10,0));
        countLabel.setFont(new Font("Arial", Font.BOLD, 20));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        seatPanel.add(countLabel);
        JSlider seatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        seatSlider.setBackground(new Color(247, 237, 255));
        seatSlider.setMaximum(6);
        seatSlider.setMinimum(2);
        seatSlider.setMajorTickSpacing(2);
        seatSlider.setMinorTickSpacing(1);
        seatSlider.setPaintLabels(true);
        seatSlider.setPaintTicks(true);
        seatSlider.setValue(2);
        seatSlider.setMaximumSize(seatSlider.getPreferredSize());
        seatSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        seatPanel.add(seatSlider);
        configPanel.add(seatPanel);
        return seatSlider;
    }

    /**
     * Displays the options for choosing difficulty modifiers. Users can choose multiple options.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private JCheckBox[] chooseDiffModifiers(JPanel configPanel){
        JPanel diffPanel = new JPanel();
        diffPanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(diffPanel, BoxLayout.Y_AXIS);
        diffPanel.setLayout(boxLayout);
        diffPanel.setBorder(new EmptyBorder(10,0,10,0));
        JLabel difficultyLabel = new JLabel("Choose difficulty modifiers:");
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyLabel.setBorder(new EmptyBorder(10,0,10,0));
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        diffPanel.add(difficultyLabel);

        JLabel effectLabel1 = new JLabel("AutoCorrect: halves slide back amount. | Caffeine Mode: speed -10%, burnout +10%. | Night Shift: accuracy -0.07.");
        effectLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
        effectLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        diffPanel.add(effectLabel1);

        JPanel diffCheckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        diffCheckPanel.setOpaque(false);
        JCheckBox diffOption1 = new JCheckBox("AutoCorrect");
        JCheckBox diffOption2 = new JCheckBox("Caffeine Mode");
        JCheckBox diffOption3 = new JCheckBox("Night Shift");
        //Styling
        diffOption1.setBackground(new Color(247, 237, 255));
        diffOption2.setBackground(new Color(247, 237, 255));
        diffOption3.setBackground(new Color(247, 237, 255));
        diffOption1.setFont(new Font("Arial", Font.PLAIN, 16));
        diffOption2.setFont(new Font("Arial", Font.PLAIN, 16));
        diffOption3.setFont(new Font("Arial", Font.PLAIN, 16));
        diffCheckPanel.add(diffOption1);
        diffCheckPanel.add(diffOption2);
        diffCheckPanel.add(diffOption3);
        diffCheckPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        diffPanel.add(diffCheckPanel);
        //Array of checkboxes
        JCheckBox[] boxArray = {diffOption1, diffOption2, diffOption3};
        configPanel.add(diffPanel);
        return boxArray;
    }

    /**
     * UI for customising typists 
     * Displays all options
     */
    private void showCustomiseTypist(){
        final String[] TYPING_STYLE_OPTIONS = {"None","Touch Typing", "Phone Thumbs", "HuntNPeck"};
        final String[] KEYBOARD_OPTIONS = {"None" ,"Ergonomic", "Mechanical", "Touch Screen"};
        final String[] ACCESSORY_OPTIONS ={ "None","Wrist support", "Energy drink", "Noise cancelling headphones"};
        JPanel customisePanel = new JPanel();
        //Styling
        customisePanel.setBackground(new Color(247, 237, 255));
        customisePanel.setBorder(new EmptyBorder(30,30,30,30));
        BoxLayout boxLayoutManager = new BoxLayout(customisePanel, BoxLayout.Y_AXIS);
        customisePanel.setLayout(boxLayoutManager);
        //Add a scrollbar
        JScrollPane panelScrollPane = new JScrollPane(customisePanel);
        panelScrollPane.setOpaque(false);

        JPanel titlePanel = new JPanel();
        BoxLayout boxLayoutManag = new BoxLayout(titlePanel, BoxLayout.Y_AXIS);
        titlePanel.setLayout(boxLayoutManag);
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Customise Typists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(10,0,10,0));
        titleLabel.setForeground(new Color(124, 90, 174));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);

        JLabel playerNumLabel = new JLabel("Player " + (pageIndex + 1));
        playerNumLabel.setBorder(new EmptyBorder(10,0,10,0));
        playerNumLabel.setForeground(new Color(124, 90, 174));
        playerNumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(playerNumLabel);
        customisePanel.add(titlePanel);

        // Displays area to enter name
        JTextField nameField = enterTypistName(customisePanel);

        //Displays area to input accuracy
        JTextField accuracyField = chooseAccuracy(customisePanel);

        // Displays typing style options
        JComboBox typingStyleBox = chooseTypingStyle(customisePanel, TYPING_STYLE_OPTIONS);

        // Displays keyboard options
        JComboBox keyboardBox = chooseKeyboardType(customisePanel, KEYBOARD_OPTIONS);

        // Displays symbol options
        JTextField symbolBox = chooseSymbol(customisePanel);

        // Displays colour options
        JColorChooser colourChooser =  chooseColour(customisePanel);

        // Display accessory options
        JComboBox accessoryBox = chooseAccessory(customisePanel, ACCESSORY_OPTIONS);

        // Button that goes to the next page, which is to customise typists
        JButton nextButton = new JButton("Next");
         nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String nameEntered = nameField.getText();
                String styleChosen = typingStyleBox.getSelectedItem().toString();                
                String keyboardChosen = keyboardBox.getSelectedItem().toString();                
                String symbolChosen = symbolBox.getText();
                char symbol = symbolChosen.charAt(0);             
                Color colourChosen = colourChooser.getColor();
                String accChosen = accessoryBox.getSelectedItem().toString();
                double accuracy = Double.parseDouble(accuracyField.getText());

                TypistGUI typist = new TypistGUI(symbol, nameEntered, styleChosen, keyboardChosen, colourChosen, accChosen, accuracy);
                addTypist(typist);
                if(pageIndex < seatCount-1){
                    resetAllFields(typingStyleBox, keyboardBox, symbolBox, colourChooser, accessoryBox, nameField, accuracyField);
                    increaseIndex();
                    playerNumLabel.setText("Player " + (pageIndex + 1));
                }
                else{
                    Race.setTypistList(TypistList);
                    Race.startRace();
                }
            }
        });
        customisePanel.add(nextButton);
        cardPanel.add(panelScrollPane, "Panel2");
    }

    private void addTypist(TypistGUI theTypist){
        TypistList[pageIndex] = theTypist;
    }

    /** 
     * Encapsulation method
     * Increases the pageIndex
     * */ 
    private void increaseIndex(){
        pageIndex++;
    }

    /**
     * Resets the customise typists page, to allow all typists to be customised.
     * 
     * @param typingStyleBox input area to choose typing style
     * @param keyboardBox input area to choose keyboard style
     * @param symbolBox input area for entering typist symbol
     * @param colourChooser color chooser to choose typist colour, reset to default colour, white.
     * @param accessoryBox input area to choose accessories.
     * @param nameField text field for entering typist's name
     * @param accuracyField text field for entering accuracy
     */
    public void resetAllFields(JComboBox typingStyleBox, JComboBox keyboardBox, JTextField symbolBox, JColorChooser colourChooser, JComboBox accessoryBox, JTextField nameField, JTextField accuracyField){
        typingStyleBox.setSelectedIndex(0);
        keyboardBox.setSelectedIndex(0);
        symbolBox.setText("");
        colourChooser.setColor(Color.WHITE);
        accessoryBox.setSelectedIndex(0);
        nameField.setText("");
        accuracyField.setText("");
    }

    /**
     * Displays text area to enter typist's name.
     * 
     * @param customisePanel container panel
     */
    private JTextField enterTypistName(JPanel customisePanel){
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(namePanel, BoxLayout.Y_AXIS);
        namePanel.setLayout(boxLayout);
        JLabel nameLabel = new JLabel("Enter typist name");
        nameLabel.setBorder(new EmptyBorder(10,0,10,0));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        namePanel.add(nameLabel);

        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(nameField.getPreferredSize());
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        namePanel.add(nameField);
        customisePanel.add(namePanel);
        return nameField;
    }

    /** 
     * Displays text field for entering typist's accuracy
    */
    private JTextField chooseAccuracy(JPanel customisePanel){
        JPanel accuracyPanel = new JPanel();
        accuracyPanel.setOpaque(false);
        BoxLayout boxLayout = new BoxLayout(accuracyPanel, BoxLayout.Y_AXIS);
        accuracyPanel.setLayout(boxLayout);

        JLabel accuracyLabel = new JLabel("Choose your accuracy");
        accuracyLabel.setBorder(new EmptyBorder(10,0,10,0));
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        accuracyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accuracyPanel.add(accuracyLabel);

        JTextField accuracyField = new JTextField(5);
        accuracyField.setMaximumSize(accuracyField.getPreferredSize());
        accuracyField.setAlignmentX(Component.CENTER_ALIGNMENT);
        accuracyPanel.add(accuracyField);
        customisePanel.add(accuracyPanel);
        return accuracyField;
    }

    /**
     * Displays options for typing style. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all options for typing style.
     */
    private JComboBox chooseTypingStyle(JPanel customisePanel, String[] typingStyleOptions){
        JPanel typingPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(typingPanel, BoxLayout.Y_AXIS);
        typingPanel.setLayout(boxLayout);
        typingPanel.setOpaque(false);
        JLabel typingLabel = new JLabel("Choose typing style:");
        typingLabel.setBorder(new EmptyBorder(10,0,10,0));
        typingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        typingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typingPanel.add(typingLabel);

        JLabel effectLabel1 = new JLabel("Touch Typing: acc +10%, burnout +30%  |  Phone Thumbs: acc -5%, burnout +10%  |  HuntNPeck: Normal");
        effectLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
        effectLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        typingPanel.add(effectLabel1);

        JComboBox<String> styleSelect = new JComboBox<>(typingStyleOptions);
        styleSelect.setMaximumSize(styleSelect.getPreferredSize());
        styleSelect.setBackground(Color.WHITE);
        styleSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        typingPanel.add(styleSelect);
        customisePanel.add(typingPanel);
        return styleSelect;
    }

    /**
     * Displays options for keyboard type. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all keyboard types, the user can choose.
     */
    private JComboBox chooseKeyboardType(JPanel customisePanel, String[] KEYBOARD_OPTIONS){
        JPanel keyboardPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS);
        keyboardPanel.setLayout(boxLayout);
        keyboardPanel.setOpaque(false);
        JLabel keyboardLabel = new JLabel("Choose your keyboard type:");
        keyboardLabel.setBorder(new EmptyBorder(10,0,10,0));
        keyboardLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        keyboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyboardPanel.add(keyboardLabel);

        JLabel effectLabel1 = new JLabel("Ergonomic: mistype -20% (fastest)  |  Mechanical: Normal  |  Touchscreen: mistype +10% (slowest)");
        effectLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
        effectLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyboardPanel.add(effectLabel1);

        JComboBox<String> keyboardSelect = new JComboBox<>(KEYBOARD_OPTIONS);
        keyboardSelect.setMaximumSize(keyboardSelect.getPreferredSize());
        keyboardSelect.setBackground(Color.WHITE);
        keyboardSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyboardPanel.add(keyboardSelect);
        customisePanel.add(keyboardPanel);
        return keyboardSelect;
    }

    /**
     * Displays options for typist's symbol. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all the symbols the user can choose from.
     */
    private JTextField chooseSymbol(JPanel customisePanel){
        JPanel symbolPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(symbolPanel, BoxLayout.Y_AXIS);
        symbolPanel.setLayout(boxLayout);
        symbolPanel.setOpaque(false);
        JLabel symbolLabel = new JLabel("Choose your symbol:");
        symbolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        symbolLabel.setBorder(new EmptyBorder(10,0,10,0));
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        symbolPanel.add(symbolLabel);

        JTextField symbolSelect = new JTextField(1);
        symbolSelect.setMaximumSize(symbolSelect.getPreferredSize());
        symbolSelect.setAlignmentX(Component.CENTER_ALIGNMENT);

        symbolPanel.add(symbolSelect);
        customisePanel.add(symbolPanel);
        return symbolSelect;
    }

    /**
     * Displays options for colour.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     */
    private JColorChooser chooseColour(JPanel customisePanel){
        JPanel colourPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(colourPanel, BoxLayout.Y_AXIS);
        colourPanel.setLayout(boxLayout);
        colourPanel.setOpaque(false);
        JLabel colourLabel = new JLabel("Choose your colour:");
        colourLabel.setBorder(new EmptyBorder(10,0,10,0));
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        colourLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colourPanel.add(colourLabel);

        JColorChooser chooseColour = new JColorChooser();
        chooseColour.setOpaque(false);
        chooseColour.setPreviewPanel(new JPanel());
        chooseColour.setAlignmentX(Component.CENTER_ALIGNMENT);
        AbstractColorChooserPanel[] colorPanels = chooseColour.getChooserPanels();
        for(AbstractColorChooserPanel p: colorPanels){
            if(!p.getDisplayName().equals("Swatches")){
                chooseColour.removeChooserPanel(p);
            }
        }

        colourPanel.add(chooseColour);
        customisePanel.add(colourPanel);
        return chooseColour;
    }

    /**
     * Displays options for accessories. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all accessories the user can choose from.
     */
    private JComboBox chooseAccessory(JPanel customisePanel, String[] ACCESSORIES_OPTIONS){
        JPanel accPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(accPanel, BoxLayout.Y_AXIS);
        accPanel.setLayout(boxLayout);
        accPanel.setOpaque(false);
        JLabel accLabel = new JLabel("Choose your accessories:");
        accLabel.setBorder(new EmptyBorder(10,0,10,0));
        accLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        accLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accPanel.add(accLabel);

        JLabel effectLabel1 = new JLabel("Wrist Support: burnout -1  |  Energy Drink: first half acc+10%, last half acc-10%  |  Noise Cancelling Headphones: mistype -10%");

        effectLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
        effectLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        accPanel.add(effectLabel1);

        JComboBox<String> accSelect = new JComboBox<>(ACCESSORIES_OPTIONS);
        accSelect.setMaximumSize(accSelect.getPreferredSize());
        accSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        accSelect.setBackground(Color.WHITE);
        accPanel.add(accSelect);
        customisePanel.add(accPanel);
        return accSelect;
    }

    public static void main(String[] args)
    {
        RaceConfigGUI race = new RaceConfigGUI();
        race.startRaceGUI();
    }
}