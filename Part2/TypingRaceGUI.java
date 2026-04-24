import java.awt.CardLayout;
import javax.swing.*;

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
    private int seatCount;
    private String[] difficultyModifier;
    private TypistGUI[] typistList;
    private JFrame frame;
    private CardLayout cards;

    // Accuracy thresholds for mistype and burnout events
    private final double MISTYPE_BASE_CHANCE = 0.3;
    private int    SLIDE_BACK_AMOUNT   = 2;
    private final int    BURNOUT_DURATION     = 3;

    public TypingRaceGUI(String passageSelected, int seatCount, String[] difficultyModifiersChosen, TypistGUI[] typistList){
        selectPassage(passageSelected);
        this.seatCount = seatCount;
        this.difficultyModifier = difficultyModifiersChosen;
        applyDifficultyModifier();
        this.typistList = typistList;

    }

    private void selectPassage(String passageSelected){
        String shortPassage = "Programming";
        String mediumPassage = "Computer Programming";
        String longPassage = "Object Oriented Programming";
        if(passageSelected.equals("Short")){
            passageLength = 11;
            passageSelected = shortPassage;
        }else if(passageSelected.equals("Medium")){
            passageLength = 20;
            passageSelected = mediumPassage;
        }else if(passageSelected.equals("Long")){
            passageLength = 27;
            passageSelected = longPassage;
        }
    }


    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace()
    {
        boolean finished = false;
        Typist winnerName = null;
        double oldAccuracy = 0.0;
        final double INCREASE_ACCURACY = 1.02;

        // Reset all typists to the start of the passage
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        while (!finished)
        {
            // Advance each typist by one turn
            advanceTypist(seat1Typist);
            advanceTypist(seat2Typist);
            advanceTypist(seat3Typist);

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            if ( raceFinishedBy(seat1Typist))
            {
                finished = true;
                winnerName = seat1Typist; 
                oldAccuracy = seat1Typist.getAccuracy();
                seat1Typist.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
            } 
            else if(raceFinishedBy(seat2Typist))
            {
                finished = true;
                winnerName = seat2Typist;
                oldAccuracy = seat2Typist.getAccuracy(); 
                seat2Typist.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
            }
            else if(raceFinishedBy(seat3Typist))
            {
                finished = true;
                winnerName = seat3Typist; 
                oldAccuracy = seat3Typist.getAccuracy();
                seat3Typist.setAccuracy(Math.round(oldAccuracy*INCREASE_ACCURACY*100.0)/100.0);
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // TODO (Task 2a): Print the winner's name here
        System.out.println("And the Winner is... " + winnerName.typistName);
        System.out.println("Final accuracy: " + winnerName.getAccuracy() + " (improved from " + oldAccuracy + " )");

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
        if ((typeChance < theTypist.getAccuracy() * MISTYPE_BASE_CHANCE)&&(!theTypist.isBurntOut()))
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setMisTyped(true);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if ((typeChance < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())&&(!theTypist.getMisTyped()))
        {
            theTypist.burnOut(BURNOUT_DURATION);
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
        int[] array = {1,2};
        TypingRaceGUI race = new TypingRaceGUI("short", 3, array);
    }
}