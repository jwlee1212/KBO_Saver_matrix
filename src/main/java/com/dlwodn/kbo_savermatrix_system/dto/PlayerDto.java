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
    private int homeRuns;

    // FIP(계산 결과)를 담을 추가 필드
    private double fip;
}