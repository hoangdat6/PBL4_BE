package org.pbl4.pbl4_be.Utils;

import org.pbl4.pbl4_be.models.UserRankData;

import java.util.*;
import java.util.stream.Collectors;

public class RankUtil {

    // Static thresholds for rank tiers
    private static final int NEWBIE_MAX_SCORE = 100;
    private static final int PROFESSIONAL_MAX_SCORE = 300;

    // Cache for Challenger players (top 20 players with scores > 300)
    private static List<UserRankData> challengerCache = new ArrayList<>();

    /**
     * Get the rank tier based on the player's score.
     *
     * @param score Player's rank score
     * @return Rank tier as a String
     */
    public static String getRankTier(int score) {
        if (score <= NEWBIE_MAX_SCORE) {
            return "Newbie";
        } else if (score <= PROFESSIONAL_MAX_SCORE) {
            return "Professional";
        } else {
            return "Master";
        }
    }

    /**
     * Recalculate the Challenger tier for top 20 players.
     *
     * @param players List of all players
     */
    public static void updateChallengers(List<UserRankData> players) {
        challengerCache = players.stream()
                .filter(player -> player.getRankScore() > PROFESSIONAL_MAX_SCORE) // Only players with scores > 300
                .sorted((p1, p2) -> Integer.compare(p2.getRankScore(), p1.getRankScore())) // Sort descending
                .limit(20) // Top 20
                .collect(Collectors.toList());
    }

    /**
     * Check if a player is a Challenger.
     *
     * @param userId Player's ID
     * @return True if the player is a Challenger
     */
    public static boolean isChallenger(Long userId) {
        return challengerCache.stream().anyMatch(player -> player.getUserId().equals(userId));
    }

    /**
     * Get the rank tier including Challenger logic.
     *
     * @param userId Player's ID
     * @param score  Player's rank score
     * @return Rank tier as a String
     */
    public static String getRankTierWithChallenger(Long userId, int score) {
        if (isChallenger(userId)) {
            return "Challenger";
        }
        return getRankTier(score);
    }
}
