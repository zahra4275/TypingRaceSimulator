import java.awt.CardLayout;
import java.awt.Color;
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
    private JFrame configFrame;
    private JPanel cardPanel; // Panel for card layout that switches from race configuration to customising typists
    // private TypingRaceGUI race; // Starts the race
    // private TypistGUI[] TypistList; // List of typists
    private int seatCount;
    private JPanel configPanel;
    private int pageIndex = 0; //Keeps track of how many times to reset the customise typist page.


    // Constructor
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
        BoxLayout boxLayoutManager = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
        configPanel.setLayout(boxLayoutManager);

        JLabel titleLabel = new JLabel("Race Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));
        configPanel.add(titleLabel);

        // Option to choose passage length from pre-defined list.
        JComboBox selectedPassage = choosePassage(configPanel, PASSAGE_OPTIONS);

        JLabel customPassageLabel = new JLabel("If chose custom, enter you custom passage:");
        customPassageLabel.setBorder(new EmptyBorder(10,0,10,0));
        customPassageLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        configPanel.add(customPassageLabel);
        JTextArea customPassageField = new JTextArea(5,50);
        configPanel.add(customPassageField);

        // Option to choose how many typists from a slider (min-2, max-6).
        JSlider seatSlider = chooseSeatCount(configPanel);

        // Option to choose difficulty modifiers, can choose more than one option.
        JCheckBox[] diffArray = chooseDiffModifiers(configPanel);

        JButton nextButton = new JButton("Next");
        configPanel.add(nextButton);
        nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String chosenPassage = selectedPassage.getSelectedItem().toString();
                if(chosenPassage.equals("Custom")){
                    String customPassage = customPassageField.getText();
                }
                int ChosenseatCount = seatSlider.getValue();
                for(JCheckBox b: diffArray){
                    if(b.isSelected()){
                        String difficultyChosen = b.getText();
                    }
                }
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
        JLabel passageLabel = new JLabel("Choose passage length:");
        passageLabel.setBorder(new EmptyBorder(10,0,10,0));
        passageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(passageLabel);
        JComboBox<String> passageSelect = new JComboBox<>(PASSAGE_OPTIONS);
        passageSelect.setBackground(Color.WHITE);
        configPanel.add(passageSelect);
        return passageSelect;
    }

    /**
     * Displays a slider so user can choose how many typists. Users can only choose one option.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private JSlider chooseSeatCount(JPanel configPanel){
        JLabel countLabel = new JLabel("How many typists?");
        countLabel.setBorder(new EmptyBorder(10,0,10,0));
        countLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(countLabel);
        JSlider seatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        seatSlider.setMaximum(6);
        seatSlider.setMinimum(2);
        seatSlider.setMajorTickSpacing(2);
        seatSlider.setMinorTickSpacing(1);
        seatSlider.setPaintLabels(true);
        seatSlider.setPaintTicks(true);
        seatSlider.setValue(2);
        configPanel.add(seatSlider);
        return seatSlider;
    }

    /**
     * Displays the options for choosing difficulty modifiers. Users can choose multiple options.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private JCheckBox[] chooseDiffModifiers(JPanel configPanel){
        JLabel difficultyLabel = new JLabel("Choose difficulty modifiers:");
        difficultyLabel.setBorder(new EmptyBorder(10,0,10,0));
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(difficultyLabel);

        JCheckBox diffOption1 = new JCheckBox("AutoCorrect: halves slide back amount");
        JCheckBox diffOption2 = new JCheckBox("Caffeine Mode: All typists gain a temporary speed boost, followed by increased burnout");
        JCheckBox diffOption3 = new JCheckBox("Night Shift: All typists accuracy are slightly reduced");
        configPanel.add(diffOption1);
        configPanel.add(diffOption2);
        configPanel.add(diffOption3);
        JCheckBox[] boxArray = {diffOption1, diffOption2, diffOption3};
        return boxArray;
    }

    /**
     * UI for customising typists 
     */
    private void showCustomiseTypist(){
        final String[] TYPING_STYLE_OPTIONS = {"None","Touch Typing", "Phone Thumbs", "HuntNPeck"};
        final String[] KEYBOARD_OPTIONS = {"None" ,"Ergonomic", "Mechanical", "Touch Screen"};
        final String[] SYMBOL_OPTIONS = {"None" ,"①", "②", "③", "④", "⑤", "⑥"};
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
        JComboBox symbolBox = chooseSymbol(customisePanel, SYMBOL_OPTIONS);

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
                    String symbolChosen = symbolBox.getSelectedItem().toString();                
                    Color colourChosen = colourChooser.getColor();
                    String accChosen = accessoryBox.getSelectedItem().toString();

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
    public void resetAllFields(JComboBox typingStyleBox, JComboBox keyboardBox, JComboBox symbolBox, JColorChooser colourChooser, JComboBox accessoryBox){
        typingStyleBox.setSelectedIndex(0);
        keyboardBox.setSelectedIndex(0);
        symbolBox.setSelectedIndex(0);
        colourChooser.setColor(Color.WHITE);
        accessoryBox.setSelectedIndex(0);
    }

    /**
     * Displays text area to enter typist's name.
     */
    private JTextField enterTypistName(JPanel customisePanel){
        JLabel nameLabel = new JLabel("Enter typist name");
        nameLabel.setBorder(new EmptyBorder(10,0,10,0));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(nameLabel);

        JTextField nameField = new JTextField();
        customisePanel.add(nameField);
        return nameField;
    }

    /**
     * Displays options for typing style. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all options for typing style.
     */
    private JComboBox chooseTypingStyle(JPanel customisePanel, String[] typingStyleOptions){
        JLabel typingLabel = new JLabel("Choose typing style:");
        typingLabel.setBorder(new EmptyBorder(10,0,10,0));
        typingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        JLabel effectLabel1 = new JLabel("Touch Typing: Higher accuracy, high chance of burnout");
        JLabel effectLabel2 = new JLabel("Phone Thumbs: Low accuracy, medium chance of burnout");
        JLabel effectLabel3 = new JLabel("HuntNPeck: high accuracy, low chance of burnout");
        
        customisePanel.add(typingLabel);
        customisePanel.add(effectLabel1);
        customisePanel.add(effectLabel2);
        customisePanel.add(effectLabel3);

        JComboBox<String> styleSelect = new JComboBox<>(typingStyleOptions);
        styleSelect.setBackground(Color.WHITE);
        customisePanel.add(styleSelect);
        return styleSelect;
    }

    /**
     * Displays options for keyboard type. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all keyboard types, the user can choose.
     */
    private JComboBox chooseKeyboardType(JPanel customisePanel, String[] KEYBOARD_OPTIONS){
        JLabel keyboardLabel = new JLabel("Choose your keyboard type:");
        keyboardLabel.setBorder(new EmptyBorder(10,0,10,0));
        keyboardLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        JLabel effectLabel1 = new JLabel("Ergonomic: Low chance of mistype (fastest)");
        JLabel effectLabel2 = new JLabel("Mechanical: medium chance of mistype ");
        JLabel effectLabel3 = new JLabel("Touchscreen: high chance of mistype (slowest)");

        customisePanel.add(keyboardLabel);
        customisePanel.add(effectLabel1);
        customisePanel.add(effectLabel2);
        customisePanel.add(effectLabel3);

        JComboBox<String> keyboardSelect = new JComboBox<>(KEYBOARD_OPTIONS);
        keyboardSelect.setBackground(Color.WHITE);
        customisePanel.add(keyboardSelect);
        return keyboardSelect;
    }

    /**
     * Displays options for typist's symbol. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all the symbols the user can choose from.
     */
    private JComboBox chooseSymbol(JPanel customisePanel, String[] SYMBOL_OPTIONS){
        JLabel symbolLabel = new JLabel("Choose your symbol:");
        symbolLabel.setBorder(new EmptyBorder(10,0,10,0));
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(symbolLabel);

        JComboBox<String> symbolSelect = new JComboBox<>(SYMBOL_OPTIONS);
        symbolSelect.setBackground(Color.WHITE);
        customisePanel.add(symbolSelect);
        return symbolSelect;
    }

    /**
     * Displays options for colour.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     */
    private JColorChooser chooseColour(JPanel customisePanel){
        JLabel colourLabel = new JLabel("Choose your colour:");
        colourLabel.setBorder(new EmptyBorder(10,0,10,0));
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(colourLabel);

        JColorChooser chooseColour = new JColorChooser();
        chooseColour.setPreviewPanel(new JPanel());
        AbstractColorChooserPanel[] colorPanels = chooseColour.getChooserPanels();
        for(AbstractColorChooserPanel p: colorPanels){
            if(!p.getDisplayName().equals("Swatches")){
                chooseColour.removeChooserPanel(p);
            }
        }
        customisePanel.add(chooseColour);

        return chooseColour;
    }

    /**
     * Displays options for accessories. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all accessories the user can choose from.
     */
    private JComboBox chooseAccessory(JPanel customisePanel, String[] ACCESSORIES_OPTIONS){
        JLabel accLabel = new JLabel("Choose your accessories:");
        accLabel.setBorder(new EmptyBorder(10,0,10,0));
        accLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        JLabel effectLabel1 = new JLabel("Wrist Support: reduces burnout duration");
        JLabel effectLabel2 = new JLabel("Energy Drink: increases accuracy in first half, decreases accuracy in last half");
        JLabel effectLabel3 = new JLabel("Noise Cancelling Headphones: reduces chance of mistype");

        customisePanel.add(accLabel);
        customisePanel.add(effectLabel1);
        customisePanel.add(effectLabel2);
        customisePanel.add(effectLabel3);

        JComboBox<String> accSelect = new JComboBox<>(ACCESSORIES_OPTIONS);
        accSelect.setBackground(Color.WHITE);
        customisePanel.add(accSelect);
        return accSelect;
    }

    public static void main(String[] args)
    {
        RaceConfigGUI race = new RaceConfigGUI();
        race.showRaceConfig();
    }
}