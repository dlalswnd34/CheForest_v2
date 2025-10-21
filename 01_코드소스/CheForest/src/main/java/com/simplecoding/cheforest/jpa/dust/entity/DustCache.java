package com.simplecoding.cheforest.jpa.dust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DUST_CACHE") // DB 테이블 이름 그대로 명시
@Getter
@Setter
public class DustCache {

    @Id
    private String sido;        // 시/도 이름 (PK)

    private String pm10;        // 미세먼지

    private String pm25;        // 초미세먼지

    @Column(name = "PM10_GRADE", length = 10)
    private String pm10G;   // 미세먼지 등급

    @Column(name = "PM25_GRADE", length = 10)
    private String pm25G;   // 초미세먼지 등급

    private String dataTime;    // 측정 시각

    @Column(name = "RESULT_CODE", length = 10)
    private String resultCode;  // "OK" or "EX"
}
