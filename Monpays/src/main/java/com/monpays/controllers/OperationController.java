package com.monpays.controllers;

import com.monpays.entities._generic.Operation;
import com.monpays.services.interfaces.user.IOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "http://localhost:4200")
public class OperationController {
    @Autowired
    private IOperationService operationService;

    @GetMapping
    public ResponseEntity<?> getMenu(@RequestAttribute String username) {
        try {
            return ResponseEntity.ok(operationService.getListRights(username));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{entityName}")
    public ResponseEntity<?> getRights4Entity(@RequestAttribute String username, @PathVariable String entityName) {
        try {
            return ResponseEntity.ok(operationService.getRights4Entity(username, entityName));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profiles/{profileType}")
    public ResponseEntity<?> getOperationsForProfileType(@RequestAttribute String username, @PathVariable String profileType) {
        try {
            List<Operation> operations = operationService.getOperationsForRole(username, profileType);
            return ResponseEntity.ok(operations);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
