package com.dlwodn.kbo_savermatrix_system.service;

import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerStatsService {

    // --- [상수 정의] ---
    // 투수 상수
    private static final double FIP_CONSTANT = 3.2;

    // 타자 wRC (기본) 가중치
    private static final double W_SINGLE = 0.7;
    private static final double W_DOUBLE = 1.1;
    private static final double W_TRIPLE = 1.4;
    private static final double W_HR = 1.8;
    private static final double W_WALK = 0.3;

    // wOBA 가중치 (시즌마다 다르지만, 표준값 적용)
    private static final double WOBA_W_BB = 0.69;
    private static final double WOBA_W_HBP = 0.72;
    private static final double WOBA_W_1B = 0.89;
    private static final double WOBA_W_2B = 1.27;
    private static final double WOBA_W_3B = 1.62;
    private static final double WOBA_W_HR = 2.10;
    private static final double WOBA_SCALE = 1.15; // wOBA 스케일 상수

    private final List<PlayerDto> pitcherCache;
    private final List<PlayerDto> hitterCache;

    // 생성자: 데이터 로딩 및 계산 실행
    public PlayerStatsService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        try {
            Resource resource = resourceLoader.getResource("classpath:players.json");
            InputStream inputStream = resource.getInputStream();
            List<PlayerDto> players = objectMapper.readValue(inputStream, new TypeReference<List<PlayerDto>>() {});

            // 1. 투수 스탯 계산 (ERA, FIP, WHIP, K/9, BB/9, K/BB)
            calculatePitcherStats(players);

            // 2. 타자 스탯 계산 (AVG, OBP, SLG, OPS, wRC, ISO, BABIP, wOBA, wRAA, PSN, GPA)
            calculateBatterStats(players);

            // 투수 랭킹 (ERA 기준 오름차순 정렬)
            this.pitcherCache = players.stream()
                    .filter(p -> p.getInningsPitched() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getEra))
                    .collect(Collectors.toList());

            // 타자 랭킹 (OPS 기준 내림차순 정렬)
            this.hitterCache = players.stream()
                    .filter(p -> p.getPlateAppearances() > 0)
                    .sorted(Comparator.comparingDouble(PlayerDto::getOps).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("랭킹 데이터 로딩 및 계산 실패", e);
        }
    }

    public List<PlayerDto> getPitchingRanking() { return this.pitcherCache; }
    public List<PlayerDto> getHittingRanking() { return this.hitterCache; }

    // ⚾️ [투수] 통합 계산 메서드
    private void calculatePitcherStats(List<PlayerDto> players) {
        for (PlayerDto p : players) {
            if (p.getInningsPitched() > 0) {
                double ip = p.getInningsPitched();

                // 1. ERA (평균자책점)
                double era = (p.getEarnedRuns() * 9.0) / ip;
                p.setEra(round(era, 2));

                // 2. FIP (수비무관 평균자책점)
                double fipNumerator = (13 * p.getHomeRuns()) + (3 * (p.getWalks() + p.getHitByPitch() - p.getIntentionalWalks())) - (2 * p.getStrikeouts());
                double fip = (fipNumerator / ip) + FIP_CONSTANT;
                p.setFip(round(fip, 2));

                // 3. WHIP (이닝당 출루 허용률)
                double whip = (p.getWalks() + p.getHitsAllowed()) / ip;
                p.setWhip(round(whip, 2));

                // 4. K/9, BB/9
                p.setKPerNine(round((p.getStrikeouts() * 9.0) / ip, 1));
                p.setBbPerNine(round((p.getWalks() * 9.0) / ip, 1));

                // 5. K/BB (볼삼비)
                double kbb = (p.getWalks() > 0) ? (double) p.getStrikeouts() / p.getWalks() : 0.0;
                p.setKbb(round(kbb, 2));
            }
        }
    }

    // ⚾️ [타자] 통합 계산 메서드
    private void calculateBatterStats(List<PlayerDto> players) {
        // 리그 평균 wOBA 계산을 위한 변수 (약식: 전체 합산 평균)
        double totalWobaNum = 0;
        double totalPa = 0;

        // 1차 루프: 기본 스탯 및 wOBA 계산
        for (PlayerDto p : players) {
            if (p.getPlateAppearances() > 0) {
                double pa = p.getPlateAppearances();
                // 안타 = 1루타 + 2루타 + 3루타 + 홈런
                int hits = p.getSingle() + p.getDoubleBase() + p.getTripleBase() + p.getHomeRunBat();
                // 타수(AB) = PA - BB - HBP - SF - SH
                double atBats = pa - p.getWalksBat() - p.getHitByPitchBat() - p.getSacrificeFlies() - p.getSacrificeHits();

                // 1. 타율 (AVG)
                double avg = (atBats > 0) ? ((double)hits / atBats) : 0.0;
                p.setBattingAverage(round(avg, 3));

                // 2. 장타율 (SLG)
                double totalBases = p.getSingle() + (p.getDoubleBase() * 2.0) + (p.getTripleBase() * 3.0) + (p.getHomeRunBat() * 4.0);
                double slg = (atBats > 0) ? (totalBases / atBats) : 0.0;
                p.setSluggingPercentage(round(slg, 3));

                // 3. 출루율 (OBP)
                double obpNum = hits + p.getWalksBat() + p.getHitByPitchBat();
                double obpDenom = atBats + p.getWalksBat() + p.getHitByPitchBat() + p.getSacrificeFlies();
                double obp = (obpDenom > 0) ? (obpNum / obpDenom) : 0.0;
                p.setOnBasePercentage(round(obp, 3));

                // 4. OPS
                p.setOps(round(obp + slg, 3));

                // 5. ISO (순수 장타율)
                p.setIso(round(slg - avg, 3));

                // 6. BABIP (인플레이 타구 타율)
                double babipNum = hits - p.getHomeRunBat();
                double babipDenom = atBats - p.getStrikeoutsBat() - p.getHomeRunBat() + p.getSacrificeFlies();
                double babip = (babipDenom > 0) ? (babipNum / babipDenom) : 0.0;
                p.setBabip(round(babip, 3));

                // 7. PSN (호타준족 지수)
                double psnNum = 2.0 * p.getHomeRunBat() * p.getStolenBases();
                double psnDenom = p.getHomeRunBat() + p.getStolenBases();
                double psn = (psnDenom > 0) ? (psnNum / psnDenom) : 0.0;
                p.setPsn(round(psn, 2));

                // 8. GPA (Gross Production Average)
                double gpa = (1.8 * obp + slg) / 4.0;
                p.setGpa(round(gpa, 3));

                // 9. wRC (기본 가중치 방식)
                double wrc = (p.getSingle() * W_SINGLE) + (p.getDoubleBase() * W_DOUBLE) +
                        (p.getTripleBase() * W_TRIPLE) + (p.getHomeRunBat() * W_HR) +
                        ((p.getWalksBat() + p.getHitByPitchBat()) * W_WALK);
                p.setWrc(round(wrc, 1));

                // 10. wOBA (가중 출루율)
                double wobaNum = (WOBA_W_BB * (p.getWalksBat() - 0)) + // 고의4구 제외 로직이 필요하면 0 대신 IBB 사용
                        (WOBA_W_HBP * p.getHitByPitchBat()) +
                        (WOBA_W_1B * p.getSingle()) +
                        (WOBA_W_2B * p.getDoubleBase()) +
                        (WOBA_W_3B * p.getTripleBase()) +
                        (WOBA_W_HR * p.getHomeRunBat());
                double wobaDenom = pa; // 약식 분모 (PA - IBB)
                double woba = (wobaDenom > 0) ? (wobaNum / wobaDenom) : 0.0;
                p.setWoba(round(woba, 3));

                // 리그 평균 계산용
                totalWobaNum += wobaNum;
                totalPa += wobaDenom;
            }
        }

        // 2차 루프: wRAA (리그 평균이 필요해서 나중에 계산)
        double leagueWoba = (totalPa > 0) ? (totalWobaNum / totalPa) : 0.0;
        for (PlayerDto p : players) {
            if (p.getPlateAppearances() > 0) {
                // 11. wRAA (평균 대비 득점 기여)
                // wRAA = ((wOBA - League wOBA) / wOBA Scale) * PA
                double wraa = ((p.getWoba() - leagueWoba) / WOBA_SCALE) * p.getPlateAppearances();
                p.setWraa(round(wraa, 1));
            }
        }
    }

    // 유틸리티: 소수점 반올림
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}