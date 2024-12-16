package org.pbl4.pbl4_be.controllers.base;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.ErrorDTO;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> implements Serializable {
    private ErrorDTO<T> error;
}