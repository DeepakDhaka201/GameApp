package com.gameapp.ludo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LudoKingRoomCodeResponse {
    @JsonProperty("roomcode")
    private String roomCode;
}
