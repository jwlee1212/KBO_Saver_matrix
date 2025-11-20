// PlayerStatsController.java

package com.dlwodn.kbo_savermatrix_system.controller;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.service.PlayerStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerStatsController {

    private final PlayerStatsService service; // ⬅️ 'Service'가 아니라 'PlayerStatsService'

    // '생성자'도 'PlayerStatsService'를 받도록 수정!
    public PlayerStatsController(PlayerStatsService service) { // ⬅️ 'Service'가 아니라 'PlayerStatsService'
        this.service = service;
    }

    // [GET] /api/pitching-ranking
    @GetMapping("/pitching-ranking")
    public List<PlayerDto> getPitchingRanking() {
        return service.getPitchingRanking();
    }

    // [GET] /api/hitting-ranking
    @GetMapping("/hitting-ranking")
    public List<PlayerDto> getHittingRanking() {
        return service.getHittingRanking();
    }
}