package com.example.ludo.service.adapter;

import com.example.ludo.dto.TableStatus;
import com.example.ludo.dto.response.BattleResult;
import com.example.ludo.dto.response.PlayedBattle;
import com.example.ludo.service.entity.LudoTable;

public class PlayedBattleTransformer {
    public static PlayedBattle transform(LudoTable ludoTable, String phone) {
        PlayedBattle playedBattle = new PlayedBattle();

        playedBattle.setId(ludoTable.getId());
        playedBattle.setPrize(ludoTable.getPrize());
        playedBattle.setTimestamp(ludoTable.getCreatedAt());

        if (ludoTable.getStatus().equals(TableStatus.COMPLETED)) {
           BattleResult battleResult = (ludoTable.getWinner() == phone) ? BattleResult.WON : BattleResult.LOST;
            playedBattle.setResult(battleResult);
        }

        if (ludoTable.getStatus().equals(TableStatus.RUNNING) || ludoTable.getStatus().equals(TableStatus.UNDER_REVIEW)) {
            playedBattle.setResult(BattleResult.PENDING);
        }

        if (ludoTable.getStatus().equals(TableStatus.CANCELLED)) {
            playedBattle.setResult(BattleResult.CANCELLED);
        }
        return playedBattle;
    }
}
