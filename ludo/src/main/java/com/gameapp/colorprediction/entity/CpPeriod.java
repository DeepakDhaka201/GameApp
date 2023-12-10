package com.gameapp.colorprediction.entity;

import com.gameapp.core.dto.CPColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CpPeriod {
    @Id
    private Long id;

    private Long startTime;
    private Long endTime;
    private Boolean active;

    private Integer number;
    private CPColor color;

    private Long resultDeclaredAt;
    private Long resultDeclaredBy;

    private Long createdAt;
    private Long updatedAt;
    private Boolean settled;
}
