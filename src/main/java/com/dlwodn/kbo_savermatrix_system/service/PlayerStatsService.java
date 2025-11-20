package com.dlwodn.kbo_savermatrix_system.service;

import com.dlwodn.kbo_savermatrix_system.domain.Player;
import com.dlwodn.kbo_savermatrix_system.dto.PlayerDto;
import com.dlwodn.kbo_savermatrix_system.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerStatsService {

    private final PlayerRepository repository;

    public PlayerStatsService(PlayerRepository repository) {
        this.repository = repository;
    }

    // 상세 페이지 조회 (ID로 찾기)
    public PlayerDto getPlayerDetail(Long id) {
        Player player = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 선수가 없습니다. id=" + id));
        return convertToDto(player);
    }

    // 투수 랭킹 조회
    public List<PlayerDto> getPitchingRanking(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "era"));
        Page<Player> result = repository.findByPosition("P", pageable);
        return result.getContent().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 타자 랭킹 조회
    public List<PlayerDto> getHittingRanking(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "ops"));
        Page<Player> result = repository.findAll(pageable); // 필터링은 스트림에서
        return result.getContent().stream()
                .filter(p -> p.getPlateAppearances() > 0)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Entity -> DTO 변환 (모든 필드 꼼꼼하게 매핑)
    private PlayerDto convertToDto(Player p) {
        PlayerDto dto = new PlayerDto();

        // ID 및 기본 정보
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPosition(p.getPosition());
        dto.setDefensiveInnings(p.getDefensiveInnings());

        // 투수 스탯
        dto.setInningsPitched(p.getInningsPitched());
        dto.setStrikeouts(p.getStrikeouts());
        dto.setWalks(p.getWalks());
        dto.setIntentionalWalks(p.getIntentionalWalks());
        dto.setHitByPitch(p.getHitByPitch());
        dto.setHomeRuns(p.getHomeRuns());
        dto.setWins(p.getWins());
        dto.setLosses(p.getLosses());
        dto.setHolds(p.getHolds());
        dto.setSaves(p.getSaves());
        dto.setHitsAllowed(p.getHitsAllowed());
        dto.setRunsAllowed(p.getRunsAllowed());
        dto.setEarnedRuns(p.getEarnedRuns());

        dto.setEra(p.getEra());
        dto.setFip(p.getFip());
        dto.setWhip(p.getWhip());
        dto.setKPerNine(p.getKPerNine());
        dto.setBbPerNine(p.getBbPerNine());
        dto.setPfr(p.getPfr());

        // 타자 스탯
        dto.setPlateAppearances(p.getPlateAppearances());
        dto.setSingle(p.getSingle());
        dto.setDoubleBase(p.getDoubleBase());
        dto.setTripleBase(p.getTripleBase());
        dto.setHomeRunBat(p.getHomeRunBat());
        dto.setWalksBat(p.getWalksBat());
        dto.setHitByPitchBat(p.getHitByPitchBat());
        dto.setRuns(p.getRuns());
        dto.setRbi(p.getRbi());
        dto.setStolenBases(p.getStolenBases());
        dto.setSacrificeFlies(p.getSacrificeFlies());
        dto.setSacrificeHits(p.getSacrificeHits());
        dto.setStrikeoutsBat(p.getStrikeoutsBat());

        dto.setBattingAverage(p.getBattingAverage());
        dto.setWrc(p.getWrc());
        dto.setOnBasePercentage(p.getOnBasePercentage());
        dto.setSluggingPercentage(p.getSluggingPercentage());
        dto.setOps(p.getOps());
        dto.setIso(p.getIso());
        dto.setBabip(p.getBabip());
        dto.setWoba(p.getWoba());
        dto.setWraa(p.getWraa());
        dto.setPsn(p.getPsn());
        dto.setGpa(p.getGpa());
        dto.setKbb(p.getKbb());
        dto.setBbk(p.getBbk());

        return dto;
    }
}