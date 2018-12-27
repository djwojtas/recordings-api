package pl.edu.agh.recorder.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorResponse extends DefaultResponse {
    private String errorCode;

    public ErrorResponse(String message, String applicationError) {
        super(message);
        this.errorCode = applicationError;
    }
}
