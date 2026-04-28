import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
    private JFrame configFrame;
    private JPanel cardPanel; // Panel for card layout that switches from race configuration to customising typists
    private TypingRaceGUI race; // Starts the race
    private TypistGUI[] TypistList; // List of typists
    private int seatCount;
    private JPanel configPanel;
    private int pageIndex = 0; //Keeps track of how many times to reset the customise typist page.


    // Constructor for objects of Class RaceConfigGUI.
    // Creates a card layout to switch between pages.
    public RaceConfigGUI()
    {
        this.configFrame = new JFrame("Typing Race");
        configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configFrame.setSize(800,650);
        this.cardPanel = new JPanel(new CardLayout());
        configFrame.add(cardPanel);
    }

    /**
     * UI design for race configuration options. Choosing passage length, how many typists and difficulty modifiers.
     */
    private void showRaceConfig()
    {
        final String[] PASSAGE_OPTIONS = {"Short", "Medium", "Long", "Custom"};
        configPanel = new JPanel();
        configPanel.setBorder(new EmptyBorder(30,30,30,30));
        configPanel.setMaximumSize(new Dimension(300, 650));
        BoxLayout boxLayoutManager = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
        configPanel.setLayout(boxLayoutManager);

        JLabel titleLabel = new JLabel("Race Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));
        configPanel.add(titleLabel);

        // Option to choose passage length from pre-defined list.
        JComboBox selectedPassage = choosePassage(configPanel, PASSAGE_OPTIONS);

        JPanel customPanel = new JPanel(new BorderLayout());
        JLabel customPassageLabel = new JLabel("If chose custom, enter the custom passage:");
        customPassageLabel.setBorder(new EmptyBorder(10,0,10,0));
        customPassageLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customPanel.add(customPassageLabel, BorderLayout.NORTH);
        JTextArea customPassageField = new JTextArea(8,50);
        customPanel.add(customPassageField, BorderLayout.SOUTH);
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
                String[] difficultyChosenArray = new String[3];
                int index = 0;
                if(chosenPassage.equals("Custom")){
                    customPassage = customPassageField.getText();
                }
                int ChosenseatCount = seatSlider.getValue();
                for(JCheckBox b: diffArray){
                    if(b.isSelected()){
                        String difficultyChosen = b.getText();
                        difficultyChosenArray[index] = difficultyChosen;
                        index++;
                    }
                }
                TypingRaceGUI Race = new TypingRaceGUI(chosenPassage, ChosenseatCount, difficultyChosenArray, customPassage, cardPanel);
                Race.setTypistList(TypistList);
                CardLayout cardLayout = (CardLayout)(cardPanel.getLayout());
                cardLayout.next(cardPanel);
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
        JPanel passagePanel = new JPanel(new BorderLayout());
        JLabel passageLabel = new JLabel("Choose passage length:");
        passageLabel.setBorder(new EmptyBorder(10,0,10,0));
        passageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passagePanel.add(passageLabel, BorderLayout.NORTH);
        JComboBox<String> passageSelect = new JComboBox<>(PASSAGE_OPTIONS);
        passageSelect.setBackground(Color.WHITE);
        passagePanel.add(passageSelect, BorderLayout.SOUTH);
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
        JLabel countLabel = new JLabel("How many typists?");
        countLabel.setBorder(new EmptyBorder(10,0,10,0));
        countLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        seatPanel.add(countLabel, BorderLayout.NORTH);
        JSlider seatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        seatSlider.setMaximum(6);
        seatSlider.setMinimum(2);
        seatSlider.setMajorTickSpacing(2);
        seatSlider.setMinorTickSpacing(1);
        seatSlider.setPaintLabels(true);
        seatSlider.setPaintTicks(true);
        seatSlider.setValue(2);
        seatPanel.add(seatSlider, BorderLayout.SOUTH);
        configPanel.add(seatPanel);
        return seatSlider;
    }

    /**
     * Displays the options for choosing difficulty modifiers. Users can choose multiple options.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private JCheckBox[] chooseDiffModifiers(JPanel configPanel){
        JPanel diffPanel = new JPanel(new BorderLayout());
        JLabel difficultyLabel = new JLabel("Choose difficulty modifiers:");
        difficultyLabel.setBorder(new EmptyBorder(10,0,10,0));
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        diffPanel.add(difficultyLabel, BorderLayout.NORTH);

        JPanel diffLabelPanel = new JPanel(new FlowLayout());
        JLabel effectLabel1 = new JLabel("AutoCorrect: halves slide back amount. | ");
        JLabel effectLabel2 = new JLabel("Caffeine Mode: speed -10%, burnout +10%. | ");
        JLabel effectLabel3 = new JLabel("Night Shift: accuracy -0.07.");
        diffLabelPanel.add(effectLabel1);
        diffLabelPanel.add(effectLabel2);
        diffLabelPanel.add(effectLabel3);
        diffPanel.add(diffLabelPanel, BorderLayout.CENTER);

        JPanel diffCheckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        JCheckBox diffOption1 = new JCheckBox("AutoCorrect");
        JCheckBox diffOption2 = new JCheckBox("Caffeine Mode");
        JCheckBox diffOption3 = new JCheckBox("Night Shift");
        diffOption1.setFont(new Font("Arial", Font.PLAIN, 16));
        diffOption2.setFont(new Font("Arial", Font.PLAIN, 16));
        diffOption3.setFont(new Font("Arial", Font.PLAIN, 16));
        diffCheckPanel.add(diffOption1);
        diffCheckPanel.add(diffOption2);
        diffCheckPanel.add(diffOption3);
        diffPanel.add(diffCheckPanel, BorderLayout.SOUTH);
        JCheckBox[] boxArray = {diffOption1, diffOption2, diffOption3};
        configPanel.add(diffPanel);
        return boxArray;
    }

    /**
     * UI for customising typists 
     */
    private void showCustomiseTypist(){
        final String[] TYPING_STYLE_OPTIONS = {"None","Touch Typing", "Phone Thumbs", "HuntNPeck"};
        final String[] KEYBOARD_OPTIONS = {"None" ,"Ergonomic", "Mechanical", "Touch Screen"};
        final String[] ACCESSORY_OPTIONS ={ "None","Wrist support", "Energy drink", "Noise cancelling headphones"};
        JPanel customisePanel = new JPanel();
        customisePanel.setBorder(new EmptyBorder(30,30,30,30));
        BoxLayout boxLayoutManager = new BoxLayout(customisePanel, BoxLayout.Y_AXIS);
        customisePanel.setLayout(boxLayoutManager);
        JScrollPane panelScrollPane = new JScrollPane(customisePanel);

        JLabel titleLabel = new JLabel("Customise Typists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(10,0,10,0));
        customisePanel.add(titleLabel);

        // Displays area to enter name
        JTextField nameField = enterTypistName(customisePanel);

        // Displays typing style options
        JComboBox typingStyleBox = chooseTypingStyle(customisePanel, TYPING_STYLE_OPTIONS);

        // Displays keyboard options
        JComboBox keyboardBox = chooseKeyboardType(customisePanel, KEYBOARD_OPTIONS);

        // Displays symbol options
        JTextField symbolBox = chooseSymbol(customisePanel);
        // symbolBox.addActionListener(new ActionListener(){
        //     @Override
        //     public void actionPerformed(ActionEvent e){
        //         if(symbolBox.getText().length() == 1){
                    
        //         }
        //     }
        // });

        // Displays colour options
        JColorChooser colourChooser =  chooseColour(customisePanel);

        // Display accessory options
        JComboBox accessoryBox = chooseAccessory(customisePanel, ACCESSORY_OPTIONS);

        // Button that goes to the next page, which is to customise typists
        JButton nextButton = new JButton("Next");
         nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(pageIndex <= seatCount){
                    String nameEntered = nameField.getText();
                    String styleChosen = typingStyleBox.getSelectedItem().toString();                
                    String keyboardChosen = keyboardBox.getSelectedItem().toString();                
                    String symbolChosen = symbolBox.getText();
                    char symbol = symbolChosen.charAt(0);             
                    Color colourChosen = colourChooser.getColor();
                    String accChosen = accessoryBox.getSelectedItem().toString();

                    TypistGUI typist = new TypistGUI(symbol, nameEntered, styleChosen, keyboardChosen, colourChosen, accChosen, 0.0);
                    TypistList[pageIndex] = typist;

                    resetAllFields(typingStyleBox, keyboardBox, symbolBox, colourChooser, accessoryBox);
                    increaseIndex();
                }
                else{
                    
                }
            }
        });
        customisePanel.add(nextButton);
        cardPanel.add(panelScrollPane, "Panel2");
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
     */
    public void resetAllFields(JComboBox typingStyleBox, JComboBox keyboardBox, JTextField symbolBox, JColorChooser colourChooser, JComboBox accessoryBox){
        typingStyleBox.setSelectedIndex(0);
        keyboardBox.setSelectedIndex(0);
        symbolBox.setText("");
        colourChooser.setColor(Color.WHITE);
        accessoryBox.setSelectedIndex(0);
    }

    /**
     * Displays text area to enter typist's name.
     */
    private JTextField enterTypistName(JPanel customisePanel){
        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Enter typist name");
        nameLabel.setBorder(new EmptyBorder(10,0,10,0));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        namePanel.add(nameLabel, BorderLayout.NORTH);

        JTextField nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.SOUTH);
        customisePanel.add(namePanel);
        return nameField;
    }

    /**
     * Displays options for typing style. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all options for typing style.
     */
    private JComboBox chooseTypingStyle(JPanel customisePanel, String[] typingStyleOptions){
        JPanel typingPanel = new JPanel(new BorderLayout());
        JLabel typingLabel = new JLabel("Choose typing style:");
        typingLabel.setBorder(new EmptyBorder(10,0,10,0));
        typingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        typingPanel.add(typingLabel, BorderLayout.NORTH);

        JPanel labelPanel = new JPanel(new FlowLayout());
        JLabel effectLabel1 = new JLabel("Touch Typing: acc +10%, burnout +30%  |  ");
        JLabel effectLabel2 = new JLabel("Phone Thumbs: acc -5%, burnout +10%  |  ");
        JLabel effectLabel3 = new JLabel("HuntNPeck: Normal");
        
        labelPanel.add(effectLabel1);
        labelPanel.add(effectLabel2);
        labelPanel.add(effectLabel3);
        typingPanel.add(labelPanel, BorderLayout.CENTER);

        JComboBox<String> styleSelect = new JComboBox<>(typingStyleOptions);
        styleSelect.setBackground(Color.WHITE);
        typingPanel.add(styleSelect, BorderLayout.SOUTH);
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
        JPanel keyboardPanel = new JPanel(new BorderLayout());
        JLabel keyboardLabel = new JLabel("Choose your keyboard type:");
        keyboardLabel.setBorder(new EmptyBorder(10,0,10,0));
        keyboardLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        keyboardPanel.add(keyboardLabel, BorderLayout.NORTH);

        JPanel labelPanel = new JPanel(new FlowLayout());
        JLabel effectLabel1 = new JLabel("Ergonomic: mistype -20% (fastest)  |  ");
        JLabel effectLabel2 = new JLabel("Mechanical: Normal  |  ");
        JLabel effectLabel3 = new JLabel("Touchscreen: mistype +10% (slowest)");

        labelPanel.add(effectLabel1);
        labelPanel.add(effectLabel2);
        labelPanel.add(effectLabel3);
        keyboardPanel.add(labelPanel, BorderLayout.CENTER);

        JComboBox<String> keyboardSelect = new JComboBox<>(KEYBOARD_OPTIONS);
        keyboardSelect.setBackground(Color.WHITE);
        keyboardPanel.add(keyboardSelect, BorderLayout.SOUTH);
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
        JPanel symbolPanel = new JPanel(new BorderLayout());
        JLabel symbolLabel = new JLabel("Choose your symbol:");
        symbolLabel.setBorder(new EmptyBorder(10,0,10,0));
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        symbolPanel.add(symbolLabel, BorderLayout.NORTH);

        JTextField symbolSelect = new JTextField(1);

        symbolPanel.add(symbolSelect, BorderLayout.SOUTH);
        customisePanel.add(symbolPanel);
        return symbolSelect;
    }

    /**
     * Displays options for colour.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     */
    private JColorChooser chooseColour(JPanel customisePanel){
        JPanel colourPanel = new JPanel(new BorderLayout());
        JLabel colourLabel = new JLabel("Choose your colour:");
        colourLabel.setBorder(new EmptyBorder(10,0,10,0));
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        colourPanel.add(colourLabel, BorderLayout.NORTH);

        JColorChooser chooseColour = new JColorChooser();
        chooseColour.setPreviewPanel(new JPanel());
        AbstractColorChooserPanel[] colorPanels = chooseColour.getChooserPanels();
        for(AbstractColorChooserPanel p: colorPanels){
            if(!p.getDisplayName().equals("Swatches")){
                chooseColour.removeChooserPanel(p);
            }
        }

        colourPanel.add(chooseColour, BorderLayout.SOUTH);
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
        JPanel accPanel = new JPanel(new BorderLayout());
        JLabel accLabel = new JLabel("Choose your accessories:");
        accLabel.setBorder(new EmptyBorder(10,0,10,0));
        accLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        accPanel.add(accLabel, BorderLayout.NORTH);

        JPanel effectPanel = new JPanel(new FlowLayout());
        JLabel effectLabel1 = new JLabel("Wrist Support: reduces burnout duration");
        JLabel effectLabel2 = new JLabel("Energy Drink: increases accuracy in first half, decreases accuracy in last half");
        JLabel effectLabel3 = new JLabel("Noise Cancelling Headphones: reduces chance of mistype");

        effectPanel.add(effectLabel1);
        effectPanel.add(effectLabel2);
        effectPanel.add(effectLabel3);
        accPanel.add(effectPanel, BorderLayout.CENTER);

        JComboBox<String> accSelect = new JComboBox<>(ACCESSORIES_OPTIONS);
        accSelect.setBackground(Color.WHITE);
        accPanel.add(accSelect);
        customisePanel.add(accPanel);
        return accSelect;
    }

    public static void main(String[] args)
    {
        RaceConfigGUI race = new RaceConfigGUI();
        race.showRaceConfig();
    }
}