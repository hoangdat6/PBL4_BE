package org.pbl4.pbl4_be.services;
import org.pbl4.pbl4_be.models.RoomDB;
import org.pbl4.pbl4_be.repositories.RoomDBRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomDBService {
    public final RoomDBRepository roomDBRepository;

    public RoomDBService(RoomDBRepository roomDBRepository) {
        this.roomDBRepository = roomDBRepository;
    }

    public RoomDB save(RoomDB roomDB) {
        return roomDBRepository.save(roomDB);
    }

    @Transactional
    public RoomDB FindById(Long id) {
        return roomDBRepository.findById(id).orElse(null);
    }

}
