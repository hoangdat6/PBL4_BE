package org.pbl4.pbl4_be.services;
import org.pbl4.pbl4_be.models.Season;
import org.pbl4.pbl4_be.repositories.SeasonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SeasonService {
    private final SeasonRepository seasonRepository;

    SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public Optional<Season> findCurrentSeason() {
        return seasonRepository.findCurrentSeason();
    }

    public void addSeason(Season season) {
        seasonRepository.save(season);
    }
    public Optional<Season> findById(Long id) {
        return seasonRepository.findById(id);
    }
}
