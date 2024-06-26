package com.monpays.services.implementations.profile;

import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.dtos.profile.ProfileHistoryEntryDto;
import com.monpays.dtos.profile.ProfilePendingResponseDto;
import com.monpays.dtos.profile.ProfileRequestDto;
import com.monpays.dtos.profile.ProfileResponseDto;
import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.profile.ProfileHistoryEntry;
import com.monpays.entities.profile.ProfilePending;
import com.monpays.entities.profile.ProfileSpecifications;
import com.monpays.entities.user.User;
import com.monpays.mappers._generic.AuditMapper;
import com.monpays.mappers.profile.ProfileMapper;
import com.monpays.persistence.repositories.profile.IProfileHistoryRepository;
import com.monpays.persistence.repositories.profile.IProfilePendingRepository;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.interfaces._generic.IAuditService;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.profile.IProfileService;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService implements IProfileService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProfileRepository profileRepository;
    @Autowired
    private IProfilePendingRepository profilePendingRepository;
    @Autowired
    private IAuditService auditService;
    @Autowired
    private ProfileHistoryService profileHistoryService;
    @Autowired
    private IProfileHistoryRepository profileHistoryRepository;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private IUserActivityService userActivityService;

    @Override
    public List<ProfileResponseDto> filterProfiles(String actorUsername, String columnName, String filterValue) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "filterAll", Operation.class.getSimpleName());

        // TODO: remove pagination and return a list
        Operation operation = new Operation(EOperationType.LIST, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);

        Specification<Profile> specification = ProfileSpecifications.filterByColumn(columnName, filterValue);
        return profileRepository.findAll(specification).stream().map(profileMapper::toResponseDto).toList();
    }

    @Override
    public List<ProfileResponseDto> getAll(String actorUsername) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        Operation operation = new Operation(EOperationType.LIST, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);

        return profileRepository.findAll().stream()
                .map(profileMapper::toResponseDto)
                .toList();
    }

    @Override
    public ProfileResponseDto getOne(String actorUsername, String name,
                                     boolean needsPending, boolean needsHistory, boolean needsAudit) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "getOne", Operation.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);

        Profile profile = profileRepository.findByName(name).orElse(null);
        if(profile == null) {
            return null;
        }

        ProfileResponseDto profileResponseDto = profileMapper.toResponseDto(profile);

        if(needsPending) {
            ProfilePendingResponseDto profilePendingResponseDto = profileMapper.toProfilePendingResponseDto(
                    profilePendingRepository.findByOriginalProfileName(profile.getName()).orElse(null));
            profileResponseDto.setPendingEntity(profilePendingResponseDto);
        }

        if(needsHistory) {
            List<ProfileHistoryEntryDto> profileHistoryEntryDtos = new ArrayList<ProfileHistoryEntryDto>();
            List<ProfileHistoryEntry> profileHistoryEntries = profileHistoryService.getByObject(profile);

            profileHistoryEntries.forEach(profileHistoryEntry -> {
                profileHistoryEntryDtos.add(profileMapper.toHistoryEntryDto(profileHistoryEntry));
            });

            profileResponseDto.setHistory(profileHistoryEntryDtos);
        }

        if(needsAudit) {
            List<AuditEntryDto> auditEntryDtos = new ArrayList<AuditEntryDto>();
            List<AuditEntry> auditEntries = auditService.getByObject(Profile.class.getSimpleName(), profile.getName());

            auditEntries.forEach(auditEntry -> {
                auditEntryDtos.add(auditMapper.toAuditEntryDto(auditEntry));
            });

            profileResponseDto.setAudit(auditEntryDtos);
        }

        return profileResponseDto;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ProfileResponseDto create(String actorUsername, ProfileRequestDto profileRequestDto) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "create", Operation.class.getSimpleName());

        if(profileRepository.findByName(profileRequestDto.getName()).isPresent()) {
            return internalRepair(actor, profileRequestDto);
        }
        else {
            return internalCreate(actor, profileRequestDto);
        }
    }

    private ProfileResponseDto internalCreate(User actor, ProfileRequestDto profileRequestDto) {
        Operation operation = new Operation(EOperationType.CREATE, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Profile profile = profileMapper.fromRequestDto(profileRequestDto);
        profile.setStatus(EProfileStatus.CREATED);
        ProfilePending profilePending = profileMapper.toProfilePending(profile, actor.getUserName());

        try {
            profile = profileRepository.save(profile);
            profilePendingRepository.save(profilePending);
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        profileHistoryService.addEntry(profile);
        auditService.add(actor, operation, profileRequestDto.getName());
        return profileMapper.toResponseDto(profile);
    }

    private ProfileResponseDto internalRepair(User actor, ProfileRequestDto profileRequestDto) {
        Operation operation = new Operation(EOperationType.REPAIR, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Profile profile = profileRepository.findByName(profileRequestDto.getName()).orElseThrow();
        if(profile.getStatus() != EProfileStatus.IN_REPAIR) {
            throw new ServiceException("You cannot repair a profile that is not in repair.");
        }
        ProfilePending profilePending = profilePendingRepository.findByOriginalProfileName(profile.getName()).orElseThrow();

        // TODO: move this in the mappers
        profile.setType(profileRequestDto.getType());
        profile.setRights(profileRequestDto.getRights());
        profile.setStatus(EProfileStatus.REPAIRED);
        profilePending.setType(profileRequestDto.getType());
        profilePending.setRights(profileRequestDto.getRights());
        profilePending.setStatus(EProfileStatus.REPAIRED);

        try {
            profileRepository.save(profile);
            profilePendingRepository.save(profilePending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        profileHistoryService.addEntry(profile);
        auditService.add(actor, operation, profileRequestDto.getName());
        return profileMapper.toResponseDto(profile);
    }



    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ProfilePendingResponseDto modify(String actorUsername, String name, ProfileRequestDto profileRequestDto) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "modify", Operation.class.getSimpleName());

        Operation operation = new Operation(EOperationType.MODIFY, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        if(!Objects.equals(name, profileRequestDto.getName())) {
            throw new ServiceException("");
        }

        // Check if the profile has pending modifications and requires approval/rejection
        if (this.existsPending(profileRequestDto.getName())) {
            throw new ServiceException("");
        }

        Profile originalProfile = profileRepository.findByName(name).orElseThrow();
        if (originalProfile.getStatus() == EProfileStatus.REMOVED) {
            throw new ServiceException("");
        }

        ProfilePending profilePending = profileMapper.mapToProfilePending(originalProfile, actorUsername);
        // TODO: add mapper
        profilePending.setType(profileRequestDto.getType());
        profilePending.setRights(profileRequestDto.getRights());
        try {
            profilePending = profilePendingRepository.save(profilePending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, name);

        // TODO: add mapper for the deep copy into "addToHistory":
        Profile addToHistory = new Profile();
        addToHistory.setId(originalProfile.getId());
        addToHistory.setVersion(originalProfile.getVersion());
        addToHistory.setName(originalProfile.getName());
        addToHistory.setType(originalProfile.getType());
        addToHistory.setRights(originalProfile.getRights());
        addToHistory.setStatus(EProfileStatus.MODIFIED);
        profileHistoryService.addEntry(addToHistory);

        try {
            profileRepository.save(originalProfile);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }
        return profileMapper.toProfilePendingResponseDto(profilePending);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean remove(String actorUsername, String name) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "remove", Operation.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REMOVE, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Optional<Profile> toDeleteProfileOptional = profileRepository.findByName(name);
        Profile toDeleteProfile;
        if(toDeleteProfileOptional.isEmpty()) {
            return false;
        }
        else {
            toDeleteProfile = toDeleteProfileOptional.get();
        }

        // Check if the profile has pending modifications and requires approval/rejection
        if (this.existsPending(toDeleteProfile.getName())) {
            throw new ServiceException("");
        }

        if (toDeleteProfile.getStatus() == EProfileStatus.REMOVED) {
            throw new ServiceException("");
        }

        ProfilePending profilePending = profileMapper.mapToProfilePending(toDeleteProfile, actorUsername);
        // TODO: add mapper
        try {
            profilePending.setStatus(EProfileStatus.REMOVED);
            profilePendingRepository.save(profilePending);
            profileRepository.save(toDeleteProfile);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toDeleteProfile.getName());

        // TODO: add mapper for the deep copy into "addToHistory":
        Profile addToHistory = new Profile();
        addToHistory.setId(toDeleteProfile.getId());
        addToHistory.setVersion(toDeleteProfile.getVersion());
        addToHistory.setName(toDeleteProfile.getName());
        addToHistory.setType(toDeleteProfile.getType());
        addToHistory.setRights(toDeleteProfile.getRights());
        addToHistory.setStatus(EProfileStatus.MODIFIED);
        profileHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean approve(String actorUsername, String name) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "approve", Operation.class.getSimpleName());

        Operation operation = new Operation(EOperationType.APPROVE, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Profile profile = profileRepository.findByName(name).orElseThrow();
        if (profile.getStatus() == EProfileStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        ProfilePending profilePending = verifyPendingAndNotSameUser(actorUsername, name);

        boolean result;

        switch (profile.getStatus().toString().toLowerCase()) {
            case "created", "repaired" -> {
                result = internalApproveCreation(profile, profilePending);
            }
            default -> { result = internalApproveModification(profile, profilePending);}
        };

        auditService.add(actor, operation, name);
        return result;
    }

    private boolean internalApproveCreation(Profile profile, ProfilePending profilePending) {
        profile.setStatus(EProfileStatus.ACTIVE);
        try {
            profileRepository.save(profile);

            // delete the pending entry from the pending table
            profilePendingRepository.delete(profilePending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        profileHistoryService.addEntry(profile);

        return true;
    }

    private boolean internalApproveModification(Profile originalProfile, ProfilePending profilePending) {
        List<Operation> newRights = new ArrayList<>(profilePending.getRights());
        originalProfile.setRights(newRights);
        originalProfile.setType(profilePending.getType());
        originalProfile.setStatus(profilePending.getStatus());

        try {
            originalProfile = profileRepository.save(originalProfile);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        // delete the pending entry from the pending table
        profilePendingRepository.delete(profilePending);

        // TODO: add mapper or CopyConstructor
        Profile addToHistory = new Profile();
        addToHistory.setId(originalProfile.getId());
        addToHistory.setVersion(originalProfile.getVersion());
        addToHistory.setType(originalProfile.getType());
        addToHistory.setName(originalProfile.getName());
        addToHistory.setRights(originalProfile.getRights());
        addToHistory.setStatus(originalProfile.getStatus());
        profileHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean reject(String actorUsername, String name) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "reject", Operation.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REJECT, Profile.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Profile profile = profileRepository.findByName(name).orElseThrow();
        if (profile.getStatus() == EProfileStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        ProfilePending profilePending = verifyPendingAndNotSameUser(actorUsername, name);
        if (profilePending == null) {
            throw new ServiceException("");
        }

        boolean result;

        switch (profile.getStatus().toString().toLowerCase()) {
            case "created" -> {
                result = internalRejectCreation(profile, profilePending);
            }
            case "repaired" -> {
                result = internalRejectReparation(profile, profilePending);
            }
            default -> { result = internalRejectModification(profile, profilePending);}
        }

        auditService.add(actor, operation, name);
        return result;
    }

    @Override
    public List<String> getProfileNames(String actorUsername) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "getProfileNames", Operation.class.getSimpleName());

        if(actor.getIsFirstLogin()) {
            return List.of();
        }

        return profileRepository.findDistinctName();
    }

    private boolean internalRejectCreation(Profile profile, ProfilePending profilePending) {
        profile.setStatus(EProfileStatus.IN_REPAIR);
        profilePending.setStatus(EProfileStatus.IN_REPAIR);

        try {
            profileRepository.save(profile);
            profilePendingRepository.save(profilePending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        return true;
    }

    private boolean internalRejectReparation(Profile profile, ProfilePending profilePending) {
        profileHistoryRepository.deleteAll(profileHistoryRepository.findAllByProfile(profile));
        profilePendingRepository.delete(profilePending);
        profileRepository.delete(profile);

        return true;
    }

    private boolean internalRejectModification(Profile oldProfile, ProfilePending profilePending) {
        if (oldProfile.getStatus() == EProfileStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        // delete the pending entry from the pending table
        profilePendingRepository.delete(profilePending);

        // TODO: add mapper
        Profile addToHistory = new Profile();
        addToHistory.setId(oldProfile.getId());
        addToHistory.setVersion(oldProfile.getVersion());
        addToHistory.setType(oldProfile.getType());
        addToHistory.setName(oldProfile.getName());
        addToHistory.setRights(oldProfile.getRights());
        addToHistory.setStatus(oldProfile.getStatus());
        profileHistoryService.addEntry(addToHistory);

        return true;
    }

    private ProfilePending verifyPendingAndNotSameUser(String actorUsername, String name) {
        ProfilePending profilePending = profilePendingRepository
                .findByOriginalProfileName(name).orElseThrow();

        // if the same user tries to approve their own request
        if (Objects.equals(profilePending.getUsername(), actorUsername)) {
            throw new ServiceException("");
        }

        return profilePending;
    }

    private boolean existsPending(String name) {
        return profilePendingRepository.findByOriginalProfileName(name).isPresent();
    }
}


/**
 @Transactional(Transactional.TxType.REQUIRES_NEW)
 */
