package com.ti.fabricadosaber.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ti.fabricadosaber.services.EnumService;

@RestController
@RequestMapping("/enums")
public class EnumController {
    @Autowired
    EnumService enumService;

    @GetMapping("/grade")
    public ResponseEntity<List<String>> recoverGrade() {
        return ResponseEntity.ok(enumService.recoverGrade());
    }

    @GetMapping("/race")
    public ResponseEntity<List<String>> recoverRace() {
        return ResponseEntity.ok(enumService.recoverRace());
    }

    @GetMapping("/CivilStatus")
    public ResponseEntity<List<String>> recoverCivilStatus() {
        return ResponseEntity.ok(enumService.recoverCivilStatus());
    }

    @GetMapping("/religion")
    public ResponseEntity<List<String>> recoverReligion() {
        return ResponseEntity.ok(enumService.recoverReligion());
    }

        @GetMapping("/state")
    public ResponseEntity<List<String>> recoverState() {
        return ResponseEntity.ok(enumService.recoverState());
    }

}
