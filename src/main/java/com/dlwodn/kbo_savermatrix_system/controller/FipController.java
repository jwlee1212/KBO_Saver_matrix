package com.dlwodn.kbo_savermatrix_system.controller; // controller 패키지 맞는지 확인

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.service.FipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // "이 클래스는 JSON 데이터를 반환하는 'API 창구'입니다"
@RequestMapping("/api") // 이 클래스의 모든 주소는 "/api" 로 시작합니다.
public class FipController {

    private final FipService fipService; // 4번 퀘스트에서 만든 '핵심 로직'

    // '생성자' - 스프링이 FipController를 만들 때, FipService를 자동으로 주입(연결)해줌
    public FipController(FipService fipService) {
        this.fipService = fipService;
    }

    // [GET] /api/fip-ranking 주소로 'GET' 요청이 오면 이 메서드가 실행됨
    @GetMapping("/fip-ranking")
    public List<PlayerDto> getFipRanking() {
        // FipService에게 랭킹을 요청하고, 받은 결과를 JSON 형태로 즉시 반환
        return fipService.getFipRanking();
    }
}