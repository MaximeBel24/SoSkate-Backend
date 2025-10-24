package com.soskate.api.exceptions.spot;

/**
 * Exception levée lorsque les coordonnées GPS sont invalides.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(String message) {
        super(message);
    }

    public InvalidCoordinatesException() {
        super("Les coordonnées GPS fournies sont invalides");
    }
}