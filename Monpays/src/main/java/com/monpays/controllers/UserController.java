package com.monpays.controllers;

import com.monpays.dtos.user.UserRequestDto;
import com.monpays.dtos.user.UserResponseDto;
import com.monpays.dtos.user.UserSignUpDto;
import com.monpays.services.interfaces.user.IUserService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<?> filterUsers(
            @RequestAttribute("username") String username,
            @RequestParam(required = false) String columnName,
            @RequestParam(required = false) String filterValue
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<UserResponseDto> filteredUsers = userService.filterUsers(username, columnName, filterValue);
            return new ResponseEntity<>(filteredUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getOne(
            @RequestAttribute("username") String actorUsername,
            @PathVariable("username") String username,
            @RequestParam(value = "pending", required = false) boolean needsPending,
            @RequestParam(value = "history", required = false) boolean needsHistory,
            @RequestParam(value = "audit", required = false) boolean needsAudit
    ) {
        try {
            return new ResponseEntity<>(userService.getOne(actorUsername, username, needsPending, needsHistory, needsAudit), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestAttribute("username") String actorUsername,
            @RequestBody UserSignUpDto userSignUpDto
    ) {
        try {
            return new ResponseEntity<>(userService.create(actorUsername, userSignUpDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> modify(
            @RequestAttribute("username") String actorUsername,
            @PathVariable("username") String username,
            @RequestBody UserRequestDto userRequestDto
    ) {
        try {
            return new ResponseEntity<>(userService.modify(actorUsername, username, userRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> delete(
            @RequestAttribute("username") String actorUsername,
            @PathVariable("username") String username
    ) {
        try {
            return new ResponseEntity<>(userService.remove(actorUsername, username), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userName}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> approve(@RequestAttribute("username") String username,
                                                   @PathVariable("userName") String userName) {
        try {
            return new ResponseEntity<>(userService.approve(username, userName), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userName}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> reject(@RequestAttribute("username") String username,
                                                   @PathVariable("userName") String userName) {
        try {
            return new ResponseEntity<>(userService.reject(username, userName), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{username}/block")
    public ResponseEntity<?> block(
            @RequestAttribute("username") String actorUsername,
            @PathVariable("username") String username
    ) {
        try {
            boolean blocked = userService.block(actorUsername, username);
            if (blocked) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{username}/unblock")
    public ResponseEntity<?> unblock(
            @RequestAttribute("username") String actorUsername,
            @PathVariable("username") String username
    ) {
        try {
            boolean unblocked = userService.unblock(actorUsername, username);
            if (unblocked) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
