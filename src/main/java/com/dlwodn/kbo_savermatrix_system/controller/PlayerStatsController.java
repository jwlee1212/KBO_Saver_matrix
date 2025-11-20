package com.dlwodn.kbo_savermatrix_system.controller;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.service.PlayerStatsService;
import org.springframework.web.bind.annotation.*; // 모든 어노테이션 임포트

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerStatsController {

    private final PlayerStatsService service;

    public PlayerStatsController(PlayerStatsService service) {
        this.service = service;
    }

    // [GET] 상세 정보 조회
    @GetMapping("/player/{id}")
    public PlayerDto getPlayerDetail(@PathVariable Long id) {
        return service.getPlayerDetail(id);
    }

    // [GET] 투수 랭킹
    @GetMapping("/pitching-ranking")
    public List<PlayerDto> getPitchingRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getPitchingRanking(page, size);
    }

    // [GET] 타자 랭킹
    @GetMapping("/hitting-ranking")
    public List<PlayerDto> getHittingRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getHittingRanking(page, size);
    }
}