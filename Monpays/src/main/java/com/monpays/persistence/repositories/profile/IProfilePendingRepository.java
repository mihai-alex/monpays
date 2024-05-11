package com.monpays.persistence.repositories.profile;
import com.monpays.entities.profile.ProfilePending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IProfilePendingRepository extends JpaRepository<ProfilePending, Long> {

    @Query("SELECT pp FROM ProfilePending pp WHERE pp.originalProfile.name = ?1")
    Optional<ProfilePending> findByOriginalProfileName(String originalProfileName);
}
