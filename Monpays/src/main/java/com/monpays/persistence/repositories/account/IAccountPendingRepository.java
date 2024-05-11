package com.monpays.persistence.repositories.account;

        import com.monpays.entities.account.AccountPending;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Query;

        import java.util.Optional;

public interface IAccountPendingRepository extends JpaRepository<AccountPending, Long> {

    @Query("SELECT ap FROM AccountPending ap WHERE ap.originalAccount.accountNumber = ?1")
    Optional<AccountPending> findByOriginalAccountNumber(String originalAccountNumber);
}
