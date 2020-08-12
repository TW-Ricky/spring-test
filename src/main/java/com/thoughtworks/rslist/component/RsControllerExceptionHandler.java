package com.thoughtworks.rslist.component;

import com.thoughtworks.rslist.exception.AmountLessThanMinimumAmount;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class RsControllerExceptionHandler {

    @ExceptionHandler({RsEventNotExistsException.class})
    public ResponseEntity<Error> rsEventNotExistsExceptionHandler(Exception e) {
        Error error = new Error();
        error.setError(e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler({AmountLessThanMinimumAmount.class})
    public ResponseEntity<Error> amountLessThanMinimumAmountExceptionHandler(Exception e) {
        Error error = new Error();
        error.setError(e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
