package pl.edu.agh.recorder.exception.application;

import lombok.Getter;
import lombok.Setter;
import pl.edu.agh.recorder.exception.error.ApplicationError;

public class RuntimeApplicationException extends RuntimeException {

    @Getter
    @Setter
    private ApplicationError error;

    public RuntimeApplicationException(String message, ApplicationError error) {
        super(message);
        this.error = error;
    }

    public RuntimeApplicationException() {
    }
}
