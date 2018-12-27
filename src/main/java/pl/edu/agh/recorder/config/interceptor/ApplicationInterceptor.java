package pl.edu.agh.recorder.config.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.edu.agh.recorder.exception.application.*;
import pl.edu.agh.recorder.exception.error.ApplicationError;
import pl.edu.agh.recorder.message.ErrorResponse;
import ws.schild.jave.EncoderException;

@ControllerAdvice
public class ApplicationInterceptor {

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleMalformedRequestException() {
        return new ResponseEntity<>(
                new ErrorResponse("Malformed request, please check your json", ApplicationError.MALFORMED_REQUEST.getErrorCode()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({InvalidFileFormatException.class})
    public ResponseEntity<ErrorResponse> handleInvalidFileFormatException() {
        return new ResponseEntity<>(
                new ErrorResponse("Only mp3's are accepted", ApplicationError.INVALID_FILE_FORMAT.getErrorCode()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({UserExistsException.class})
    public ResponseEntity<ErrorResponse> handleUserExistsException() {
        return new ResponseEntity<>(
                new ErrorResponse("Username already exists", ApplicationError.USERNAME_EXISTS.getErrorCode()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException() {
        return new ResponseEntity<>(
                new ErrorResponse("User not found", ApplicationError.USER_NOT_FOUND.getErrorCode()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PermissionAlreadyGrantedException.class})
    public ResponseEntity<ErrorResponse> handlePermissionAlreadyGrantedException() {
        return new ResponseEntity<>(
                new ErrorResponse("User already has permission to this recording", ApplicationError.PERMISSION_ALREADY_GRANTED.getErrorCode()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PermissionDoesNotExistException.class})
    public ResponseEntity<ErrorResponse> handlePermissionDoesNotExistException() {
        return new ResponseEntity<>(
                new ErrorResponse("User already did't have permission to this recording", ApplicationError.PERMISSION_DOES_NOT_EXIST.getErrorCode()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TagAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleTagAlreadyExistsExceptions() {
        return new ResponseEntity<>(
                new ErrorResponse("Tag is already defined", ApplicationError.TAG_ALREADY_EXIST.getErrorCode()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({RecordingDoesNotExistException.class})
    public ResponseEntity<ErrorResponse> handleRecordingDoesNotExistException() {
        return new ResponseEntity<>(
                new ErrorResponse("Recording not found", ApplicationError.RECORDING_NOT_FOUND.getErrorCode()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EncoderException.class})
    public ResponseEntity<ErrorResponse> handleEncoderException() {
        return new ResponseEntity<>(
                new ErrorResponse("", ApplicationError.ENCODER_ERROR.getErrorCode()),
                HttpStatus.METHOD_FAILURE);
    }
}
