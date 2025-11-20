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

    // ⚡️ 누락되었던 투수 고급 스탯 추가! ⚡️
    private double kbb;             // 볼삼비 (K/BB)

    // 3. 타자 스탯
    private int plateAppearances;
    private int single;
    private int doubleBase;
    private int tripleBase;
    private int homeRunBat;
    private int walksBat;
    private int hitByPitchBat;
    private int runs;
    private int rbi;
    private int stolenBases;

    private int sacrificeFlies;
    private int sacrificeHits;
    private int strikeoutsBat;

    // 타자 계산 결과
    private double battingAverage;
    private double wrc;
    private double onBasePercentage;
    private double sluggingPercentage;
    private double ops;

    // ⚡️ 누락되었던 타자 고급 스탯 추가! ⚡️
    private double iso;             // 순수 장타율 (ISO)
    private double babip;           // 인플레이 타구 타율 (BABIP)
    private double woba;            // 가중 출루율 (wOBA)
    private double wraa;            // 평균 대비 득점 기여 (wRAA)
    private double psn;             // 호타준족 지수 (PSN)
    private double gpa;             // GPA
}