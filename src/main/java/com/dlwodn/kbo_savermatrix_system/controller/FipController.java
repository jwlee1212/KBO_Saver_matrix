package com.dlwodn.kbo_savermatrix_system.controller;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.service.FipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FipController {

    private final FipService fipService;

    public FipController(FipService fipService) {
        this.fipService = fipService;
    }

    // [GET] /api/pitching-ranking 주소로 투수 랭킹 반환
    @GetMapping("/pitching-ranking")
    public List<PlayerDto> getPitchingRanking() {
        return fipService.getPitchingRanking();
    }

    // [GET] /api/hitting-ranking 주소로 타자 랭킹 반환
    @GetMapping("/hitting-ranking")
    public List<PlayerDto> getHittingRanking() {
        return fipService.getHittingRanking();
    }
}