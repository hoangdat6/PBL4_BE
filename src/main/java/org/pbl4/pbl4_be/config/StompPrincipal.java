package org.pbl4.pbl4_be.config;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private String playerId;

    public StompPrincipal() {}

    public StompPrincipal(String playerId) {
        this.playerId = playerId;

    }

    @Override
    public String getName() {
        return playerId;
    }
}
