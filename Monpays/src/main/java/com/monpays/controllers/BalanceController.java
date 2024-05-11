package com.monpays.controllers;

import com.monpays.services.interfaces.balance.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balances")
@CrossOrigin(origins = "http://localhost:4200")
public class BalanceController {
    @Autowired
    private IBalanceService balanceService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestAttribute("username") String username) {
        try {
            return new ResponseEntity<>(balanceService.getAll(username), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAllByAccount(
            @RequestAttribute("username") String username,
            @PathVariable("accountNumber") String accountNumber) {
        try {
            return new ResponseEntity<>(balanceService.getAllByAccount(username, accountNumber), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
