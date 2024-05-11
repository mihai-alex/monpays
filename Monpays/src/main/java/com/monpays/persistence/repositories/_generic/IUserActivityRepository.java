package com.monpays.persistence.repositories._generic;

import com.monpays.entities._generic.UserActivityEntry;
import com.monpays.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserActivityRepository extends JpaRepository<UserActivityEntry, Long> {
    List<UserActivityEntry> findByUser(User user);
}
