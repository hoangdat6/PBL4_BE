package org.pbl4.pbl4_be;

import lombok.extern.slf4j.Slf4j;
import org.pbl4.pbl4_be.enums.ERole;
import org.pbl4.pbl4_be.models.Role;
import org.pbl4.pbl4_be.models.Season;
import org.pbl4.pbl4_be.repositories.RoleRepository;
import org.pbl4.pbl4_be.repositories.UserRepository;
import org.pbl4.pbl4_be.services.SeasonService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@Slf4j
@Component
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final SeasonService seasonService;


    public DataLoader(RoleRepository roleRepository, SeasonService seasonService) {
        this.roleRepository = roleRepository;
        this.seasonService = seasonService;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Init data");

        /**
         * Init roles
         *
         * ROLE_USER
         * ROLE_ADMIN
         */
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER).orElse(null);
        Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN).orElse(null);
        if (roleUser == null) {
            Role role = new Role();
            role.setName(ERole.ROLE_USER);
            roleRepository.save(role);
        }

        if(roleAdmin == null) {
            Role role1 = new Role();
            role1.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role1);
        }

        log.info("Init season");
        /**
         * Init season
         *
         * Mỗi season sẽ diễn ra trong thời gian 1 tháng
         * Nếu server được start lên mà tháng đó chưa có season nào của tháng đó thì sẽ tạo season mới
         */
        Season currentSeason = seasonService.findCurrentSeason().orElse(null);

        if (currentSeason == null) {
            ZonedDateTime date = ZonedDateTime.now();
            // Lấy ngày đầu tiên của tháng hiện tại
            LocalDateTime startDate = date.with(TemporalAdjusters.firstDayOfMonth()).toLocalDateTime();
            // Lấy ngày cuối cùng của tháng hiện tại
            LocalDateTime endDate = date.with(TemporalAdjusters.lastDayOfMonth()).toLocalDateTime();
            // Tạo season mới

            System.out.println("startDate: " + startDate);
            System.out.println("endDate: " + endDate);
            Season season = new Season();
            season.setStartDate(startDate);
            season.setEndDate(endDate);
            season.setName("Season " + startDate.getMonthValue() + "/" + startDate.getYear());
            season.setReward("Reward for season " + startDate.getMonthValue() + "/" + startDate.getYear());

            seasonService.addSeason(season);
        }

    }
}