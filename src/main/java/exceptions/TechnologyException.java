package exceptions;

/**
 * @Created by Terrax on 15-Oct-2015.
 */
public class TechnologyException extends Exception {

    public TechnologyException() {
        super("Unable to get the techs from the database");
    }
}
