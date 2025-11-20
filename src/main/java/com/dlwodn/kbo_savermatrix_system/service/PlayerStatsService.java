package com.dlwodn.kbo_savermatrix_system.service;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class PlayerStatsService {

    // 투수/타자 계산 상수 (변경 없음)
    private static final double FIP_CONSTANT = 3.2;
    private static final double W_SINGLE = 0.7;
    private static final double W_DOUBLE = 1.1;
    private static final double W_TRIPLE = 1.4;
    private static final double W_HR = 1.8;
    private static final double W_WALK = 0.3;

    private final List<PlayerDto> pitcherCache;
    private final List<PlayerDto> hitterCache;

    // '생성자' - 논리적 순서를 깔끔하게 정리!
    public PlayerStatsService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        try {
            Resource resource = resourceLoader.getResource("classpath:players.json");
            InputStream inputStream = resource.getInputStream();
            List<PlayerDto> players = objectMapper.readValue(inputStream, new TypeReference<List<PlayerDto>>() {});

            // 1. 모든 투수 스탯 계산 (FIP, ERA, WHIP, K/9, BB/9 통합 계산!)
            calculateAndSetPitchingMetrics(players);

            // 2. 모든 타자 스탯 계산 (OBP, SLG, OPS, WRC 통합 계산!)
            calculateAndSetObpSlgOps(players);
            calculateAndSetWrc(players);

            // IP가 0보다 큰 선수만 투수 랭킹에 포함
            this.pitcherCache = players.stream()
                    .filter(p -> p.getInningsPitched() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getEra)) // ERA 기준으로 정렬!
                    .collect(Collectors.toList());

            // PA가 0보다 큰 선수만 타자 랭킹에 포함
            this.hitterCache = players.stream()
                    .filter(p -> p.getPlateAppearances() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getOps).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("랭킹을 초기화하는 데 실패했습니다.", e);
        }
    }

    // [PUBLIC] 투수/타자 랭킹 반환 메서드는 변경 없음
    public List<PlayerDto> getPitchingRanking() { return this.pitcherCache; }
    public List<PlayerDto> getHittingRanking() { return this.hitterCache; }


    // [PRIVATE] OBP, SLG, OPS 계산 로직 (변경 없음)
    // [PRIVATE] OBP, SLG, OPS, AVG 계산 로직
    private void calculateAndSetObpSlgOps(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            if (player.getPlateAppearances() > 0) {
                double pa = player.getPlateAppearances();
                int hits = player.getSingle() + player.getDoubleBase() + player.getTripleBase() + player.getHomeRunBat();

                // ⚡️ 타수(AB) 계산에 희생타(SF, SH) 반영! ⚡️
                double atBats = pa - player.getWalksBat() - player.getHitByPitchBat()
                        - player.getSacrificeFlies() - player.getSacrificeHits();

                double totalBases = player.getSingle() + (player.getDoubleBase() * 2.0) + (player.getTripleBase() * 3.0) + (player.getHomeRunBat() * 4.0);
                double slg = (atBats > 0) ? (totalBases / atBats) : 0.0;

                // ⚡️ OBP 계산은 PA 기준 (SF 포함) ⚡️
                double obp = (pa > 0) ? ((double)hits + player.getWalksBat() + player.getHitByPitchBat()) / pa : 0.0;

                // ⚡️ 타율(AVG) 계산 추가! ⚡️
                double avg = (atBats > 0) ? ((double)hits / atBats) : 0.0;

                double ops = obp + slg;

                player.setOnBasePercentage(Math.round(obp * 1000.0) / 1000.0);
                player.setSluggingPercentage(Math.round(slg * 1000.0) / 1000.0);
                player.setOps(Math.round(ops * 1000.0) / 1000.0);
                player.setBattingAverage(Math.round(avg * 1000.0) / 1000.0); // AVG 저장
            }
        }
    }

    // [PRIVATE] wRC 계산 로직 (변경 없음)
    private void calculateAndSetWrc(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            if (player.getPlateAppearances() > 0) {
                double wrc = (player.getSingle() * W_SINGLE) +
                        (player.getDoubleBase() * W_DOUBLE) +
                        (player.getTripleBase() * W_TRIPLE) +
                        (player.getHomeRunBat() * W_HR) +
                        ((player.getWalksBat() + player.getHitByPitchBat()) * W_WALK);

                player.setWrc(Math.round(wrc * 10.0) / 10.0);
            }
        }
    }

    // [PRIVATE] 투수 계산 로직 통합 (FIP, ERA, WHIP, K/9, BB/9 모두 계산)
    private void calculateAndSetPitchingMetrics(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            // IP가 0보다 클 때만 계산
            if (player.getInningsPitched() > 0) {
                double ip = player.getInningsPitched();

                // 1. FIP
                double fipNumerator = (13 * player.getHomeRuns()) + (3 * (player.getWalks() + player.getHitByPitch() - player.getIntentionalWalks())) - (2 * player.getStrikeouts());
                double fip = (fipNumerator / ip) + FIP_CONSTANT;
                player.setFip(Math.round(fip * 100.0) / 100.0);

                // 2. ERA
                double era = (player.getEarnedRuns() * 9.0) / ip;
                player.setEra(Math.round(era * 100.0) / 100.0);

                // 3. WHIP
                double whip = (player.getWalks() + player.getHitsAllowed()) / ip;
                player.setWhip(Math.round(whip * 100.0) / 100.0);

                // 4. K/9
                double kPerNine = (player.getStrikeouts() * 9.0) / ip;
                player.setKPerNine(Math.round(kPerNine * 10.0) / 10.0);

                // 5. BB/9
                double bbPerNine = (player.getWalks() * 9.0) / ip;
                player.setBbPerNine(Math.round(bbPerNine * 10.0) / 10.0);
            }
        }
    }
}