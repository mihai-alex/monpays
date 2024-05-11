package com.monpays.controllers;

import com.monpays.dtos.profile.ProfileRequestDto;
import com.monpays.dtos.profile.ProfileResponseDto;
import com.monpays.services.interfaces.profile.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private IProfileService profileService;

    @GetMapping
    public @ResponseBody ResponseEntity<?> filterProfiles(
            @RequestAttribute("username") String username,
            @RequestParam(required = false) String columnName,
            @RequestParam(required = false) String filterValue
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<ProfileResponseDto> filteredProfiles = profileService.filterProfiles(username, columnName, filterValue);
            return new ResponseEntity<>(filteredProfiles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{name}")
    public @ResponseBody ResponseEntity<?> getOne(
            @RequestAttribute("username") String username,
            @PathVariable("name") String name,
            @RequestParam(value = "pending", required = false) boolean needsPending,
            @RequestParam(value = "history", required = false) boolean needsHistory,
            @RequestParam(value = "audit", required = false) boolean needsAudit
    ) {
        try {
            return new ResponseEntity<>(profileService.getOne(username, name, needsPending, needsHistory, needsAudit), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile-names") // TODO: remove the "/profile-names" part
    public @ResponseBody ResponseEntity<?> getProfileNames(@RequestAttribute("username") String username) {
        try {
            return new ResponseEntity<>(profileService.getProfileNames(username), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> create(@RequestAttribute("username") String username, @RequestBody ProfileRequestDto profileRequestDto) {
        try {
            return new ResponseEntity<>(profileService.create(username, profileRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{name}")
    public @ResponseBody ResponseEntity<?> update(@RequestAttribute("username") String username, @PathVariable("name") String name, @RequestBody ProfileRequestDto profileRequestDto) {
        try {
            return new ResponseEntity<>(profileService.modify(username, name, profileRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{name}")
    public @ResponseBody ResponseEntity<?> delete(@RequestAttribute("username") String username, @PathVariable("name") String name) {
        try {
            profileService.remove(username, name);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{name}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> approve(@RequestAttribute("username") String username,
                                                   @PathVariable("name") String name) {
        try {
            return new ResponseEntity<>(profileService.approve(username, name), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{name}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> reject(@RequestAttribute("username") String username,
                                                  @PathVariable("name") String name) {
        try {
            return new ResponseEntity<>(profileService.reject(username, name), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
