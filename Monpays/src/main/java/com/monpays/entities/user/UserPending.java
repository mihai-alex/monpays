package com.monpays.entities.user;

import com.monpays.entities.user.enums.EUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pending_users")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPending {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "original_user_userName", referencedColumnName = "userName", unique = true)
    private User originalUser;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String emailAddress;
    @Column
    private String phoneNumber;
    @Column
    private String address;
    @Column
    private String profileName;
    @Column
    @Enumerated(EnumType.STRING)
    private EUserStatus status;
    @Column
    private String actorUserName; // the username of the user who created the pending entry
}