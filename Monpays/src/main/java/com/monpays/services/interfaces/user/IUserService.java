package com.monpays.services.interfaces.user;

import com.monpays.dtos.user.UserPendingResponseDto;
import com.monpays.dtos.user.UserRequestDto;
import com.monpays.dtos.user.UserResponseDto;
import com.monpays.dtos.user.UserSignUpDto;
import com.monpays.entities.user.User;

import java.util.List;

public interface IUserService {
    List<UserResponseDto> filterUsers(String actorUsername, String columnName, String filterValue);
    User getOneByUserName(String userName);
    List<UserResponseDto> getAll(String actorUsername);
    UserResponseDto getOne(String actorUsername, String username, boolean needsPending, boolean needsHistory, boolean needsAudit);
    UserResponseDto create(String actorUsername, UserSignUpDto userSignUpDto);
    UserPendingResponseDto modify(String actorUsername, String username, UserRequestDto userRequestDto);
    boolean remove(String actorUsername, String username);

    boolean approve(String actorUsername, String userName);

    boolean reject(String actorUsername, String userName);

    boolean blockBySystem(String actorUsername, String userName);

    boolean block(String actorUsername, String username);

    boolean unblock(String actorUsername, String username);
}
