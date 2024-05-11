package com.monpays.services.interfaces._generic;

import com.monpays.entities._generic.UserActivityEntry;
import com.monpays.entities.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserActivityService {
    UserActivityEntry add(User user, String action, String className);
    UserActivityEntry add(UserActivityEntry userActivityEntry);
    List<UserActivityEntry> getAll();
    List<UserActivityEntry> getByUser(User user);
}
