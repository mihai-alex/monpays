package com.monpays.services.implementations.profile;

import com.monpays.entities.profile.Profile;
import com.monpays.entities.profile.ProfileHistoryEntry;
import com.monpays.persistence.repositories.profile.IProfileHistoryRepository;
import com.monpays.services.interfaces._generic.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileHistoryService implements IHistoryService<ProfileHistoryEntry, Profile> {

    @Autowired
    private IProfileHistoryRepository profileHistoryRepository;

    @Override
    public ProfileHistoryEntry addEntry(Profile profile) {
        ProfileHistoryEntry profileHistoryEntry = new ProfileHistoryEntry(profile, Timestamp.from(Instant.now()));
        return profileHistoryRepository.save(profileHistoryEntry);
    }

    @Override
    public List<ProfileHistoryEntry> getHistory() {
        return profileHistoryRepository.findAll();
    }

    @Override
    public List<ProfileHistoryEntry> getByObject(Profile profile) {
        return profileHistoryRepository.findAllByProfile(profile);
    }
}
