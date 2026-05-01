import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 * A typing race graphical user interface. Players get to configure the race, choose customisables and race.
 * 
 * @author Zahra Bint Afzal Asghar
 * @version 1.0
 */

public class TypingRaceGUI
{
    private String passageSelected;
    private int passageLength;
    private final int seatCount;
    private final String[] difficultyModifier;
    private TypistGUI[] typistList;
    private JLabel[] typistLabelArray;
    private final JPanel cards;
    private int turns = 0;
    private JPanel racePanel;

    // Accuracy thresholds for mistype and burnout events
    private final double MISTYPE_BASE_CHANCE = 0.2;
    private int SLIDE_BACK_AMOUNT = 2;
    private final int BURNOUT_DURATION = 3;
    private boolean caffeineMode = false;

    /**
     * Constructor for objects of Class TypingRaceGUI
     * Sets up the race with a passage of chosen length and applies difficulty modifiers.
     * 
     * @param passageSelected represents the passage length chosen by user
     * @param seatCount represents the number of typists (2-6)
     * @param difficultyModifiersChosen holds all difficulty modifiers chosen for the race
     */
    public TypingRaceGUI(String passageSelected, int seatCount, String[] difficultyModifiersChosen, String customPassage, JPanel cardPanel){
        selectPassage(passageSelected, customPassage);
        this.seatCount = seatCount;
        this.difficultyModifier = difficultyModifiersChosen;
        this.cards = cardPanel;
    }

    /**
     * Sets the passage length and passage for the race
     * 
     * @param chosenPassage the passage length chosen by user (short, medium, long, custom).
     */
    private void selectPassage(String chosenPassage, String customPassage){
        String shortPassage = "Programming";
        String mediumPassage = "Computer Programming";
        String longPassage = "Object Oriented Programming";
        if(chosenPassage.equals("Short")){
            passageLength = 11;
            passageSelected = shortPassage;
        }else if(chosenPassage.equals("Medium")){
            passageLength = 20;
            passageSelected = mediumPassage;
        }else if(chosenPassage.equals("Long")){
            passageLength = 27;
            passageSelected = longPassage;
        }else if(chosenPassage.equals("Custom")){
            passageSelected = customPassage;
            passageLength = customPassage.length();
        }
    }

    /**
     * Applies appropriate difficulty modifier, if present in the array.
     */
    private void applyDifficultyModifier(){
        if(difficultyModifier != null){
            for(String s: difficultyModifier){
                if(s.equals("AutoCorrect")){
                    applyAutocorrect();
                }else if(s.equals("Caffeine Mode")){
                    caffeineMode = true;
                }else if(s.equals("Night Shift")){
                    applyNightShift();
                }
            }
        }
    }

    /**
     * If AutoCorrect is chosen, slide back amount is halved for all typists.
     */
    private void applyAutocorrect(){
        SLIDE_BACK_AMOUNT = SLIDE_BACK_AMOUNT / 2;
    }

    /**
     * If Night Shift is chosen, the accuracy is lower for all typists.
     */
    private void applyNightShift(){
        for(TypistGUI t: typistList){
            double oldAccuracy = t.getAccuracy();
            t.setAccuracy(oldAccuracy - 0.07);
        }
    }

    /**
     * Encapsulation method
     * Sets list of typists for the race.
     */
    public void setTypistList(TypistGUI[] typistList){
        this.typistList = typistList;
    }

