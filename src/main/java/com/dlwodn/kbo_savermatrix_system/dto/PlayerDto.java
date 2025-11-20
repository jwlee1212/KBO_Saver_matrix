package com.dlwodn.kbo_savermatrix_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // ⚡️ [핵심] 이거 없으면 데이터 0으로 나옴!
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor  // ⚡️ 필수!
@AllArgsConstructor // ⚡️ 필수!
public class PlayerDto {
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("position")
    private String position;

    @JsonProperty("defensiveInnings")
    private double defensiveInnings;

    // ... (나머지 필드들은 그대로 두셔도 됩니다)
    // 만약 불안하면 아까 드린 '전체 코드'를 그대로 쓰세요.
    // 핵심은 클래스 위에 @NoArgsConstructor 붙이는 것입니다.

    @JsonProperty("inningsPitched")
    private double inningsPitched;
    @JsonProperty("strikeouts")
    private int strikeouts;
    @JsonProperty("walks")
    private int walks;
    @JsonProperty("intentionalWalks")
    private int intentionalWalks;
    @JsonProperty("hitByPitch")
    private int hitByPitch;
    @JsonProperty("homeRuns")
    private int homeRuns;
    @JsonProperty("wins")
    private int wins;
    @JsonProperty("losses")
    private int losses;
    @JsonProperty("holds")
    private int holds;
    @JsonProperty("saves")
    private int saves;
    @JsonProperty("hitsAllowed")
    private int hitsAllowed;
    @JsonProperty("runsAllowed")
    private int runsAllowed;
    @JsonProperty("earnedRuns")
    private int earnedRuns;

    private double era;
    private double fip;
    private double whip;
    @JsonProperty("kPerNine")
    private double kPerNine;
    @JsonProperty("bbPerNine")
    private double bbPerNine;
    private double pfr;

    @JsonProperty("plateAppearances")
    private int plateAppearances;
    @JsonProperty("single")
    private int single;
    @JsonProperty("doubleBase")
    private int doubleBase;
    @JsonProperty("tripleBase")
    private int tripleBase;
    @JsonProperty("homeRunBat")
    private int homeRunBat;
    @JsonProperty("walksBat")
    private int walksBat;
    @JsonProperty("hitByPitchBat")
    private int hitByPitchBat;
    @JsonProperty("runs")
    private int runs;
    @JsonProperty("rbi")
    private int rbi;
    @JsonProperty("stolenBases")
    private int stolenBases;
    @JsonProperty("sacrificeFlies")
    private int sacrificeFlies;
    @JsonProperty("sacrificeHits")
    private int sacrificeHits;
    @JsonProperty("strikeoutsBat")
    private int strikeoutsBat;

    private double battingAverage;
    private double wrc;
    private double onBasePercentage;
    private double sluggingPercentage;
    private double ops;
    private double iso;
    private double babip;
    private double woba;
    private double wraa;
    private double psn;
    private double gpa;
    private double kbb;
    private double bbk;
}