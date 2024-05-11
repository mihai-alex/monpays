package com.monpays.services.implementations._generic;

import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.account.Account;
import com.monpays.entities.payment.Payment;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.User;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.persistence.repositories._generic.IAuditRepository;
import com.monpays.persistence.repositories.payment.IPaymentRepository;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.interfaces._generic.IAuditService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class AuditService implements IAuditService {

    @Autowired
    private IAuditRepository auditRepository;
    @Autowired
    private IProfileRepository profileRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IPaymentRepository paymentRepository;
    @Autowired
    private IAccountRepository accountRepository;

    /**
     * @param user the user that performs the operation
     * @param operation the operation performed
     * @param uniqueEntityIdentifier the unique identifier of the object on which the operation was performed
     * @return the auditEntry created
     */
    @Override
    public AuditEntry add(User user, Operation operation, String uniqueEntityIdentifier) {
        AuditEntry auditEntry = new AuditEntry(0L, user, operation, uniqueEntityIdentifier, Timestamp.from(Instant.now()));
        auditRepository.save(auditEntry);
        return auditEntry;
    }

    @Override
    public AuditEntry add(AuditEntry auditEntry) {
        return auditRepository.save(auditEntry);
    }

    @Override
    public List<AuditEntry> getAll() {
        return auditRepository.findAll();
    }

    @Override
    public List<AuditEntry> getByClassProfile(String groupName) {
        return auditRepository.findAllBygroupName(groupName);
    }

    @Override
    public List<AuditEntry> getByObject(String groupName, String uniqueEntityIdentifier) {

        Long objectLongId;

        if(Objects.equals(groupName, Profile.class.getSimpleName())) {
            objectLongId = profileRepository.findByName(uniqueEntityIdentifier).orElseThrow().getId();
        } else if (Objects.equals(groupName, User.class.getSimpleName())) {
            objectLongId = userRepository.findByUserName(uniqueEntityIdentifier).orElseThrow().getId();
        }
        else if (Objects.equals(groupName, Account.class.getSimpleName())) {
            objectLongId = accountRepository.findByAccountNumber(uniqueEntityIdentifier).orElseThrow().getId();
        }
        else if (Objects.equals(groupName, Payment.class.getSimpleName())) {
            objectLongId = paymentRepository.findByNumber(uniqueEntityIdentifier).orElseThrow().getId();
        }
        else {
            throw new ServiceException("");
        }

        return auditRepository.findAllBygroupNameAndObjectId(groupName, uniqueEntityIdentifier);
    }
}
