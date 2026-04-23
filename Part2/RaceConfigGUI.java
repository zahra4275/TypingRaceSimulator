import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

    public RaceConfigGUI()
    {
        this.configFrame = new JFrame("Typing Race");
        configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configFrame.setSize(600,500);
        this.cardPanel = new JPanel(new CardLayout());
        configFrame.add(cardPanel);
        configFrame.setVisible(true);
    }

    /**
     * UI design for race configuration options. Choosing passage length, how many typists and difficulty modifiers.
     */
    private void showRaceConfig()
    {
        final String[] PASSAGE_OPTIONS = {"Short", "Medium", "Long", "Customised"};
        JPanel configPanel = new JPanel();
        configPanel.setBorder(new EmptyBorder(30,30,30,30));
        BoxLayout boxLayoutManager = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
        configPanel.setLayout(boxLayoutManager);

        JLabel titleLabel = new JLabel("Race Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(10,0,10,0));
        configPanel.add(titleLabel);

        // Option to choose passage length from pre-defined list.
        choosePassage(configPanel, PASSAGE_OPTIONS);

        // Option to choose how many typists from a slider (max-6, min-2).
        chooseSeatCount(configPanel);

        // Option to choose difficulty modifiers, can choose more than one option.
        chooseDiffModifiers(configPanel);

        JButton nextButton = new JButton("Next");
        configPanel.add(nextButton);
        nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                CardLayout cardLayout = (CardLayout)(cardPanel.getLayout());
                cardLayout.next(cardPanel);
            }
        });

        // Adds the race configuration panel to the panel that has card layout.
        cardPanel.add(configPanel, "panel1");
        showCustomiseTypist();

    }

    /**
     * Displays the options for passage selection using a comboBox. Users can only choose one option.
     * 
     * @param configPanel the panel that will display the race configuration options.
     * @param PASSAGE_OPTIONS Array of options for passage length.
     */
    private void choosePassage(JPanel configPanel, String[] PASSAGE_OPTIONS){
        JLabel passageLabel = new JLabel("Choose passage length:");
        passageLabel.setBorder(new EmptyBorder(10,0,10,0));
        passageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(passageLabel);
        JComboBox<String> passageSelect = new JComboBox<>(PASSAGE_OPTIONS);
        passageSelect.setBackground(Color.WHITE);
        configPanel.add(passageSelect);
        
    }

    /**
     * Displays a slider so user can choose how many typists. Users can only choose one option.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private void chooseSeatCount(JPanel configPanel){
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
        configPanel.add(seatSlider);
    }

    /**
     * Displays the options for choosing difficulty modifiers. Users can choose multiple options.
     * 
     * @param configPanel the panel that will display the race configuration options.
     */
    private void chooseDiffModifiers(JPanel configPanel){
        JLabel difficultyLabel = new JLabel("Choose difficulty modifiers:");
        difficultyLabel.setBorder(new EmptyBorder(10,0,10,0));
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(difficultyLabel);
        JCheckBox diffOption1 = new JCheckBox("AutoCorrect");
        JCheckBox diffOption2 = new JCheckBox("Caffeine mode");
        JCheckBox diffOption3 = new JCheckBox("Night Shift");
        configPanel.add(diffOption1);
        configPanel.add(diffOption2);
        configPanel.add(diffOption3);
    }

    /**
     * UI for customising typists 
     */
    private void showCustomiseTypist(){
        final String[] TYPING_STYLE_OPTIONS = {"Touch Typing", "Phone Thumbs", "Voice-to-Text"};
        final String[] KEYBOARD_OPTIONS = {"Ergonomic", "Mechanical", "Touch Screen"};
        final String[] SYMBOL_OPTIONS = {"①", "②", "③", "④", "⑤", "⑥"};
        final String[] ACCESSORY_OPTIONS ={"Wrist support", "Energy drink", "Noise cancelling headphones"};
        JPanel customisePanel = new JPanel();
        customisePanel.setBorder(new EmptyBorder(30,30,30,30));
        BoxLayout boxLayoutManager = new BoxLayout(customisePanel, BoxLayout.Y_AXIS);
        customisePanel.setLayout(boxLayoutManager);
        JScrollPane panelScrollPane = new JScrollPane(customisePanel);

        JLabel titleLabel = new JLabel("Customise Typists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(10,0,10,0));
        customisePanel.add(titleLabel);

        // Displays typing style options
        chooseTypingStyle(customisePanel, TYPING_STYLE_OPTIONS);

        // Displays keyboard options
        chooseKeyboardType(customisePanel, KEYBOARD_OPTIONS);

        // Displays symbol options
        chooseSymbol(customisePanel, SYMBOL_OPTIONS);

        // Displays colour options
        chooseColour(customisePanel);

        // Display accessory options
        chooseAccessory(customisePanel, ACCESSORY_OPTIONS);

        // JButton nextButton = new JButton("Next");
        // customisePanel.add(nextButton);

        cardPanel.add(panelScrollPane, "panel2");

    }

    /**
     * Displays options for typing style. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all options for typing style.
     */
    private void chooseTypingStyle(JPanel customisePanel, String[] typingStyleOptions){
        JLabel typingLabel = new JLabel("Choose typing style:");
        typingLabel.setBorder(new EmptyBorder(10,0,10,0));
        typingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(typingLabel);

        JComboBox<String> styleSelect = new JComboBox<>(typingStyleOptions);
        styleSelect.setBackground(Color.WHITE);
        customisePanel.add(styleSelect);
    }

    /**
     * Displays options for keyboard type. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all keyboard types, the user can choose.
     */
    private void chooseKeyboardType(JPanel customisePanel, String[] KEYBOARD_OPTIONS){
        JLabel keyboardLabel = new JLabel("Choose your keyboard type:");
        keyboardLabel.setBorder(new EmptyBorder(10,0,10,0));
        keyboardLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(keyboardLabel);

        JComboBox<String> keyboardSelect = new JComboBox<>(KEYBOARD_OPTIONS);
        keyboardSelect.setBackground(Color.WHITE);
        customisePanel.add(keyboardSelect);
    }

    /**
     * Displays options for typist's symbol. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all the symbols the user can choose from.
     */
    private void chooseSymbol(JPanel customisePanel, String[] SYMBOL_OPTIONS){
        JLabel symbolLabel = new JLabel("Choose your symbol:");
        symbolLabel.setBorder(new EmptyBorder(10,0,10,0));
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(symbolLabel);

        JComboBox<String> symbolSelect = new JComboBox<>(SYMBOL_OPTIONS);
        symbolSelect.setBackground(Color.WHITE);
        customisePanel.add(symbolSelect);
    }

    /**
     * Displays options for colour.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     */
    private void chooseColour(JPanel customisePanel){
        JLabel colourLabel = new JLabel("Choose your colour:");
        colourLabel.setBorder(new EmptyBorder(10,0,10,0));
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(colourLabel);

        JButton colourButton = new JButton("Choose a colour");
        colourButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Color chosenColour = JColorChooser.showDialog(null, "Pick a colour",  Color.WHITE);
            }
        });

        customisePanel.add(colourButton);

    }

    /**
     * Displays options for accessories. Users can only choose one option.
     * 
     * @param customisePanel panel that holds all components for customising typists.
     * @param typingStyleOptions array that holds all accessories the user can choose from.
     */
    private void chooseAccessory(JPanel customisePanel, String[] ACCESSORIES_OPTIONS){
        JLabel accLabel = new JLabel("Choose your accessories:");
        accLabel.setBorder(new EmptyBorder(10,0,10,0));
        accLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        customisePanel.add(accLabel);

        JComboBox<String> accSelect = new JComboBox<>(ACCESSORIES_OPTIONS);
        accSelect.setBackground(Color.WHITE);
        customisePanel.add(accSelect);
    }

    public static void main(String[] args)
    {
        RaceConfigGUI race = new RaceConfigGUI();
        race.showRaceConfig();
    }
}