package com.monpays.persistence.seeders;

import com.monpays.controllers.AccountController;
import com.monpays.controllers.AuthenticationController;
import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.user.UserChangePasswordDto;
import com.monpays.dtos.user.UserSignUpDto;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.account.Account;
import com.monpays.entities.balance.Balance;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import com.monpays.entities.user.User;
import com.monpays.entities.user.enums.EUserStatus;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.persistence.repositories.balance.IBalanceRepository;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.utils.ProfileTemplateXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Component
public class AbstractDatabaseSeeder {
    @Autowired
    protected AuthenticationController authenticationController;
    @Autowired
    protected IUserRepository userRepository;
    @Autowired
    protected IProfileRepository profileRepository;
    @Autowired
    protected AccountController accountController;
    @Autowired
    protected IAccountRepository accountRepository;
    @Autowired
    protected IBalanceRepository balanceRepository;
    @Autowired
    protected ProfileTemplateXmlParser profileTemplateXmlParser;

    private static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseSeeder.class);

    protected void insertSystemEntriesIfNotPresent() {
        if (userRepository.findByUserName("system").isEmpty()) {
            try {
                insertProfileSystem();
                insertUserSystem();
            } catch (NoSuchElementException e) {
                logger.error("Error while inserting system entries: {}", e.getMessage(), e);
            }
        }
    }

    protected void insertProfilesIfNotPresent() {
        if (this.profileRepository.count() <= 1) {
            try {
                insertProfileAdministratorSuper();
                insertProfileEmployeeSuper();
                insertProfileCustomerSuper();
                insertProfileCustomerDumb();
            } catch (NoSuchElementException e) {
                logger.error("Error while inserting profiles: {}", e.getMessage(), e);
            }
        }
    }

    protected void insertUsersIfNotPresent() {
        if (this.userRepository.count() <= 1) {
            try {
                insertUser("a1", "super administrator");
                insertUser("a2", "super administrator");
                insertUser("e1", "super employee");
                insertUser("e2", "super employee");
                insertUser("c1", "super customer");
                insertUser("c2", "super customer");
                insertUser("c3", "super customer");
                insertUser("c4", "super customer");
                insertUser("dumb1", "dumb customer");
            } catch (NoSuchElementException e) {
                logger.error("Error while inserting users: {}", e.getMessage(), e);
            }
        }
    }

    protected void insertAccountsIfNotPresent() {
        if (this.accountRepository.count() <= 0) {
            try {
                insertAccount("c1", "c1_account1");
                spawnMoneyInAccount("c1", "c1_account1", BigDecimal.valueOf(1000000L));
                insertAccount("c1", "c1_account2");
                insertAccount("c2", "c2_account1");
                insertAccount("c2", "c2_account2");
                insertAccount("c3", "c3_account1");
                insertAccount("c3", "c3_account2");
                insertAccount("c4", "c4_account1");
                insertAccount("c4", "c4_account2");
                insertAccount("a1", "a1_account1");
                spawnMoneyInAccount("a1", "a1_account1", BigDecimal.valueOf(1000000L));
                insertAccount("a2", "a1_account2");
            } catch (NoSuchElementException | InterruptedException e) {
                logger.error("Error while inserting accounts: {}", e.getMessage(), e);
            }
        }
    }

    private void insertProfileSystem() throws NoSuchElementException {
        List<Operation> operations_system = profileTemplateXmlParser
                .getProfileTemplateByName(EProfileType.ADMINISTRATOR.name())
                .orElseThrow().getRights();
        Profile profile_system = new Profile();
        profile_system.setName("system");
        profile_system.setType(EProfileType.ADMINISTRATOR); // Replace with your enum value
        profile_system.setStatus(EProfileStatus.ACTIVE);
        profile_system.setRights(operations_system);
        profileRepository.save(profile_system);
    }

    private void insertProfileAdministratorSuper() throws NoSuchElementException {
        List<Operation> operations_administrator = profileTemplateXmlParser
                .getProfileTemplateByName(EProfileType.ADMINISTRATOR.name())
                .orElseThrow().getRights();
        Profile profile_administrator = new Profile();
        profile_administrator.setName("super administrator");
        profile_administrator.setType(EProfileType.ADMINISTRATOR);
        profile_administrator.setStatus(EProfileStatus.ACTIVE);
        profile_administrator.setRights(operations_administrator);
        profileRepository.save(profile_administrator);
    }

    private void insertProfileEmployeeSuper() throws NoSuchElementException {
        List<Operation> operations_employee = profileTemplateXmlParser
                .getProfileTemplateByName(EProfileType.EMPLOYEE.name())
                .orElseThrow().getRights();
        Profile profile_employee = new Profile();
        profile_employee.setName("super employee");
        profile_employee.setType(EProfileType.EMPLOYEE);
        profile_employee.setStatus(EProfileStatus.ACTIVE);
        profile_employee.setRights(operations_employee);
        profileRepository.save(profile_employee);
    }

    private void insertProfileCustomerSuper() throws NoSuchElementException {
        List<Operation> operations_customer = profileTemplateXmlParser
                .getProfileTemplateByName(EProfileType.CUSTOMER.name())
                .orElseThrow().getRights();
        Profile profile_customer = new Profile();
        profile_customer.setName("super customer");
        profile_customer.setType(EProfileType.CUSTOMER);
        profile_customer.setStatus(EProfileStatus.ACTIVE);
        profile_customer.setRights(operations_customer);
        profileRepository.save(profile_customer);
    }

    private void insertProfileCustomerDumb() {
        List<Operation> operations_customer_2 = new ArrayList<>();
        Profile profile_customer_2 = new Profile();
        profile_customer_2.setName("dumb customer");
        profile_customer_2.setType(EProfileType.CUSTOMER);
        profile_customer_2.setStatus(EProfileStatus.ACTIVE);
        profile_customer_2.setRights(operations_customer_2);
        profileRepository.save(profile_customer_2);
    }

    private void insertUserSystem() throws NoSuchElementException {
        User user_system = new User();
        user_system.setUserName("system");
        user_system.setPassword("absolutely_not_a_valid_P@ssw0rd!");
        user_system.setFirstName("system");
        user_system.setLastName("system");
        user_system.setEmailAddress("system");
        user_system.setAddress("system");
        user_system.setPhoneNumber("0123456789");
        user_system.setStatus(EUserStatus.ACTIVE);
        user_system.setProfile(profileRepository.findByName("system").orElseThrow());
        userRepository.save(user_system);
    }

    private void insertUser(String name, String profileName) {
        signUpUser(name, profileName);
        changeUserPassword(name, "P@ssw0rd!", "P@ssw0rd!_new");
        changeUserPassword(name, "P@ssw0rd!_new", "P@ssw0rd!");
    }

    private void signUpUser(String name, String profileName) {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setUserName(name);
        userSignUpDto.setPassword("P@ssw0rd!");
        userSignUpDto.setFirstName(name);
        userSignUpDto.setLastName(name);
        userSignUpDto.setEmailAddress(name);
        userSignUpDto.setAddress(name);
        userSignUpDto.setPhoneNumber("0123456789");
        userSignUpDto.setProfileName(profileName);
        authenticationController.signUp(userSignUpDto);
    }

    private void changeUserPassword(String username, String oldPassword, String newPassword) {
        UserChangePasswordDto userChangePasswordDto = new UserChangePasswordDto();
        userChangePasswordDto.setOldPassword(oldPassword);
        userChangePasswordDto.setNewPassword(newPassword);
        authenticationController.changePassword(username, userChangePasswordDto);
    }

    private void insertAccount(String ownerName, String accountName) {
        createAccount(ownerName, accountName);
        approveAccount(ownerName, accountName);
    }

    private void createAccount(String ownerName, String accountName) {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setOwner(ownerName);
        accountRequestDto.setCurrency("EUR");
        accountRequestDto.setName(accountName);
        accountRequestDto.setTransactionLimit(new BigDecimal("100000"));
        accountController.create("a1", accountRequestDto);
    }

    private void approveAccount(String ownerName, String accountName) {
        String accountNumber = accountRepository.findAll().stream()
                .filter(account -> Objects.equals(account.getName(), accountName) &&
                        Objects.equals(account.getOwner().getUserName(), ownerName))
                .findFirst()
                .orElseThrow()
                .getAccountNumber();
        accountController.approve("a2", accountNumber);
    }

    @SuppressWarnings("SameParameterValue")
    private void spawnMoneyInAccount(String ownerName, String accountName, BigDecimal amount) throws NoSuchElementException, InterruptedException {
        Account account = accountRepository.findAll()
                .stream()
                .filter(account1 -> Objects.equals(account1.getOwner().getUserName(), ownerName) &&
                        Objects.equals(account1.getName(), accountName))
                .findAny()
                .orElseThrow();

        Thread.sleep(10);

        Balance balance = new Balance(balanceRepository.findAllByAccount(account)
                .stream().findFirst().orElseThrow());
        balance.receiveAmountAvailable(amount);
        balanceRepository.save(balance);
    }
}
