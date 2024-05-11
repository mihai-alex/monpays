package com.monpays.persistence.repositories.user;

import com.monpays.entities.user.UserPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserPendingRepository extends JpaRepository<UserPending, Long> {

    @Query("SELECT up FROM UserPending up WHERE up.originalUser.userName = ?1")
    Optional<UserPending> findByOriginalUserName(String originalUserName);
}
