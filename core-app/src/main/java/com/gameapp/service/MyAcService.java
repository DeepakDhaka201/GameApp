package com.gameapp.service;

import com.gameapp.service.adapter.PlayedBattleTransformer;
import com.gameapp.core.dto.BalanceSummary;
import com.gameapp.core.dto.TableStatus;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.response.PlayedBattle;
import com.gameapp.core.dto.response.ProfileResponse;
import com.gameapp.ludo.entity.LudoTable;
import com.gameapp.corepersistence.entity.User;
import com.gameapp.corepersistence.entity.UserBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gameapp.ludo.repo.TableRepo;
import com.gameapp.corepersistence.repo.UserBalanceRepo;
import com.gameapp.corepersistence.repo.UserRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyAcService {
    private final UserRepo userRepo;
    private final UserBalanceRepo userBalanceRepo;

    private final TableRepo tableRepo;
    public ProfileResponse getProfile(UserDto userDto) {
        String userId = userDto.getLoggedInUserId();
        User user = userRepo.findById(userId).get();
        UserBalance userBalance = userBalanceRepo.findByUserId(userId);

        return ProfileResponse.builder()
                .username(user.getUserName())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .kycStatus(user.getKycStatus())
                .totalWin(userBalance.getTotalWon())
                .playedBattles(getValidTables(userId).size())
                .build();
    }

    public List<PlayedBattle> getPlayedBattles(UserDto userDto) {
        List<PlayedBattle> playedBattles = new ArrayList<>();
        String userId = userDto.getLoggedInUserId();

        List<LudoTable> ludoTables = getValidTables(userId);
        for(LudoTable ludoTable : ludoTables) {
            playedBattles.add(PlayedBattleTransformer.transform(ludoTable, userDto.getLoggedInUserId()));
        }
        return playedBattles;
    }

    private List<LudoTable> getValidTables(String userId) {
        List<TableStatus> tableStatuses = Arrays.asList(TableStatus.RUNNING, TableStatus.COMPLETED,
                TableStatus.UNDER_REVIEW, TableStatus.CANCELLED);

        return tableRepo.findByCreatorIdOrAcceptorIdAndStatus(userId);
    }

    public BalanceSummary getBalanceSummary(UserDto userDto) {
        String userId = userDto.getLoggedInUserId();
        UserBalance userBalance = userBalanceRepo.findByUserId(userId);

        return BalanceSummary.builder()
                .totalWinning(userBalance.getTotalWon())
                .referralEarning(userBalance.getTotalReferral())
                .balance(userBalance.getBalance())
                .onHold(userBalance.getOnHold())
                .build();
    }
}
