package exceptions;

import logger.Logger;

public class WrongClassException extends Exception {

    public WrongClassException(String c){
        Logger.log("Given class '" + c + "', does not match the methods requirments."
                + System.lineSeparator()
                + "Method can handle following classes: List, Array. Which are type of: String.");
    }
}
