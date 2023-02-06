package de.r3s6.maven.constcreator;

/**
 * Thrown when an property key is encountered that can't be transformed into a
 * Java variable/constant name.
 *
 * @author Ralf Schandl
 */
public class InvalidPropertyKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    InvalidPropertyKeyException(final String message) {
        super(message);
    }

}
