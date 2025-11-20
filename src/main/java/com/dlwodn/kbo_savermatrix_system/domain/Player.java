package com.dlwodn.kbo_savermatrix_system.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 1. 이 클래스는 DB 테이블입니다.
@Getter @Setter
@NoArgsConstructor
public class Player {

    @Id // 2. 주민등록번호(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @JsonProperty("kPerNine")
    private double kPerNine;

    @JsonProperty("bbPerNine")
    private double bbPerNine;

    // ⚡️ 투수 PFR (Power/Finesse Ratio) 추가 ⚡️
    @JsonProperty("pfr")
    private double pfr;

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

    @JsonProperty("wrc")
    private double wrc;

    private double onBasePercentage;
    private double sluggingPercentage;
    private double ops;

    @JsonProperty("iso")
    private double iso;

    @JsonProperty("babip")
    private double babip;

    @JsonProperty("woba")
    private double woba;

    @JsonProperty("wraa")
    private double wraa;

    @JsonProperty("psn")
    private double psn;

    @JsonProperty("gpa")
    private double gpa;

    // 기존 K/BB (유지)
    @JsonProperty("kbb")
    private double kbb;

    // ⚡️ 타자 BB/K (볼넷/삼진) 추가 ⚡️
    @JsonProperty("bbk")
    private double bbk;
}

