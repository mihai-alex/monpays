package com.monpays.services.implementations.user;

import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.profile.ProfileTemplate;
import com.monpays.entities.user.User;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.user.IOperationService;
import com.monpays.services.interfaces.user.IUserService;
import com.monpays.utils.ProfileTemplateXmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OperationService implements IOperationService {
    private ProfileTemplateXmlParser xmlParser;

    @Autowired
    private IUserService userService;
    @Autowired
    IUserActivityService userActivityService;

    @Autowired
    public void setXmlParser(ProfileTemplateXmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    @Override
    public List<String> getListRights(String username) {
        User user = userService.getOneByUserName(username);

        userActivityService.add(user, "listAll", Operation.class.getSimpleName());

        if(user.getIsFirstLogin()) {
            return List.of();
        }

        return user.getProfile().getRights().stream()
                .filter(right -> right.getOperation().equals(EOperationType.LIST))
                .map(Operation::getGroupName)
                .toList();
    }

    @Override
    public List<EOperationType> getRights4Entity(String username, String entityName) {
        User user = userService.getOneByUserName(username);

        userActivityService.add(user, "listAllPerEntity", Operation.class.getSimpleName());

        if(user.getIsFirstLogin()) {
            return List.of();
        }

        return user.getProfile().getRights().stream()
                .filter(right -> right.getGroupName().equalsIgnoreCase(entityName))
                .map(Operation::getOperation)
                .toList();
    }

    // new methods below:
    @Override
    public List<Operation> getOperationsForRole(String username, String profileType) {
        User user = userService.getOneByUserName(username);

        userActivityService.add(user, "listAllPerProfile", Operation.class.getSimpleName());

        if(user.getIsFirstLogin()) {
            return List.of();
        }

        Optional<ProfileTemplate> profileTemplateOptional = xmlParser.getProfileTemplateByName(profileType);

        if (profileTemplateOptional.isPresent()) {
            ProfileTemplate profileTemplate = profileTemplateOptional.get();
            return profileTemplate.getRights().stream()
                    .map(operation -> new Operation(operation.getOperation(), operation.getGroupName()))
                    .toList();
        }

        return List.of();
    }

}
