package com.dlwodn.kbo_savermatrix_system;

import com.dlwodn.kbo_savermatrix_system.domain.Player;
import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.repository.PlayerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlayerRepository repository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // 상수 정의 (계산용)
    private static final double FIP_CONSTANT = 3.2;
    private static final double W_SINGLE = 0.7;
    private static final double W_DOUBLE = 1.1;
    private static final double W_TRIPLE = 1.4;
    private static final double W_HR = 1.8;
    private static final double W_WALK = 0.3;
    private static final double WOBA_W_BB = 0.69;
    private static final double WOBA_W_HBP = 0.72;
    private static final double WOBA_W_1B = 0.89;
    private static final double WOBA_W_2B = 1.27;
    private static final double WOBA_W_3B = 1.62;
    private static final double WOBA_W_HR = 2.10;
    private static final double WOBA_SCALE = 1.15;

    public DataLoader(PlayerRepository repository, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            System.out.println("🚀 [System] 초기 데이터 로딩 및 DB 적재 시작...");

            InputStream inputStream = resourceLoader.getResource("classpath:players.json").getInputStream();
            List<PlayerDto> dtos = objectMapper.readValue(inputStream, new TypeReference<List<PlayerDto>>() {});

            // wOBA 계산을 위한 리그 평균 변수
            double totalWobaNum = 0;
            double totalPa = 0;

            // 1차 루프: 엔티티 변환 및 기본 스탯 계산
            for (PlayerDto dto : dtos) {
                Player p = new Player();

                // 기본 정보 복사
                p.setName(dto.getName());
                p.setPosition(dto.getPosition());
                p.setDefensiveInnings(dto.getDefensiveInnings());

                // 투수 Raw Data
                p.setInningsPitched(dto.getInningsPitched());
                p.setStrikeouts(dto.getStrikeouts());
                p.setWalks(dto.getWalks());
                p.setIntentionalWalks(dto.getIntentionalWalks());
                p.setHitByPitch(dto.getHitByPitch());
                p.setHomeRuns(dto.getHomeRuns());
                p.setWins(dto.getWins());
                p.setLosses(dto.getLosses());
                p.setHolds(dto.getHolds());
                p.setSaves(dto.getSaves());
                p.setHitsAllowed(dto.getHitsAllowed());
                p.setRunsAllowed(dto.getRunsAllowed());
                p.setEarnedRuns(dto.getEarnedRuns());

                // 타자 Raw Data
                p.setPlateAppearances(dto.getPlateAppearances());
                p.setSingle(dto.getSingle());
                p.setDoubleBase(dto.getDoubleBase());
                p.setTripleBase(dto.getTripleBase());
                p.setHomeRunBat(dto.getHomeRunBat());
                p.setWalksBat(dto.getWalksBat());
                p.setHitByPitchBat(dto.getHitByPitchBat());
                p.setRuns(dto.getRuns());
                p.setRbi(dto.getRbi());
                p.setStolenBases(dto.getStolenBases());
                p.setSacrificeFlies(dto.getSacrificeFlies());
                p.setSacrificeHits(dto.getSacrificeHits());
                p.setStrikeoutsBat(dto.getStrikeoutsBat()); // 새로 추가된 필드

                // --- ⚾️ 투수 스탯 계산 ---
                if (p.getInningsPitched() > 0) {
                    double ip = p.getInningsPitched();
                    p.setEra(round((p.getEarnedRuns() * 9.0) / ip, 2));

                    double fipNumerator = (13 * p.getHomeRuns()) + (3 * (p.getWalks() + p.getHitByPitch() - p.getIntentionalWalks())) - (2 * p.getStrikeouts());
                    p.setFip(round((fipNumerator / ip) + FIP_CONSTANT, 2));

                    p.setWhip(round((p.getWalks() + p.getHitsAllowed()) / ip, 2));
                    p.setKPerNine(round((p.getStrikeouts() * 9.0) / ip, 1));
                    p.setBbPerNine(round((p.getWalks() * 9.0) / ip, 1));
                    p.setPfr(round((p.getStrikeouts() + p.getWalks()) / ip, 2)); // PFR
                }

                // --- ⚾️ 타자 스탯 계산 ---
                if (p.getPlateAppearances() > 0) {
                    double pa = p.getPlateAppearances();
                    int hits = p.getSingle() + p.getDoubleBase() + p.getTripleBase() + p.getHomeRunBat();
                    double atBats = pa - p.getWalksBat() - p.getHitByPitchBat() - p.getSacrificeFlies() - p.getSacrificeHits();

                    p.setBattingAverage(atBats > 0 ? round((double)hits / atBats, 3) : 0);

                    double totalBases = p.getSingle() + (p.getDoubleBase() * 2.0) + (p.getTripleBase() * 3.0) + (p.getHomeRunBat() * 4.0);
                    p.setSluggingPercentage(atBats > 0 ? round(totalBases / atBats, 3) : 0);

                    double obpNum = hits + p.getWalksBat() + p.getHitByPitchBat();
                    double obpDenom = atBats + p.getWalksBat() + p.getHitByPitchBat() + p.getSacrificeFlies();
                    p.setOnBasePercentage(obpDenom > 0 ? round(obpNum / obpDenom, 3) : 0);

                    p.setOps(round(p.getOnBasePercentage() + p.getSluggingPercentage(), 3));
                    p.setIso(round(p.getSluggingPercentage() - p.getBattingAverage(), 3));

                    double babipNum = hits - p.getHomeRunBat();
                    double babipDenom = atBats - p.getStrikeoutsBat() - p.getHomeRunBat() + p.getSacrificeFlies();
                    p.setBabip(babipDenom > 0 ? round(babipNum / babipDenom, 3) : 0);

                    double psnNum = 2.0 * p.getHomeRunBat() * p.getStolenBases();
                    double psnDenom = p.getHomeRunBat() + p.getStolenBases();
                    p.setPsn(psnDenom > 0 ? round(psnNum / psnDenom, 2) : 0);

                    p.setGpa(round((1.8 * p.getOnBasePercentage() + p.getSluggingPercentage()) / 4.0, 3));

                    double wrc = (p.getSingle() * W_SINGLE) + (p.getDoubleBase() * W_DOUBLE) + (p.getTripleBase() * W_TRIPLE) + (p.getHomeRunBat() * W_HR) + ((p.getWalksBat() + p.getHitByPitchBat()) * W_WALK);
                    p.setWrc(round(wrc, 1));

                    double wobaNum = (WOBA_W_BB * p.getWalksBat()) + (WOBA_W_HBP * p.getHitByPitchBat()) + (WOBA_W_1B * p.getSingle()) + (WOBA_W_2B * p.getDoubleBase()) + (WOBA_W_3B * p.getTripleBase()) + (WOBA_W_HR * p.getHomeRunBat());
                    p.setWoba(pa > 0 ? round(wobaNum / pa, 3) : 0);

                    p.setKbb(p.getWalksBat() > 0 ? round((double)p.getStrikeoutsBat() / p.getWalksBat(), 2) : 0);
                    p.setBbk(p.getStrikeoutsBat() > 0 ? round((double)p.getWalksBat() / p.getStrikeoutsBat(), 2) : 0);

                    totalWobaNum += wobaNum;
                    totalPa += pa;
                }

                repository.save(p); // 1차 저장 (wRAA 제외)
            }

            // 2차 루프: wRAA 계산 (리그 평균 필요)
            double leagueWoba = totalPa > 0 ? totalWobaNum / totalPa : 0;
            List<Player> allPlayers = repository.findAll();

            for (Player p : allPlayers) {
                if (p.getPlateAppearances() > 0) {
                    double wraa = ((p.getWoba() - leagueWoba) / WOBA_SCALE) * p.getPlateAppearances();
                    p.setWraa(round(wraa, 1));
                    repository.save(p); // wRAA 업데이트
                }
            }

            System.out.println("✅ [System] 데이터 적재 완료! 총 " + allPlayers.size() + "명");
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}