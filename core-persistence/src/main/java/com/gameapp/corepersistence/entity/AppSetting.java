package com.gameapp.corepersistence.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
@Data
public class AppSetting {
    @Id
    private String id;
    private String name;
}