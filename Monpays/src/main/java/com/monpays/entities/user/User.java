package com.monpays.entities.user;

import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.enums.EUserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Long version;

    @Size(max = 50)
    @NotBlank
    @Column(unique = true)
    private String userName;

    @Column
    private String password;

    @Size(max = 50)
    @NotBlank
    @Column
    private String firstName;

    @Size(max = 50)
    @NotBlank
    @Column
    private String lastName;

    @Size(max = 255)
    @NotBlank
    @Column
    //@Email
    private String emailAddress;

    @Column
    private String phoneNumber;

    @Size(max = 50)
    @NotBlank
    @Column
    private String address;

    @ManyToOne
    private Profile profile;
    @Column

    @Enumerated(EnumType.STRING)
    private EUserStatus status;

    @Column
    private Boolean isFirstLogin;

    @Column
    private int failedLoginAttempts = 0;

    @Column
    private boolean mfaEnabled = false;

    @Column
    private String mfaSecret;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number is not in the standard format (10 digits)");
        }

        this.phoneNumber = phoneNumber;
    }

    public boolean hasRight(Operation operation) {
        return List.of(EUserStatus.ACTIVE, EUserStatus.MODIFIED).contains(this.getStatus()) &&
                (
                        operation.getOperation() == EOperationType.NONE ||
                                profile.getRights().stream().anyMatch(right -> right.equals(operation)) && !isFirstLogin
                );
    }
}
