package com.monpays.services.implementations.user;

import com.monpays.entities.user.User;
import com.monpays.entities.user.UserHistoryEntry;
import com.monpays.persistence.repositories.user.IUserHistoryRepository;
import com.monpays.services.interfaces._generic.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class UserHistoryService implements IHistoryService<UserHistoryEntry, User> {
    @Autowired
    private IUserHistoryRepository userHistoryRepository;

    @Override
    public UserHistoryEntry addEntry(User user) {
        UserHistoryEntry userHistoryEntry = new UserHistoryEntry(user, Timestamp.from(Instant.now()));
        return userHistoryRepository.save(userHistoryEntry);
    }

    @Override
    public List<UserHistoryEntry> getHistory() {
        return userHistoryRepository.findAll();
    }

    @Override
    public List<UserHistoryEntry> getByObject(User user) {
        return userHistoryRepository.findAllByUser(user);
    }
}
