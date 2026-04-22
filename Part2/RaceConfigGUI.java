import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
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

    private final String[] PASSAGE_OPTIONS = {"Short", "Medium", "Long", "Customised"};
    // private final String[] DIFFICULTY_OPTIONS = {"Autocorrect", "Caffeine mode" ,"Night shift"};

    public RaceConfigGUI()
    {
        this.configFrame = new JFrame("Typing Race");
        configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configFrame.setSize(600,500);
        configFrame.setVisible(true);
        this.cardPanel = new JPanel(new CardLayout());
        configFrame.add(cardPanel);
    }

    /**
     * UI design for race configuration options. Choosing passage length, how many typists and difficulty modifiers.
     */
    private void showRaceConfig()
    {
        JPanel configPanel = new JPanel();
        configPanel.setBorder(new EmptyBorder(30,30,30,30));
        BoxLayout boxLayoutManager = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
        configPanel.setLayout(boxLayoutManager);

        // Option to choose passage length from pre-defined list.
        JLabel passageLabel = new JLabel("Choose passage length");
        passageLabel.setBorder(new EmptyBorder(10,0,10,0));
        passageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(passageLabel);
        JComboBox<String> passageSelect = new JComboBox<>(PASSAGE_OPTIONS);
        passageSelect.setBackground(Color.WHITE);
        configPanel.add(passageSelect);

        // Option to choose how many typists from a slider (max-6, min-2).
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

        // Option to choose difficulty modifiers, can choose more than one option.
        JLabel difficultyLabel = new JLabel("Choose difficulty modifiers.");
        difficultyLabel.setBorder(new EmptyBorder(10,0,10,0));
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        configPanel.add(difficultyLabel);
        JCheckBox diffOption1 = new JCheckBox("AutoCorrect");
        JCheckBox diffOption2 = new JCheckBox("Caffeine mode");
        JCheckBox diffOption3 = new JCheckBox("Night Shift");
        configPanel.add(diffOption1);
        configPanel.add(diffOption2);
        configPanel.add(diffOption3);

        // Adds the race configuration panel to the panel that has card layout.
        cardPanel.add(configPanel);

    }

    public static void main(String[] args)
    {
        RaceConfigGUI race = new RaceConfigGUI();
        race.showRaceConfig();
    }
}