package com.monpays.services.interfaces.user;

import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;

import java.util.List;

public interface IOperationService {
    List<String> getListRights(String username);
    List<EOperationType> getRights4Entity(String username, String entityName);

    List<Operation> getOperationsForRole(String username, String profileType);
}
