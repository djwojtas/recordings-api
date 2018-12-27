package pl.edu.agh.recorder.exception.application;

import lombok.Getter;
import lombok.Setter;
import pl.edu.agh.recorder.exception.error.ApplicationError;

public class ApplicationException extends Exception {

    @Getter
    @Setter
    private ApplicationError error;

    public ApplicationException(String message, ApplicationError error) {
        super(message);
        this.error = error;
    }

    public ApplicationException() {
    }
}
