package pl.edu.agh.recorder.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum ApplicationError {

    //1xxx - user related errors
    USERNAME_EXISTS("1000"),
    INVALID_FILE_FORMAT("1010"),
    RECORDING_NOT_FOUND("1011"),
    USER_NOT_FOUND("1012"),
    PERMISSION_ALREADY_GRANTED("1013"),
    PERMISSION_DOES_NOT_EXIST("1014"),
    TAG_ALREADY_EXIST("1015"),

    //2xxx - third party exceptions
    ENCODER_ERROR("2000"),

    //4xxx - various errors
    MALFORMED_REQUEST("4000");

    @Getter
    @Setter
    private String errorCode;
}
