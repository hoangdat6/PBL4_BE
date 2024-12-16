package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controllers.dto.ProfileDTO;
import org.pbl4.pbl4_be.controllers.dto.UserDTO;
import org.pbl4.pbl4_be.models.PlayerSeason;
import org.pbl4.pbl4_be.models.User;
import org.pbl4.pbl4_be.repositories.PlayerSeasonRepository;
import org.pbl4.pbl4_be.repositories.UserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PlayerSeasonRepository playerSeasonRepository;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PlayerSeasonRepository playerSeasonRepository) {
        this.userRepository = userRepository;
        this.playerSeasonRepository = playerSeasonRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public Optional<UserDTO> findUserWithScoreBySeasonId(Long userId, Integer score) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(new UserDTO(user.getId(), user.getName(), user.getAvatar(), score));
    }

    public Optional<ProfileDTO> findProfileById(Long userId) {
        return userRepository.findProfileById(userId);
    }

}