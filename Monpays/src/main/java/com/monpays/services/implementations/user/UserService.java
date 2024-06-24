package com.monpays.services.implementations.user;

import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.dtos.user.*;
import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.User;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.user.enums.EUserStatus;
import com.monpays.entities.user.UserHistoryEntry;
import com.monpays.entities.user.UserPending;
import com.monpays.mappers._generic.AuditMapper;
import com.monpays.mappers.user.UserMapper;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import com.monpays.persistence.repositories.user.IUserHistoryRepository;
import com.monpays.persistence.repositories.user.IUserPendingRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.implementations._generic.AuditService;
import com.monpays.services.implementations.tfa.TwoFactorAuthentication;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.user.IUserService;
import com.monpays.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProfileRepository profileRepository;
    @Autowired
    private IUserHistoryRepository userHistoryRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private UserHistoryService userHistoryService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserPendingRepository userPendingRepository;
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private TwoFactorAuthentication tfaService;

    @Autowired
    private IUserActivityService userActivityService;


    @Override
    public List<UserResponseDto> filterUsers(String actorUsername, String columnName, String filterValue) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "filterUsers", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);
        return userRepository.findAll().stream().map(userMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public User getOneByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow();
    }

    @Override
    public List<UserResponseDto> getAll(String actorUsername) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        Operation operation = new Operation(EOperationType.LIST, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);

        // map them to dtos using streams
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * @param actorUsername
     * @param username
     * @param needsHistory
     * @param needsAudit
     * @return
     */
    @Override
    public UserResponseDto getOne(String actorUsername, String username, boolean needsPending, boolean needsHistory, boolean needsAudit) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "getOne", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(username).orElse(null);
        if(user == null) {
            return null;
        }
        auditService.add(actor, operation, user.getUserName());

        UserResponseDto userResponseDto = userMapper.toResponseDto(user);

        if(needsPending) {
            UserPendingResponseDto userPendingResponseDto = userMapper.toUserPendingResponseDto(
                    userPendingRepository.findByOriginalUserName(user.getUserName()).orElse(null));
            userResponseDto.setPendingEntity(userPendingResponseDto);
        }

        if(needsHistory) {
            List<UserHistoryEntryDto> userHistoryEntryDtos = new ArrayList<>();
            List<UserHistoryEntry> userHistoryEntries = userHistoryService.getByObject(user);

            userHistoryEntries.forEach(userHistoryEntry -> {
                userHistoryEntryDtos.add(userMapper.toHistoryEntryDto(userHistoryEntry));
            });

            userResponseDto.setHistory(userHistoryEntryDtos);
        }

        if(needsAudit) {
            List<AuditEntryDto> auditEntryDtos = new ArrayList<>();
            List<AuditEntry> auditEntries = auditService.getByObject(User.class.getSimpleName(), user.getUserName());

            auditEntries.forEach(auditEntry -> {
                auditEntryDtos.add(auditMapper.toAuditEntryDto(auditEntry));
            });

            userResponseDto.setAudit(auditEntryDtos);
        }

        return userResponseDto;
    }

    /**
     * @param actorUsername
     * @param userSignUpDto
     * @return
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public UserResponseDto create(String actorUsername, UserSignUpDto userSignUpDto) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "create", User.class.getSimpleName());

        if(userRepository.findByUserName(userSignUpDto.getUserName()).isPresent()) {
            if(userSignUpDto.getPassword() != null) {
                throw new ServiceException("Password should not be set.");
            }
            return internalRepair(actor, userMapper.toUserRequestDto(userSignUpDto));
        }
        else {
            if (userSignUpDto.getPassword() == null || !PasswordUtils.validatePassword(userSignUpDto.getPassword())) {
                throw new ServiceException("A valid password should be set.");
            }
            return internalCreate(actor, userSignUpDto);
        }
    }


    private UserResponseDto internalCreate(User actor, UserSignUpDto userSignUpDto) {
        Operation operation = new Operation(EOperationType.CREATE, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(userSignUpDto.getPassword());

        Profile profile = profileRepository.findByName(userSignUpDto.getProfileName()).orElseThrow();

        User user = userMapper.fromUserSignUpDto(userSignUpDto, hashedPassword, profile);
        user.setStatus(EUserStatus.CREATED);
        user.setIsFirstLogin(true);

        // MFA:
        if (user.isMfaEnabled()) {
            user.setMfaSecret(tfaService.generateNewSecret());
        }


        try {
            user = userRepository.save(user);
            userRepository.flush();
            UserPending userPending = userMapper.mapToUserPending(user, actor.getUserName());
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        userHistoryService.addEntry(user);
        auditService.add(actor, operation, userSignUpDto.getUserName());
        return userMapper.toResponseDto(user);
    }

    private UserResponseDto internalRepair(User actor, UserRequestDto userRequestDto) {
        Operation operation = new Operation(EOperationType.REPAIR, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userRequestDto.getUserName()).orElseThrow();
        if(user.getStatus() != EUserStatus.IN_REPAIR) {
            throw new ServiceException("Sir, you cannot repair a user that is not in repair.");
        }
        UserPending userPending = userPendingRepository.findByOriginalUserName(user.getUserName()).orElseThrow();

        // TODO: move this in the mappers
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmailAddress(userRequestDto.getEmailAddress());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setAddress(userRequestDto.getAddress());
        user.setProfile(profileRepository.findByName(userRequestDto.getProfileName()).orElseThrow());
        user.setStatus(EUserStatus.REPAIRED);
        userPending.setFirstName(userRequestDto.getFirstName());
        userPending.setLastName(userRequestDto.getLastName());
        userPending.setEmailAddress(userRequestDto.getEmailAddress());
        userPending.setPhoneNumber(userRequestDto.getPhoneNumber());
        userPending.setAddress(userRequestDto.getAddress());
        userPending.setProfileName(userRequestDto.getProfileName());

        userPending.setStatus(EUserStatus.REPAIRED);

        try {
            userRepository.save(user);
            userRepository.flush();
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        userHistoryService.addEntry(user);
        auditService.add(actor, operation, userRequestDto.getUserName());
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public UserPendingResponseDto modify(String actorUsername, String userName, UserRequestDto userRequestDto) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "modify", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.MODIFY, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        if(!Objects.equals(userName, userRequestDto.getUserName())) {
            throw new ServiceException("");
        }

        // Check if the user has pending modifications and requires approval/rejection
        if (this.existsPending(userRequestDto.getUserName())) {
            throw new ServiceException("");
        }

        User originalUser = userRepository.findByUserName(userName).orElseThrow();
        if (originalUser.getStatus() == EUserStatus.REMOVED) {
            throw new ServiceException("");
        }

        UserPending userPending = userMapper.mapToUserPending(originalUser, actorUsername);
        // TODO: add mapper
        userPending.setFirstName(userRequestDto.getFirstName());
        userPending.setLastName(userRequestDto.getLastName());
        userPending.setEmailAddress(userRequestDto.getEmailAddress());
        userPending.setPhoneNumber(userRequestDto.getPhoneNumber());
        userPending.setAddress(userRequestDto.getAddress());
        userPending.setStatus(originalUser.getStatus());
        userPending.setProfileName(userRequestDto.getProfileName());
        try {
            userPending = userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, userName);

        // TODO: add mapper for the deep copy into "addToHistory":
        User addToHistory = new User();
        addToHistory.setId(originalUser.getId());
        addToHistory.setVersion(originalUser.getVersion());
        addToHistory.setUserName(originalUser.getUserName());
        addToHistory.setFirstName(originalUser.getFirstName());
        addToHistory.setLastName(originalUser.getLastName());
        addToHistory.setEmailAddress(originalUser.getEmailAddress());
        addToHistory.setPhoneNumber(originalUser.getPhoneNumber());
        addToHistory.setAddress(originalUser.getAddress());
        addToHistory.setProfile(originalUser.getProfile());
        addToHistory.setStatus(EUserStatus.MODIFIED);
        userHistoryService.addEntry(addToHistory);

        try {
            userRepository.save(originalUser);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }
        return userMapper.toUserPendingResponseDto(userPending);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean remove(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "remove", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REMOVE, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Optional<User> toDeleteUserOptional = userRepository.findByUserName(userName);
        User toDeleteUser;
        if(toDeleteUserOptional.isEmpty()) {
            return false;
        }
        else {
            toDeleteUser = toDeleteUserOptional.get();
        }

        // Check if the profile has pending modifications and requires approval/rejection
        if (this.existsPending(toDeleteUser.getUserName())) {
            throw new ServiceException("");
        }

        if (toDeleteUser.getStatus() == EUserStatus.REMOVED) {
            throw new ServiceException("");
        }

        UserPending userPending = userMapper.mapToUserPending(toDeleteUser, actorUsername);
        userPending.setStatus(EUserStatus.REMOVED);
        try {
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toDeleteUser.getUserName());

        // TODO: add mapper
        User addToHistory = new User();
        addToHistory.setId(toDeleteUser.getId());
        addToHistory.setVersion(toDeleteUser.getVersion());
        addToHistory.setUserName(toDeleteUser.getUserName());
        addToHistory.setFirstName(toDeleteUser.getFirstName());
        addToHistory.setLastName(toDeleteUser.getLastName());
        addToHistory.setEmailAddress(toDeleteUser.getEmailAddress());
        addToHistory.setPhoneNumber(toDeleteUser.getPhoneNumber());
        addToHistory.setAddress(toDeleteUser.getAddress());
        addToHistory.setProfile(toDeleteUser.getProfile());
        addToHistory.setStatus(EUserStatus.MODIFIED);
        userHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean approve(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "approve", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.APPROVE, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userName).orElseThrow();
        if (user.getStatus() == EUserStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        UserPending userPending = verifyPendingAndNotSameUser(actorUsername, userName);

        boolean result;

        switch (user.getStatus().toString().toLowerCase()) {
            case "created", "repaired" -> {
                result = internalApproveCreation(user, userPending);
            }
            default -> {result = internalApproveModification(user, userPending);} // this means that the user is modified
        };

        auditService.add(actor, operation, userName);
        return result;
    }

    private boolean internalApproveCreation(User user, UserPending userPending) {
        user.setStatus(EUserStatus.ACTIVE);
        try {
            userRepository.save(user);

            // delete the pending entry from the pending table
            userPendingRepository.delete(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        userHistoryService.addEntry(user);

        return true;
    }

    private boolean internalApproveModification(User originalUser, UserPending userPending) {
        originalUser.setFirstName(userPending.getFirstName());
        originalUser.setLastName(userPending.getLastName());
        originalUser.setEmailAddress(userPending.getEmailAddress());
        originalUser.setPhoneNumber(userPending.getPhoneNumber());
        originalUser.setAddress(userPending.getAddress());
        originalUser.setProfile(profileRepository.findByName(userPending.getProfileName()).orElseThrow());
        originalUser.setStatus(userPending.getStatus());

        try {
            originalUser = userRepository.save(originalUser);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        // delete the pending entry from the pending table
        userPendingRepository.delete(userPending);

        // TODO: add mapper or CopyConstructor
        //User addToHistory = userMapper.fromUserPending(userPendingOptional.get());
        User addToHistory = new User();
        addToHistory.setId(originalUser.getId());
        addToHistory.setVersion(originalUser.getVersion());
        addToHistory.setUserName(originalUser.getUserName());
        addToHistory.setFirstName(originalUser.getFirstName());
        addToHistory.setLastName(originalUser.getLastName());
        addToHistory.setEmailAddress(originalUser.getEmailAddress());
        addToHistory.setPhoneNumber(originalUser.getPhoneNumber());
        addToHistory.setAddress(originalUser.getAddress());
        addToHistory.setProfile(originalUser.getProfile());
        addToHistory.setStatus(originalUser.getStatus());
        userHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean reject(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "reject", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REJECT, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userName).orElseThrow();
        if (user.getStatus() == EUserStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        UserPending userPending = verifyPendingAndNotSameUser(actorUsername, userName);
        boolean result;

        switch (user.getStatus().toString().toLowerCase()) {
            case "created" -> {
                result = internalRejectCreation(user, userPending);
            }
            case "repaired" -> {
                result = internalRejectReparation(user, userPending);
            }
            default -> { result = internalRejectModification(user, userPending);} // this means that the user is modified
        }

        auditService.add(actor, operation, userName);
        return result;
    }

    private boolean internalRejectCreation(User user, UserPending userPending) {
        user.setStatus(EUserStatus.IN_REPAIR);
        userPending.setStatus(EUserStatus.IN_REPAIR);

        try {
            userRepository.save(user);
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        return true;
    }

    private boolean internalRejectReparation(User user, UserPending userPending) {
        userHistoryRepository.deleteAll(userHistoryRepository.findAllByUser(user));
        userPendingRepository.delete(userPending);
        userRepository.delete(user);

        return true;
    }

    private boolean internalRejectModification(User oldUser, UserPending userPending) {
        // delete the pending entry from the pending table
        userPendingRepository.delete(userPending);

        // TODO: add mapper
        User addToHistory = new User();
        addToHistory.setId(oldUser.getId());
        addToHistory.setVersion(oldUser.getVersion());
        addToHistory.setUserName(oldUser.getUserName());
        addToHistory.setFirstName(oldUser.getFirstName());
        addToHistory.setLastName(oldUser.getLastName());
        addToHistory.setEmailAddress(oldUser.getEmailAddress());
        addToHistory.setPhoneNumber(oldUser.getPhoneNumber());
        addToHistory.setAddress(oldUser.getAddress());
        addToHistory.setProfile(oldUser.getProfile());
        addToHistory.setStatus(oldUser.getStatus());
        userHistoryService.addEntry(addToHistory);

        return true;
    }

    private UserPending verifyPendingAndNotSameUser(String actorUsername, String userName) {
        UserPending userPending = userPendingRepository
                .findByOriginalUserName(userName).orElseThrow();

        // if the same user tries to approve their own request
        if (Objects.equals(userPending.getActorUserName(), actorUsername)) {
            throw new ServiceException("");
        }

        return userPending;
    }

    private boolean existsPending(String userName) {
        return userPendingRepository.findByOriginalUserName(userName).isPresent();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean blockBySystem(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        Operation operation = new Operation(EOperationType.BLOCK, User.class.getSimpleName());
        if(!Objects.equals(actor.getUserName(), "system")) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userName).orElseThrow();
        if (user.getStatus().equals(EUserStatus.BLOCKED)) {
            throw new ServiceException("User is already blocked!");
        }

        // Check if the user has pending modifications and requires approval/rejection
        if (this.existsPending(userName)) {
            // TODO: find out if the pending entry should be deleted or not
            UserPending userPending = verifyPendingAndNotSameUser(actorUsername, userName);
            userPendingRepository.delete(userPending);
        }

        try {
            user.setStatus(EUserStatus.BLOCKED);
            user.setFailedLoginAttempts(0);
            user = userRepository.save(user);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        // TODO: add mapper or CopyConstructor
        //User addToHistory = userMapper.fromUserPending(userPendingOptional.get());
        User addToHistory = new User();
        addToHistory.setId(user.getId());
        addToHistory.setVersion(user.getVersion());
        addToHistory.setUserName(user.getUserName());
        addToHistory.setFirstName(user.getFirstName());
        addToHistory.setLastName(user.getLastName());
        addToHistory.setEmailAddress(user.getEmailAddress());
        addToHistory.setPhoneNumber(user.getPhoneNumber());
        addToHistory.setAddress(user.getAddress());
        addToHistory.setProfile(user.getProfile());
        addToHistory.setStatus(user.getStatus());
        userHistoryService.addEntry(addToHistory);

        auditService.add(actor, operation, userName);
        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean block(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "block", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.BLOCK, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userName).orElseThrow();
        if (user.getStatus().equals(EUserStatus.BLOCKED)) {
            throw new ServiceException("User is already blocked!");
        }

        // Check if the user has pending modifications and requires approval/rejection
        if (this.existsPending(userName)) {
            throw new ServiceException("");
        }

        if (user.getStatus() == EUserStatus.REMOVED) {
            throw new ServiceException("");
        }

        UserPending userPending = userMapper.mapToUserPending(user, actorUsername);
        userPending.setStatus(EUserStatus.BLOCKED);
        try {
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, userName);

        User addToHistory = new User();
        addToHistory.setId(user.getId());
        addToHistory.setVersion(user.getVersion());
        addToHistory.setUserName(user.getUserName());
        addToHistory.setFirstName(user.getFirstName());
        addToHistory.setLastName(user.getLastName());
        addToHistory.setEmailAddress(user.getEmailAddress());
        addToHistory.setPhoneNumber(user.getPhoneNumber());
        addToHistory.setAddress(user.getAddress());
        addToHistory.setProfile(user.getProfile());
        addToHistory.setStatus(EUserStatus.MODIFIED);
        userHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean unblock(String actorUsername, String userName) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "unblock", User.class.getSimpleName());

        Operation operation = new Operation(EOperationType.UNBLOCK, User.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        User user = userRepository.findByUserName(userName).orElseThrow();
        if (!user.getStatus().equals(EUserStatus.BLOCKED)) {
            throw new ServiceException("User is not blocked!");
        }

        // Check if the user has pending modifications and requires approval/rejection
        if (this.existsPending(userName)) {
            throw new ServiceException("");
        }

        if (user.getStatus() == EUserStatus.REMOVED) {
            throw new ServiceException("");
        }

        UserPending userPending = userMapper.mapToUserPending(user, actorUsername);
        userPending.setStatus(EUserStatus.ACTIVE);
        try {
            userPendingRepository.save(userPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, userName);

        User addToHistory = new User();
        addToHistory.setId(user.getId());
        addToHistory.setVersion(user.getVersion());
        addToHistory.setUserName(user.getUserName());
        addToHistory.setFirstName(user.getFirstName());
        addToHistory.setLastName(user.getLastName());
        addToHistory.setEmailAddress(user.getEmailAddress());
        addToHistory.setPhoneNumber(user.getPhoneNumber());
        addToHistory.setAddress(user.getAddress());
        addToHistory.setProfile(user.getProfile());
        addToHistory.setStatus(EUserStatus.MODIFIED);
        userHistoryService.addEntry(addToHistory);

        return true;
    }
}
