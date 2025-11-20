package com.dlwodn.kbo_savermatrix_system.dto;

import lombok.Data;

@Data
public class PlayerDto {
    // 1. 기본 정보
    private String name;
    private String position;
    private double defensiveInnings;

    // 2. 투수 스탯
    private double inningsPitched;
    private int strikeouts;
    private int walks;
    private int intentionalWalks;
    private int hitByPitch;
    private int homeRuns;
    private int wins;
    private int losses;
    private int holds;
    private int saves;
    private int hitsAllowed;
    private int runsAllowed;
    private int earnedRuns;

    private double era;
    private double fip;
    private double whip;
    private double kPerNine;
    private double bbPerNine;

    // 3. 타자 스탯 (희생타, 희생번트 추가!)
    private int plateAppearances;   // 타석
    private int single;
    private int doubleBase;
    private int tripleBase;
    private int homeRunBat;
    private int walksBat;
    private int hitByPitchBat;
    private int runs;
    private int rbi;
    private int stolenBases;

    private int sacrificeFlies;     // ⬅️ 희생플라이 (SF) 추가!
    private int sacrificeHits;      // ⬅️ 희생번트 (SH) 추가!

    // 타자 계산 결과 (타율 추가!)
    private double battingAverage;  // ⬅️ 타율 (AVG) 추가!
    private double wrc;
    private double onBasePercentage;
    private double sluggingPercentage;
    private double ops;
}