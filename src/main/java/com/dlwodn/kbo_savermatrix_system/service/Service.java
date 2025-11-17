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
public class Service {

    // 투수: FIP 계산에 필요한 리그 평균 상수 (MVP용 플레이스홀더)
    private static final double FIP_CONSTANT = 3.2;

    // 타자: wRC 계산에 필요한 가중치 (리그 평균 상수는 제거됨)
    private static final double W_SINGLE = 0.7; // 단타 가중치
    private static final double W_DOUBLE = 1.1; // 2루타 가중치
    private static final double W_TRIPLE = 1.4; // 3루타 가중치
    private static final double W_HR = 1.8;     // 홈런 가중치
    private static final double W_WALK = 0.3;   // 볼넷/사구 가중치

    private final List<PlayerDto> pitcherCache;
    private final List<PlayerDto> hitterCache;

    // '생성자'
    public Service(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        try {
            Resource resource = resourceLoader.getResource("classpath:players.json");
            InputStream inputStream = resource.getInputStream();
            List<PlayerDto> players = objectMapper.readValue(inputStream, new TypeReference<List<PlayerDto>>() {});
            calculateAndSetPitchingMetrics(players);
            calculateAndSetFip(players);
            calculateAndSetObpSlgOps(players);
            calculateAndSetWrc(players); // ⬅️ 메서드 이름도 wRC로 통일

            // IP가 0보다 큰 선수만 투수 랭킹에 포함
            this.pitcherCache = players.stream()
                    .filter(p -> p.getInningsPitched() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getEra)) // ⬅️ ERA 기준으로 정렬!
                    .collect(Collectors.toList());

            // PA가 0보다 큰 선수만 타자 랭킹에 포함 (wRC는 높을수록 좋으므로 내림차순)
            this.hitterCache = players.stream()
                    .filter(p -> p.getPlateAppearances() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getOps).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("랭킹을 초기화하는 데 실패했습니다.", e);
        }
    }

    // [PUBLIC] 투수 랭킹을 반환하는 메서드 (변경 없음)
    public List<PlayerDto> getPitchingRanking() {
        return this.pitcherCache;
    }

    // [PUBLIC] 타자 랭킹을 반환하는 메서드 (변경 없음)
    public List<PlayerDto> getHittingRanking() {
        return this.hitterCache;
    }

    // [PRIVATE] FIP 계산 로직 (변경 없음)
    private void calculateAndSetFip(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            if (player.getInningsPitched() > 0) {
                double fipNumerator = (13 * player.getHomeRuns()) + (3 * (player.getWalks() + player.getHitByPitch() - player.getIntentionalWalks())) - (2 * player.getStrikeouts());
                double fip = (fipNumerator / player.getInningsPitched()) + FIP_CONSTANT;
                player.setFip(Math.round(fip * 100.0) / 100.0);
            }
        }
    }
    private void calculateAndSetObpSlgOps(List<PlayerDto> players) {
        for (PlayerDto player : players) {
            if (player.getPlateAppearances() > 0) {
                // 타석수 (PA)
                double pa = player.getPlateAppearances();
                // 안타 (H) = 1B + 2B + 3B + HR
                int hits = player.getSingle() + player.getDoubleBase() + player.getTripleBase() + player.getHomeRunBat();
                // 타수 (AB) = PA - BB - HBP (간소화)
                double atBats = pa - player.getWalksBat() - player.getHitByPitchBat();

                // 1. 장타율 (SLG): (1B + 2B*2 + 3B*3 + HR*4) / AB
                double totalBases = player.getSingle() + (player.getDoubleBase() * 2.0) + (player.getTripleBase() * 3.0) + (player.getHomeRunBat() * 4.0);
                double slg = (atBats > 0) ? (totalBases / atBats) : 0.0; // 분모 0 방지

                // 2. 출루율 (OBP): (H + BB + HBP) / PA
                double obp = (pa > 0) ? ((double)hits + player.getWalksBat() + player.getHitByPitchBat()) / pa : 0.0; // 분모 0 방지

                // 3. OPS: OBP + SLG
                double ops = obp + slg;

                // DTO에 저장 (소수점 3자리까지 반올림)
                player.setOnBasePercentage(Math.round(obp * 1000.0) / 1000.0);
                player.setSluggingPercentage(Math.round(slg * 1000.0) / 1000.0);
                player.setOps(Math.round(ops * 1000.0) / 1000.0);
            }
        }
    }

    // [PRIVATE] wRC 계산 로직 (수정된 로직)
    private void calculateAndSetWrc(List<PlayerDto> players) { // ⬅️ 메서드 이름 변경
        for (PlayerDto player : players) {
            if (player.getPlateAppearances() > 0) {
                // 1. wRC (Weighted Runs Created) 계산
                double wrc = (player.getSingle() * W_SINGLE) +
                        (player.getDoubleBase() * W_DOUBLE) +
                        (player.getTripleBase() * W_TRIPLE) +
                        (player.getHomeRunBat() * W_HR) +
                        ((player.getWalksBat() + player.getHitByPitchBat()) * W_WALK);

                // 2. wRC 값 저장 (소수점 1자리까지)
                // wRC+ 계산 로직은 완전히 제거되고 wRC값만 저장됨
                player.setWrc(Math.round(wrc * 10.0) / 10.0); // ⬅️ setWrc로 저장
            }
        }
    }
    // [PRIVATE] 투수 계산 로직 통합 (FIP, ERA, WHIP, K/9, BB/9)
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