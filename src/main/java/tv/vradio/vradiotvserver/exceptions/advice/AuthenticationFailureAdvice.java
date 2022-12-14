package tv.vradio.vradiotvserver.exceptions.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;

@ControllerAdvice
public class AuthenticationFailureAdvice {
    @ResponseBody
    @ExceptionHandler(AuthenticationFailureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String authenticationFailureHandler(AuthenticationFailureException ex) {
        return ex.getMessage();
    }
}
