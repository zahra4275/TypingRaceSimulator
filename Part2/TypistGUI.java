import java.awt.Color;

/**
 * The Typist class represents individual competitors in the race. Each competitor has a name, symbol, progress, whether they are burnt out, 
 * how many turns of burnout left, and their accuracy. There are also several accessor methods and mutator methods.
 *
 * @author Zahra Bint Afzal Asghar
 * @version 22/04/2026
 */
public class TypistGUI
{
    // Fields of class Typist
    String typistName;
    char typistSymbol;
    Color typistColor;
    int progress = 0;
    boolean burntout = false;
    int burnoutTurnsLeft = 0;
    int numBurnout = 0;
    double accuracy;
    boolean misTyped = false;
    String typingStyle;
    String keyboardStyle;
    String accessory;
    
    // Constructor of class Typist
    /**
     * Constructor for objects of class Typist.
     * Creates a new typist with a given symbol, name, and accuracy rating.
     *
     * @param typistSymbol  a single Unicode character representing this typist (e.g. '①', '②', '③')
     * @param typistName    the name of the typist (e.g. "TURBOFINGERS")
     * @param typistAccuracy the typist's accuracy rating, between 0.0 and 1.0
     * @param typingStyle the typing style chosen for this typist
     * @param keyboardStyle the keyboard style chosen when customising typists
     * @param accessory the accessory chosen
     */
    public TypistGUI(char typistSymbol, String typistName, String typingStyle, String keyboardStyle, Color typistColor, String accessory, double accuracy)
    {
        this.typistName = typistName;
        this.typistSymbol = typistSymbol;
        this.typistColor = typistColor;
        this.typingStyle = typingStyle;
        ApplyTypingStyle();
        this.keyboardStyle = keyboardStyle;
        this.accessory = accessory;
        this.accuracy = accuracy;
    }


    // Methods of class Typist

    /**
     * Increases and decreases accuracy based on typing style.
     */
    private void ApplyTypingStyle(){
        if(typingStyle.equals("Touch Typing")){
            accuracy = accuracy * 1.1;
        }
        else if(typingStyle.equals("Phone Thumbs")){
            accuracy = accuracy * 0.95;
        }
    }

    /**
     * Returns typing style
     */
    public String getTypingStyle(){
        return typingStyle;
    }

    /**
     * Returns keyboard style
     */
    public String getKeyboardStyle(){
        return keyboardStyle;
    }

    /**
     * Returns accessory
     */
    public String getAccessory(){
        return accessory;
    }

    /**
     * Returns Typist's colour
     */
    public Color getColour(){
        return typistColor;
    }

    /**
     * Returns number of times typist burnt out
     */
    public int getNumBurnout(){
        return numBurnout;
    }

    /**
     * Increases number out burnt out times
     */
    public void increaseBurnout(){
        numBurnout++;
    }

    /**
     * Sets this typist into a burnout state for a given number of turns.
     * A burnt-out typist cannot type until their burnout has worn off.
     *
     * @param turns the number of turns the burnout will last
     */
    public void burnOut(int turns){
        burntout = true;
        burnoutTurnsLeft = turns;
        numBurnout++;
    }

    /**
     * Reduces the remaining burnout counter by one turn.
     * When the counter reaches zero, the typist recovers automatically.
     * Has no effect if the typist is not currently burnt out.
     */
    public void recoverFromBurnout(){
        if(burntout == true){
            if(burnoutTurnsLeft == 0){
                burntout = false;
            }
            else{
                burnoutTurnsLeft--;
            }
        }
    }

    /**
     * Returns the typist's accuracy rating.
     *
     * @return accuracy as a double between 0.0 and 1.0
     */
    public double getAccuracy(){
        return accuracy; 
    }

    /**
     * Returns the typist's current progress through the passage.
     * Progress is measured in characters typed correctly so far.
     * Note: this value can decrease if the typist mistypes.
     *
     * @return progress as a non-negative integer
     */
    public int getProgress(){
        return progress;  
    }

    /**
     * Returns the name of the typist.
     *
     * @return the typist's name as a String
     */
    public String getName(){
        return typistName; 
    }

    /**
     * Returns the character symbol used to represent this typist.
     *
     * @return the typist's symbol as a char
     */
    public char getSymbol(){
        return typistSymbol; 
    }

    /**
     * Returns the number of turns of burnout remaining.
     * Returns 0 if the typist is not currently burnt out.
     *
     * @return burnout turns remaining as a non-negative integer
     */
    public int getBurnoutTurnsRemaining(){
        if(burntout == false){
            return 0;
        }
        else {
            return burnoutTurnsLeft;
        } 
    }

    /**
     * Returns value of mistyped field
     */
    public boolean getMisTyped(){
        return misTyped;
    }

    /**
     * Sets mistyped field
     */
    public void setMisTyped(boolean ifMisTyped){
        misTyped = ifMisTyped;
    }

    /**
     * Resets the typist to their initial state, ready for a new race.
     * Progress returns to zero, burnout is cleared entirely.
     */
    public void resetToStart(){
        progress = 0;
        burntout = false;
        burnoutTurnsLeft= 0;
    }

    /**
     * Returns true if this typist is currently burnt out, false otherwise.
     *
     * @return true if burnt out
     */
    public boolean isBurntOut(){
        return burntout; 
    }

    /**
     * Advances the typist forward by one character along the passage.
     * Should only be called when the typist is not burnt out.
     */
    public void typeCharacter(){
        progress++;
    }

    /**
     * Moves the typist backwards by a given number of characters (a mistype).
     * Progress cannot go below zero — the typist cannot slide off the start.
     *
     * @param amount the number of characters to slide back (must be positive)
     */
    public void slideBack(int amount){
        if(amount > 0){
            if(amount > progress){
                progress = 0;
            }
            else{
                progress = progress - amount;
            }
        }
    }

    /**
     * Sets the accuracy rating of the typist.
     * Values below 0.0 should be set to 0.0; values above 1.0 should be set to 1.0.
     *
     * @param newAccuracy the new accuracy rating
     */
    public void setAccuracy(double newAccuracy)
    {
        if(newAccuracy < 0.0){
            accuracy = 0.0;
        }
        else if(newAccuracy > 1.0){
            accuracy = 1.0;
        }
        else{
            accuracy = newAccuracy;
        }
    }

    /**
     * Sets the symbol used to represent this typist.
     *
     * @param newSymbol the new symbol character
     */
    public void setSymbol(char newSymbol){
        typistSymbol = newSymbol;
    }
}