    private void applyEnergyDrink(int turns){
        for(TypistGUI t: typistList){
            if(t.getAccessory().equals("Energy drink")){
                if(turns == 0){
                    t.setAccuracy(t.getAccuracy()*1.1);
                }else if(turns == passageLength/2){
                    t.setAccuracy(t.getAccuracy()*0.9);
                }
            }
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace(){
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        Highlighter.HighlightPainter currentPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        // Display each typist's name and symbol along with the passage.
        JTextPane[] paneArray = printRace();
        CardLayout cardLayout = (CardLayout)(cards.getLayout());
        cardLayout.next(cards);

        // Reset all typists to the start of the passage
        ResetAllTypists(painter, paneArray, currentPainter);
        applyDifficultyModifier(); //Applies effects of any difficulty modifiers

        // Start time of race
        long StartTime = System.nanoTime();

        Timer timer = new Timer(100, null);

        timer.addActionListener(e -> {
            //Applies energy drink effects(increase accuracy in first half, decrease in second half)
            applyEnergyDrink(turns);
            //Advances all typists by one turn
            AdvanceAllTypists(paneArray, turns);
            turns++;

             //Displays if any typist has mistyped or is burnt out
            updateTypistsUI();

            // Check if any typist has finished the passage
            boolean finished = allRaceFinishedby();

            if(finished){
                ((Timer) e.getSource()).stop();
                for(TypistGUI t: typistList){
                    if(raceFinishedBy(t)){
                        TypistGUI winner = t; 
                        double oldAccuracy = t.getAccuracy();
                        double INCREASE_ACCURACY = 1.02;
                        t.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
                        long EndTime = System.nanoTime(); //end time of race
                        long timeElapsed = EndTime - StartTime;
                        DisplayStats(timeElapsed, turns);
                        DisplayWinner(winner);
                        break;
                    }
                }
            }
        });

        timer.start();
    }

    /**
     * Checks if any typist has finished the race
     * @return boolean if a typist has finished the race
     */
    private boolean allRaceFinishedby(){
        boolean finished = false;
        for(TypistGUI t: typistList){
            if(raceFinishedBy(t)){
                finished = true;
            }
        }
        return finished;
    }

    /**
     * Resets all typists to start of passage.
     */
    private void ResetAllTypists(Highlighter.HighlightPainter painter, JTextPane[] paneArray, Highlighter.HighlightPainter currentPainter){
        for(int i=0; i<seatCount; i++){
            typistList[i].resetToStart();
            JTextPane pane = paneArray[i];
            Highlighter HL = pane.getHighlighter();
            try {
                HL.addHighlight(0, 1, painter);
                HL.addHighlight(0, 1, currentPainter);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Advances all typists
     * @param paneArray array of JTextPane which has the passage they are typing
     * @param turns number of turns so far
     */
    private void AdvanceAllTypists(JTextPane[] paneArray, int turns){
        for(int i=0; i<seatCount; i++){
            advanceTypist(typistList[i], paneArray[i], turns);
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(TypistGUI theTypist, JTextPane pane, int turns)
    {
        if(turns > 10){
            //After 10 turns, the typists no longer have a speed boost
            caffeineMode = false;
        }

        if (theTypist.isBurntOut()){
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }
        if(theTypist.getMisTyped()){
            //mistype only lasts one turn, resets at the next turn
            theTypist.setMisTyped(false);
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy()){
            theTypist.typeCharacter();
            highlightCharacter(pane, theTypist);
        }

        // Depending on keyboard style, the chance of mistyping is affected.
        double mistypeChance = calculateMistypeChance(theTypist);

        // Mistype check — the probability should reflect the typist's accuracy
        if ((Math.random() < mistypeChance)&&(!theTypist.isBurntOut())){
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setMisTyped(true);
            removePrevHighlights(theTypist, pane);
        }

        //Depending on typing style, the chance of burning out is affected.
        double burnoutChance = calculateBurnoutChance(theTypist);

        // Burnout check — pushing too hard increases burnout risk
        if ((Math.random() < burnoutChance)&&(!theTypist.getMisTyped())){
            if(theTypist.getAccessory().equals("Wrist Support")){
                theTypist.burnOut(BURNOUT_DURATION - 1);
                theTypist.increaseBurnout();
            }else{
                theTypist.burnOut(BURNOUT_DURATION);
                theTypist.increaseBurnout();
            }
        }

        // Wait for some milliseconds, depending on keyboard style.
        if(theTypist.keyboardStyle.equals("Ergonomic")){
            if(caffeineMode == false){
                try {
                    TimeUnit.MILLISECONDS.sleep(60);
                } catch (Exception e) {}
            }
            else{
                try {
                    TimeUnit.MILLISECONDS.sleep(40);
                } catch (Exception e) {}
            }
        }else if(theTypist.keyboardStyle.equals("Touch Screen") && caffeineMode == false){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {}
        }else{
            try {
                    TimeUnit.MILLISECONDS.sleep(80);
                } catch (Exception e) {}
        }
    }

    /**
     * Calculates chance of a typist mistyping
     * Multiplies accuracy by the base chance
     * Depending on keyboard style or accessory, the chance is affected
     * 
     * @param theTypist current typist 
     */
    private double calculateMistypeChance( TypistGUI theTypist){
        double mistypeChance = theTypist.getAccuracy() * MISTYPE_BASE_CHANCE;
        if(theTypist.getKeyboardStyle().equals("Ergonomic")){
            mistypeChance = mistypeChance * 0.8;
        }else if(theTypist.getKeyboardStyle().equals("Touch Screen")){
            mistypeChance = mistypeChance * 1.1;
        }

        if(theTypist.getAccessory().equals("Noise cancelling headphones")){
            mistypeChance = mistypeChance * 0.9;
        }
        return mistypeChance;
    }

    /**
     * Calculates chance of typist burning out
     * Capped at 0.05
     * Depending on typing style the chance is affected
     * 
     * @param theTypist current typist
     */
    private double calculateBurnoutChance(TypistGUI theTypist){
        double burnoutChance = 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy();
        if(theTypist.getTypingStyle().equals("Touch Typing")){
            burnoutChance = burnoutChance * 1.3;
        }
        else if(theTypist.getTypingStyle().equals("Phone Thumbs")){
            burnoutChance = burnoutChance * 1.1;
        }
        return burnoutChance;
    }

    /**
     * Highlights a correct character in green.
     * 
     * @param pane Holds the passage for current typist
     * @param theTypist current typist
     */
    private void highlightCharacter(JTextPane pane, TypistGUI theTypist){
        Highlighter HL = pane.getHighlighter();
        Highlighter.Highlight[] highlights = HL.getHighlights();
        int index = theTypist.getProgress();
        try {
            HL.changeHighlight(highlights[0], 0, index+1);
            HL.changeHighlight(highlights[1], index+1, index+2);
        } catch (Exception e) {
        }
    }

    /**
     * Removes highlight of characters to show slide back.
     */
    private void removePrevHighlights(TypistGUI theTypist, JTextPane pane){
        Highlighter HL = pane.getHighlighter();
        Highlighter.Highlight[] highlights = HL.getHighlights();
        int index = theTypist.getProgress();
        try {
            HL.changeHighlight(highlights[0], 0, index);
            HL.changeHighlight(highlights[1], index, index+1);
        } catch (Exception e) {
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(TypistGUI theTypist)
    {
        return theTypist.getProgress() >= passageLength;
    }

    /**
     * UI for the race
     * Adds all typists and passage to the screen 
     */
    public JTextPane[] printRace(){
        JTextPane[] paneArray = new JTextPane[seatCount];
        typistLabelArray = new JLabel[seatCount];
        racePanel = new JPanel();
        racePanel.setBorder(new EmptyBorder(30,30,30,30));
        racePanel.setBackground(new Color(247, 237, 255));
        racePanel.setSize(600, 500);
        cards.add(racePanel, "Panel3");
        BoxLayout boxLayoutManager = new BoxLayout(racePanel, BoxLayout.Y_AXIS);
        racePanel.setLayout(boxLayoutManager);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(247, 237, 255));
        JLabel titleLabel = new JLabel("Race!");
        titleLabel.setForeground(new Color(124, 90, 174));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));
        titlePanel.add(titleLabel);
        racePanel.add(titlePanel);
        
        for(int i=0; i<seatCount; i++){
            JPanel typistPanel = new JPanel(new FlowLayout());
            typistPanel.setOpaque(false);
            JLabel typistName = new JLabel(typistList[i].getName() +" " + typistList[i].getSymbol());
            typistName.setFont(new Font("Arial", Font.BOLD, 25));
            typistName.setForeground(typistList[i].getColour());
            typistLabelArray[i] = typistName;
            typistPanel.add(typistName);
            JTextPane passage = new JTextPane();
            passage.setText(passageSelected);
            passage.setEditable(false);
            passage.setMaximumSize(passage.getPreferredSize());
            passage.setBorder(new EmptyBorder(10,10,10,10));;
            typistPanel.add(passage);
            paneArray[i] = passage;
            racePanel.add(typistPanel);
        }

        racePanel.revalidate();
        racePanel.repaint();
        cards.revalidate();
        cards.repaint();
        return paneArray;
    }

    /**
     * Displays whether the typist has mistyped or burnt out in red colour each turn.
     */
    private void updateTypistsUI(){
        for(int i=0; i <seatCount; i++){
            TypistGUI typist = typistList[i];
            if(typist.getMisTyped()){
                typistLabelArray[i].setText(typist.getName() + " MISTYPED");
                typistLabelArray[i].setForeground(Color.RED);
            }else if(typist.isBurntOut()){
                typistLabelArray[i].setText(typist.getName() + " BURNT OUT " + typist.getBurnoutTurnsRemaining() + " TURNS LEFT");
                typistLabelArray[i].setForeground(Color.RED);
            }
            else{
                typistLabelArray[i].setText(typist.getName() +" " + typist.getSymbol());
                typistLabelArray[i].setForeground(typist.getColour());
            }
        }
    }

    /**
     * Displays each typist's name, WPM, accuracy percentage, number of burnouts, accuracy change after every race in a table.
     * 
     * @param timeElapsed the amount of time the race took in nanoseconds
     * @param turns the total number of turns of the race. 
     */
    private void DisplayStats(long timeElapsed, int turns){
        JTabbedPane statsTabs = new JTabbedPane();
        JPanel raceStatsPanel = new JPanel();
        raceStatsPanel.setOpaque(false);
        Object[] columnNames = {"Name", "WPM", "Accuracy Percentage", "Burnout Count", "Accuracy Change"};
        Object[][] tableData = new Object[seatCount][5];

        for(int i=0; i<seatCount; i++){
            TypistGUI typist = typistList[i];
            String name = typist.getName();
            Integer WPM = calculateWPM(typist, timeElapsed);
            Integer burnoutCount = typist.getNumBurnout();
            Double newAccuracy = typist.getAccuracy();
            Integer accuracyPercent = calcAccuracyPercent(turns, typist);
            Object[] typistStats = {name, WPM, accuracyPercent, burnoutCount, newAccuracy};
            tableData[i] = typistStats;
        }
        JTable statsTable = new JTable(tableData, columnNames);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScroll = new JScrollPane(statsTable);
        JTableHeader tableHeader = statsTable.getTableHeader();
        tableHeader.setBackground(new Color(85, 57, 128));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Arial", Font.PLAIN, 15));
        statsTable.setFillsViewportHeight(true);
        raceStatsPanel.setLayout(new BorderLayout());
        raceStatsPanel.add(statsTable.getTableHeader(), BorderLayout.PAGE_START);
        raceStatsPanel.add(statsTable, BorderLayout.CENTER);
        statsTabs.addTab("Race stats", raceStatsPanel); 
        cards.add(statsTabs, "panel4");  
    }

    /**
     * Calculates words per minute for a typist
     * 
     * @param theTypist the typist whose WPM is calculated
     * @param timeElapsed time the race took in nanoseconds
     */
    private int calculateWPM(TypistGUI theTypist, long timeElapsed){
        double seconds = timeElapsed/1000000000;
        double CPS = theTypist.getProgress()/seconds; //characters per second
        double WPM = CPS * 12;
        return (int) WPM;
    }

    /**
     * Calculates the accuracy percentage of a typist
     * 
     * @param turns total turns of the race
     */
    private int calcAccuracyPercent(int turns, TypistGUI theTypist){
        int correctTypes = theTypist.getProgress(); //number of correct characters
        double accuracyPercent = ((float) correctTypes / turns) * 100;
        int percentage = (int) accuracyPercent;
        return percentage;
    }

    /**
     * Displays the winner of the race when it is finished
     * 
     * @param winner typist who won the race
     */
    private void DisplayWinner(TypistGUI winner){
        JPanel winnerPanel = new JPanel();
        winnerPanel.setOpaque(false);
        JLabel winnerLabel = new JLabel("The winner is ... " + winner.getName());
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        winnerLabel.setForeground(Color.RED);
        winnerPanel.add(winnerLabel);
        racePanel.add(winnerPanel);

        JButton statsButton = new JButton("View Stats");
        statsButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                CardLayout cardLayout = (CardLayout)(cards.getLayout());
                cardLayout.next(cards);
            }
        });
        racePanel.add(statsButton);
        racePanel.revalidate();
        racePanel.repaint();
    }
}