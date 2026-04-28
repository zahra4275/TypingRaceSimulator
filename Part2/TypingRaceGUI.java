import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 * A typing race graphical user interface. Players get to configure the race, choose customisables and race.
 * 
 * @author Zahra Bint Afzal Asghar
 * @version 0.1
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

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace(){
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
        Highlighter.HighlightPainter currentPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
        boolean finished = false;
        TypistGUI winner = null;
        double oldAccuracy = 0.0;
        final double INCREASE_ACCURACY = 1.02;
        int turns = 0;
        long EndTime = 0;
        double winnerWPM = 0;

        // Display each typist's name and symbol along with the passage.
        JTextPane[] paneArray = printRace();
        CardLayout cardLayout = (CardLayout)(cards.getLayout());
        cardLayout.next(cards);

        // Reset all typists to the start of the passage
        ResetAllTypists(painter, paneArray, currentPainter);
        System.out.println("reset");
        applyDifficultyModifier();
        System.out.println("applied modifiers");


        // Start timer
        long StartTime = System.nanoTime();

        while(!finished)
        {
            turns++;
            // Advance each typist by one turn
            AdvanceAllTypists(paneArray, turns);

            // Check if any typist has finished the passage
            for(TypistGUI t: typistList){
                if(raceFinishedBy(t)){
                finished = true;
                winner = t; 
                oldAccuracy = t.getAccuracy();
                t.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
                EndTime = System.nanoTime();
                winnerWPM = calculateWinnerWPM(StartTime, EndTime);
                }
            }
        }
        long timeElapsed = EndTime - StartTime;
        DisplayStats(timeElapsed, turns);
        System.out.println("Winner: " + winnerWPM + " WPM");
        System.out.println("And the Winner is... " + winner.getName());
        System.out.println("Final accuracy: " + winner.getAccuracy() + " (improved from " + oldAccuracy + " )");
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

        // double typeChance = Math.random();
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

        //Displays if any typist has mistyped or is burnt out
        updateTypistsUI();

        // Wait for some milliseconds, depending on keyboard style.
        if(theTypist.keyboardStyle.equals("Ergonomic")){
            if(caffeineMode == false){
                try {
                    TimeUnit.MILLISECONDS.sleep(160);
                } catch (Exception e) {}
            }
            else{
                try {
                    TimeUnit.MILLISECONDS.sleep(140);
                } catch (Exception e) {}
            }
        }else if(theTypist.keyboardStyle.equals("Touch Screen") && caffeineMode == false){
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {}
        }else{
            try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (Exception e) {}
        }
    }


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
     * Calculates the winner's words per minute
     * Worked out from characters per second
     */
    private double calculateWinnerWPM(long StartTime, long EndTime){
        long timeElapsed = EndTime - StartTime;
        double seconds = timeElapsed/1000000000;
        double CPS = passageLength/seconds;
        double WPM = CPS * 12;
        return WPM;
    }

    /**
     * UI for the race
     * Adds all typists and passage to the screen 
     */
    public JTextPane[] printRace(){
        JTextPane[] paneArray = new JTextPane[seatCount];
        typistLabelArray = new JLabel[seatCount];
        JPanel racePanel = new JPanel();
        racePanel.setSize(600, 500);
        cards.add(racePanel, "Panel3");
        BoxLayout boxLayoutManager = new BoxLayout(racePanel, BoxLayout.Y_AXIS);
        racePanel.setLayout(boxLayoutManager);
        JLabel titleLabel = new JLabel("Race!");
        racePanel.add(titleLabel);
        
        for(int i=0; i<seatCount; i++){
            JPanel typistPanel = new JPanel(new FlowLayout());
            JLabel typistName = new JLabel(typistList[i].getName() +" " + typistList[i].getSymbol());
            typistLabelArray[i] = typistName;
            typistPanel.add(typistName);
            JTextPane passage = new JTextPane();
            passage.setText(passageSelected);
            passage.setEditable(false);
            passage.setMaximumSize(passage.getPreferredSize());
            typistPanel.add(passage);
            paneArray[i] = passage;
            racePanel.add(typistPanel);
        }

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
        cards.revalidate();
        cards.repaint();
        return paneArray;
    }

    /**
     * Displays whether the typist has mistyped or burnt out in red colour.
     */
    private void updateTypistsUI(){
        for(int i=0; i <seatCount; i++){
            TypistGUI typist = typistList[i];
            if(typist.getMisTyped()){
                typistLabelArray[i].setText(typist.getName() + " MISTYPED");
                typistLabelArray[i].setForeground(Color.red);
            }else if(typist.isBurntOut()){
                typistLabelArray[i].setText(typist.getName() + " BURNT OUT " + typist.getBurnoutTurnsRemaining() + " TURNS LEFT");
                typistLabelArray[i].setForeground(Color.red);
            }
            else{
                typistLabelArray[i].setText(typist.getName() +" " + typist.getSymbol());
                typistLabelArray[i].setForeground(Color.black);
            }
        }
    }

    /**
     * Displays each typist's name, WPM, accuracy percentage, number of burnouts, accuracy change after every race.
     * 
     * @param timeElapsed the amount of time the race took in nanoseconds
     * @param turns the total number of turns of the race. 
     */
    private void DisplayStats(long timeElapsed, int turns){
        JTabbedPane statsTabs = new JTabbedPane();
        JPanel raceStatsPanel = new JPanel();
        Object[] columnNames = {"Name", "WPM", "Accuracy Percentage", "Burnout Count", "Accuracy Change"};
        Object[][] tableData = new Object[seatCount][5];

        for(int i=0; i<seatCount; i++){
            TypistGUI typist = typistList[i];
            String name = typist.getName();
            Integer WPM = calculateWPM(typist, timeElapsed);
            Integer burnoutCount = typist.getNumBurnout();
            Double newAccuracy = typist.getAccuracy();
            Integer accuracyPercent = calcAccuracyPercent(turns, typist);
            Object[] typistStats = {name, WPM, newAccuracy, burnoutCount, accuracyPercent};
            tableData[i] = typistStats;
        }
        JTable statsTable = new JTable(tableData, columnNames);
        JScrollPane tableScroll = new JScrollPane(statsTable);
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

    // public static void main(String[] args) {
        // TypistGUI t1 = new TypistGUI('@', "player1", "Touch Typing", "Mechanical", Color.black, "Wrist support", 0.85);
        // TypistGUI t2 = new TypistGUI('!', "player2", "Touch Typing", "Mechanical", Color.black, "Wrist support", 0.5);
        // TypistGUI[] playersArray = {t1, t2};
        // String[] diffArray = {"Autocorrect", "Night Shift"};
        // TypingRaceGUI race = new TypingRaceGUI("Short", 2, diffArray, null);
        // race.setTypistList(playersArray);
        // race.startRace();
    // }
}