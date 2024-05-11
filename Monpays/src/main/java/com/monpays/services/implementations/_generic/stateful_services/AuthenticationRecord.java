package com.monpays.services.implementations._generic.stateful_services;

import com.monpays.services.interfaces._generic.stateful_services.IAuthenticationRecord;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationRecord implements IAuthenticationRecord {
    private final Map<String, Timestamp> record;

    public AuthenticationRecord() {
        this.record = new HashMap<>();
    }

    @Override
    public void setNewAction(String username) {
        record.put(username, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public Timestamp getLastAction(String username) {
        return record.get(username);
    }

    @Override
    public void removeLastAction(String username) {
        record.remove(username);
    }
}
