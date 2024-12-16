package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
}
