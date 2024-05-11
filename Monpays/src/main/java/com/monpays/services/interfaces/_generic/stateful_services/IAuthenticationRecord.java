package com.monpays.services.interfaces._generic.stateful_services;

import java.sql.Timestamp;

public interface IAuthenticationRecord {
    void setNewAction(String username);
    Timestamp getLastAction(String username);

    void removeLastAction(String username);
}
