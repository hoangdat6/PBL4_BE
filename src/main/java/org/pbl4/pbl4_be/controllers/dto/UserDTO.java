package org.pbl4.pbl4_be.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pbl4.pbl4_be.models.User;
import org.pbl4.pbl4_be.models.UserDetailsImpl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String avatar;
    private Integer score;
}
