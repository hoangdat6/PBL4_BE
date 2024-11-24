package org.pbl4.pbl4_be.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pbl4.pbl4_be.models.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String avatar;
    private Integer score;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.score = 0;
    }
}
