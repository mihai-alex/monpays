package com.monpays.entities.user;

import com.monpays.entities._generic.AbstractHistoryEntry;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.enums.EUserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_history")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryEntry extends AbstractHistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String userName;
    @Column
    private String password;
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
    @ManyToOne
    private Profile profile;
    @Column
    @Enumerated(EnumType.STRING)
    private EUserStatus status;
    @ManyToOne
    private User user;

    public UserHistoryEntry(User user, Timestamp timestamp) {
        super(timestamp);
        this.id = 0L;
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.emailAddress = user.getEmailAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.profile = user.getProfile();
        this.status = user.getStatus();
        this.user = user;
    }
}
