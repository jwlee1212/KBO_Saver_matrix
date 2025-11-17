package com.dlwodn.kbo_savermatrix_system.dto; // 우리가 방금 만든 dto 패키지

import lombok.Data;

@Data // (Lombok: Getter, Setter 등을 자동으로 만들어주는 마법)
public class PlayerDto {

    // players.json 파일의 필드와 이름/타입이 정확히 같아야 합니다.
    private String name;
    private double inningsPitched;
    private int strikeouts;
    private int walks;
    private int intentionalWalks;
    private int hitByPitch;
    private int homeRuns;   //피홈런
    private int wins; // 승
    private int losses; // 패
    private int holds; // 홀드
    private int saves; // 세이브
    private int hitsAllowed; // 피안타
    private int runsAllowed; // 실점
    private int earnedRuns; // 자책점

    private double era; // ERA (계산 결과)
    private double whip; // WHIP (계산 결과)
    private double kPerNine; // K/9 (계산 결과)
    private double bbPerNine; // BB/9 (계산 결과)
    private int single;       // 단타 (1루타)
    private int doubleBase;   // 2루타
    private int tripleBase;   // 3루타
    private int homeRunBat;   // 홈런 (타자용)
    private int walksBat;     // 볼넷 (타자용)
    private int hitByPitchBat;// 사구 (타자용)
    private int runs; // 득점
    private int rbi; // 타점
    private int stolenBases; // 도루
    private int plateAppearances; // 타석 (PA)
    private double wrc;      // wRC 결과 필드
    private double onBasePercentage; // 출루율 (OBP)
    private double sluggingPercentage; // 장타율 (SLG)
    private double ops; // OPS (OBP + SLG)

    // FIP(계산 결과)를 담을 추가 필드
    private double fip;
}