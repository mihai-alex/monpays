package com.monpays.persistence.repositories.user;

import com.monpays.entities.user.User;
import com.monpays.entities.user.UserHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserHistoryRepository extends JpaRepository<UserHistoryEntry, Long> {
    List<UserHistoryEntry> findAllByUser(User user);
}
