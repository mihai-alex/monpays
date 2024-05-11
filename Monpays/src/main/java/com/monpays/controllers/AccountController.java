package com.monpays.controllers;

import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.account.AccountResponseDto;
import com.monpays.services.implementations.account.AccountHistoryService;
import com.monpays.services.interfaces.account.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {
    @Autowired
    private IAccountService accountService;
    @Autowired
    private AccountHistoryService accountHistoryService;

    @GetMapping
    public @ResponseBody ResponseEntity<?> filterProfiles(
            @RequestAttribute("username") String username,
            @RequestParam(required = false) String columnName,
            @RequestParam(required = false) String filterValue
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<AccountResponseDto> filteredAccounts = accountService.filterAccounts(username, columnName, filterValue);
            return new ResponseEntity<>(filteredAccounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<?> getOne(
            @RequestAttribute("username") String username,
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(value = "pending", required = false) boolean needsPending,
            @RequestParam(value = "history", required = false) boolean needsHistory,
            @RequestParam(value = "audit", required = false) boolean needsAudit
    ) {
        try {
            return new ResponseEntity<>(accountService.getOne(username, accountNumber, needsPending, needsHistory, needsAudit), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> create(@RequestAttribute("username") String username,
                                                  @RequestBody AccountRequestDto accountRequestDto) {
        try {
            return new ResponseEntity<>(accountService.create(username, accountRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<?> modify(
            @RequestAttribute("username") String username,
            @PathVariable("accountNumber") String accountNumber,
            @RequestBody AccountRequestDto accountRequestDto) {
        try {
            return new ResponseEntity<>(accountService.modify(username, accountNumber, accountRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<?> changeAccountStatus(
            @RequestAttribute("username") String username,
            @PathVariable("accountNumber") String accountNumber,
            @RequestBody String operation) {
        try {
            return new ResponseEntity<>(accountService.changeAccountStatus(username, accountNumber, operation.toLowerCase()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<?> remove(@RequestAttribute("username") String username, @PathVariable("accountNumber") String accountNumber) {
        try {
            return new ResponseEntity<>(accountService.remove(username, accountNumber), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{accountNumber}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> approve(@RequestAttribute("username") String username,
                                                   @PathVariable("accountNumber") String accountNumber) {
        try {
            return new ResponseEntity<>(accountService.approve(username, accountNumber), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{accountNumber}/checks") // checks is a convention
    public @ResponseBody ResponseEntity<?> reject(@RequestAttribute("username") String username,
                                                  @PathVariable("accountNumber") String accountNumber) {
        try {
            return new ResponseEntity<>(accountService.reject(username, accountNumber), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
