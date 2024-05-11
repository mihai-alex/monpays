package com.monpays.services.implementations._generic;

import com.monpays.entities._generic.UserActivityEntry;
import com.monpays.entities.user.User;
import com.monpays.persistence.repositories._generic.IUserActivityRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.interfaces._generic.IUserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class UserActivityService implements IUserActivityService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserActivityRepository userActivityRepository;

    @Override
    public UserActivityEntry add(User user, String action, String className) {
        UserActivityEntry userActivityEntry = new UserActivityEntry(0L, user, action, className, Timestamp.from(Instant.now()));
        userActivityRepository.save(userActivityEntry);
        return userActivityEntry;
    }

    @Override
    public UserActivityEntry add(UserActivityEntry userActivityEntry) {
        return userActivityRepository.save(userActivityEntry);
    }

    @Override
    public java.util.List<UserActivityEntry> getAll() {
        return userActivityRepository.findAll();
    }

    @Override
    public List<UserActivityEntry> getByUser(User user) {
        return userActivityRepository.findByUser(user);
    }
}
