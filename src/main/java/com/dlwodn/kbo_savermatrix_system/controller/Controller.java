// Controller.java

package com.dlwodn.kbo_savermatrix_system.controller;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.service.statsService; // ⬅️ 'FipService'가 아니라 'Service'를 import!
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller { // ⬅️ 'FipController'가 아니라 'Controller'

    private final statsService service; // ⬅️ 'FipService'가 아니라 'Service'

    // '생성자'도 'Service'를 받도록 수정!
    public Controller(statsService service) { // ⬅️ 'FipService'가 아니라 'Service'
        this.service = service;
    }

    // [GET] /api/pitching-ranking
    @GetMapping("/pitching-ranking")
    public List<PlayerDto> getPitchingRanking() {
        return service.getPitchingRanking(); // ⬅️ service.
    }

    // [GET] /api/hitting-ranking
    @GetMapping("/hitting-ranking")
    public List<PlayerDto> getHittingRanking() {
        return service.getHittingRanking(); // ⬅️ service.
    }
}