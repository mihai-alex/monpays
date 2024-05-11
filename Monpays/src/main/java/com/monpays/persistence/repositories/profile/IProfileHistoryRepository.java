package com.monpays.persistence.repositories.profile;

import com.monpays.entities.profile.Profile;
import com.monpays.entities.profile.ProfileHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProfileHistoryRepository extends JpaRepository<ProfileHistoryEntry, Long> {
    List<ProfileHistoryEntry> findAllByProfile(Profile profile);
}
