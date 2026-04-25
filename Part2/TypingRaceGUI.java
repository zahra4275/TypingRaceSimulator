import java.awt.CardLayout;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

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
    private JFrame frame;
    private CardLayout cards;

    // Accuracy thresholds for mistype and burnout events
    private final double MISTYPE_BASE_CHANCE = 0.3;
    private int SLIDE_BACK_AMOUNT = 2;
    private final int BURNOUT_DURATION = 3;
    private double BURNOUT_RISK = 0.3;

    /**
     * Constructor for objects of Class TypingRaceGUI
     * Sets up the race with a passage of chosen length and applies difficulty modifiers.
     * 
     * @param passageSelected represents the passage length chosen by user
     * @param seatCount represents the number of typists (min-2, max-6)
     * @param difficultyModifiersChosen holds all difficulty modifiers chosen for the race
     */
    public TypingRaceGUI(String passageSelected, int seatCount, String[] difficultyModifiersChosen){
        selectPassage(passageSelected);
        this.seatCount = seatCount;
        this.difficultyModifier = difficultyModifiersChosen;
        applyDifficultyModifier();
    }

    /**
     * Sets the passage length and passage for the race
     * 
     * @param chosenPassage the passage length chosen by user (short, medium, long, custom).
     */
    private void selectPassage(String chosenPassage){
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
        }
    }

    /**
     * Applies appropriate difficulty modifier, if present in the array.
     */
    private void applyDifficultyModifier(){
        for(String s: difficultyModifier){
            if(s.equals("AutoCorrect")){
                applyAutocorrect();
            }else if(s.equals("Caffeine Mode")){
                applyCaffeineMode();
            }else if(s.equals("Night Shift")){
                applyNightShift();
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
     * If Caffeine mode is chosen, the risk of burnout is lower.
     */
    private void applyCaffeineMode(){
        BURNOUT_RISK = 0.2;
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
    public void startRace()
    {
        boolean finished = false;
        TypistGUI winner = null;
        double oldAccuracy = 0.0;
        final double INCREASE_ACCURACY = 1.02;

        // Reset all typists to the start of the passage
        ResetAllTypists();

        while (!finished)
        {
            // Advance each typist by one turn
            AdvanceAllTypists();

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            for(TypistGUI t: typistList){
                if(raceFinishedBy(t)){
                finished = true;
                winner = t; 
                oldAccuracy = t.getAccuracy();
                t.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
                }
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        System.out.println("And the Winner is... " + winner.typistName);
        System.out.println("Final accuracy: " + winner.getAccuracy() + " (improved from " + oldAccuracy + " )");

    }

    /**
     * Resets all typists to start of passage.
     */
    private void ResetAllTypists(){
        for(TypistGUI t: typistList){
            t.resetToStart();
        }
    }

    private void AdvanceAllTypists(){
        for(TypistGUI t: typistList){
            advanceTypist(t);
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
    private void advanceTypist(TypistGUI theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }
        if(theTypist.misTyped){

            //mistype only lasts one turn, so it resets at the next turn
            theTypist.setMisTyped(false);
        }

        double typeChance = Math.random();
        // Attempt to type a character
        if (typeChance < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        double mistypeChance = theTypist.getAccuracy() * MISTYPE_BASE_CHANCE;
        if(theTypist.getKeyboardStyle().equals("Ergonomic")){
            mistypeChance = mistypeChance * 0.8;
        }else if(theTypist.getKeyboardStyle().equals("Touch Screen")){
            mistypeChance = mistypeChance * 1.1;
        }

        if ((typeChance < mistypeChance)&&(!theTypist.isBurntOut()))
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setMisTyped(true);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        double burnoutChance = 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy();
        if(theTypist.getTypingStyle().equals("Touch Typing")){
            burnoutChance = burnoutChance * 1.2;
        }
        else if(theTypist.getTypingStyle().equals("Phone Thumbs")){
            burnoutChance = burnoutChance * 1.1;
        }

        if ((typeChance < burnoutChance)&&(!theTypist.getMisTyped()))
        {
            if(theTypist.getAccessory().equals("Wrist Support")){
                theTypist.burnOut(BURNOUT_DURATION - 1);
            }else{
                theTypist.burnOut(BURNOUT_DURATION);
            }
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
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist);
        System.out.println();

        printSeat(seat2Typist);
        System.out.println();

        printSeat(seat3Typist);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          ⌨           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [~]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement — perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        //Append < when the typist misstypes 
        if(theTypist.getMisTyped())
        {
            System.out.print("<");
            spacesAfter--; // symbol + < takes two characters
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else if(theTypist.getMisTyped())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " JUST MISTYPED");
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        for(int i=0; i<times; i++){
            System.out.print(aChar);
        }
    }

    public static void main(String[] args) 
    {
        // int[] array = {1,2};
        // TypingRaceGUI race = new TypingRaceGUI("short", 3, array);
    }
}