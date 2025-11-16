package com.dlwodn.kbo_savermatrix_system.service; // service 패키지 맞는지 확인

// 퀘스트 1, 3에서 만든 부품/파이프들 import
import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service // "이 클래스는 비즈니스 로직을 담당하는 '서비스'입니다" 라고 스프링에게 알려줌
public class FipService {

    // FIP 계산에 필요한 리그 평균 상수 (일단 3.2로 고정)
    private static final double FIP_CONSTANT = 3.2;

    private final List<PlayerDto> playerCache; // json 데이터를 읽어서 저장해둘 '캐시'

    // '생성자' - FipService가 처음 실행될 때 딱 한 번만 호출됨
    public FipService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        // "ResourceLoader" : resources 폴더의 파일을 읽는 도구
        // "ObjectMapper" : JSON <-> Java 객체 변환 도구 (퀘스트 1에서 추가)
        try {
            // 1. resources 폴더에서 players.json 파일을 찾는다.
            Resource resource = resourceLoader.getResource("classpath:players.json");
            InputStream inputStream = resource.getInputStream();

            // 2. JSON 파일을 읽어서 -> List<PlayerDto> 자바 리스트로 변환한다.
            List<PlayerDto> players = objectMapper.readValue(inputStream, new TypeReference<List<PlayerDto>>() {});

            // 3. FIP 계산 로직 실행
            calculateAndSetFip(players);

            // 4. FIP 기준으로 '오름차순' 정렬 (FIP는 낮을수록 좋음)
            Collections.sort(players, Comparator.comparingDouble(PlayerDto::getFip));

            // 5. 계산/정렬된 결과를 '캐시'에 저장
            this.playerCache = players;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("FIP 랭킹을 초기화하는 데 실패했습니다.", e);
        }
    }

    // [PUBLIC] FIP 랭킹을 반환하는 메인 메서드
    public List<PlayerDto> getFipRanking() {
        return this.playerCache; // 생성자에서 이미 계산/정렬해둔 캐시를 그냥 반환
    }

    // [PRIVATE] 선수 리스트를 받아 FIP를 계산하고 DTO에 값을 세팅하는 메서드
    private void calculateAndSetFip(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            // FIP 공식 적용
            double fipNumerator = (13 * player.getHomeRuns()) + (3 * (player.getWalks() + player.getHitByPitch() - player.getIntentionalWalks())) - (2 * player.getStrikeouts());
            double fip = (fipNumerator / player.getInningsPitched()) + FIP_CONSTANT;

            // DTO에 계산된 FIP 값 저장 (소수점 2자리까지만 반올림)
            player.setFip(Math.round(fip * 100.0) / 100.0);
        }
    }
}