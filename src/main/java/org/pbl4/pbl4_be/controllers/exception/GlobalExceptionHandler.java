package org.pbl4.pbl4_be.controllers.exception;

import org.pbl4.pbl4_be.controllers.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý ngoại lệ
//     @ExceptionHandler(Exception.class)

    @ExceptionHandler(value = {BadRequestException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<?> handleBadRequest(Exception e) {
        return new ResponseEntity<>(new ErrorDTO<>(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlayerAlreadyInRoomException.class)
    public ResponseEntity<Object> handlePlayerAlreadyInRoomException(PlayerAlreadyInRoomException ex) {
        // Trả về mã trạng thái 409 Conflict với thông báo lỗi
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }



}
