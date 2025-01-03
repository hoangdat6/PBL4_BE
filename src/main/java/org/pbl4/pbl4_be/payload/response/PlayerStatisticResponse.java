package org.pbl4.pbl4_be.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.PlayerStatisticDTO;

import java.util.List;

@Setter
@Getter
public class PlayerStatisticResponse {
    int totalPage;
    List<PlayerStatisticDTO> playerStatisticDTOList;
    public PlayerStatisticResponse(int totalPage, List<PlayerStatisticDTO> playerStatisticDTOList) {
        this.totalPage = totalPage;
        this.playerStatisticDTOList = playerStatisticDTOList;
    }
}
