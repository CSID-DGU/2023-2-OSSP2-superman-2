package com.superman.backend.DTO;

import lombok.Data;


@Data
public class SessionDataDto {
    private String sessionId;
    private int HomeType;
    private int TransportationType;
    private String OftenPlace;
    private double FuelCost;
}

