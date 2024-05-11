package com.monpays.controllers;

import com.monpays.dtos.payment.PaymentRequestDto;
import com.monpays.services.interfaces.payment.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    @Autowired
    private IPaymentService paymentService;

    @GetMapping("/{paymentNumber}")
    public @ResponseBody ResponseEntity<?> getOne(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber,
            @RequestParam(value = "needsHistory", required = false) boolean needsHistory,
            @RequestParam(value = "needsAudit", required = false) boolean needsAudit
    ) {
        try {
            return new ResponseEntity<>(paymentService.getOne(username, paymentNumber, needsHistory, needsAudit), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public @ResponseBody ResponseEntity<?> getAll(@RequestAttribute("username") String username) {
        try {
            return new ResponseEntity<>(paymentService.getAll(username), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/accounts/{accountNumber}")
    public @ResponseBody ResponseEntity<?> getAllByAccountNumber(
            @RequestAttribute("username") String username,
            @PathVariable("accountNumber") String accountNumber
    ) {
        try {
            return new ResponseEntity<>(paymentService.getAllByAccountNumber(username, accountNumber), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> create(
            @RequestAttribute("username") String username,
            @RequestBody PaymentRequestDto paymentRequestDto
    ) {
        try {
            return new ResponseEntity<>(paymentService.create(username, paymentRequestDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{paymentNumber}/checks")
    public @ResponseBody ResponseEntity<?> repair(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber,
            @RequestBody PaymentRequestDto paymentRequestDto
    ) {
        try {
            return new ResponseEntity<>(paymentService.repair(username, paymentNumber, paymentRequestDto),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{paymentNumber}/checks/approve")
    public @ResponseBody ResponseEntity<?> approve(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber
            ) {
        try {
            return new ResponseEntity<>(paymentService.approve(username, paymentNumber),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{paymentNumber}/checks/verify")
    public @ResponseBody ResponseEntity<?> verify(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber
    ) {
        try {
            return new ResponseEntity<>(paymentService.verify(username, paymentNumber),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{paymentNumber}/checks/authorize")
    public @ResponseBody ResponseEntity<?> authorize(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber
    ) {
        try {
            return new ResponseEntity<>(paymentService.authorize(username, paymentNumber),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{paymentNumber}/checks")
    public @ResponseBody ResponseEntity<?> reject(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber
    ) {
        try {
            return new ResponseEntity<>(paymentService.reject(username, paymentNumber),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{paymentNumber}")
    public @ResponseBody ResponseEntity<?> cancel(
            @RequestAttribute("username") String username,
            @PathVariable("paymentNumber") String paymentNumber
    ) {
        try {
            return new ResponseEntity<>(paymentService.cancel(username, paymentNumber),HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
