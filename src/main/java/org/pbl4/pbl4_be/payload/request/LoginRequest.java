package org.pbl4.pbl4_be.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String email; // Thay đổi từ `username` thành `email`

    @NotBlank
    private String password;
}
