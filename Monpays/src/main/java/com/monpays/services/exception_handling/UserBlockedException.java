package com.monpays.services.exception_handling;

import org.hibernate.service.spi.ServiceException;

public class UserBlockedException extends ServiceException {
    public UserBlockedException(String message) {
        super(message);
    }
}
