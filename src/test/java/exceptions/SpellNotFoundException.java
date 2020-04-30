package exceptions;

public class SpellNotFoundException extends Exception {

    public SpellNotFoundException(String message) {
        super(String.format("Spell '%s' not found in list of spells", message));
    }
}
