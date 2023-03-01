package com.example.ludo.service;

import com.example.ludo.dto.BalanceSummary;
import com.example.ludo.dto.TableStatus;
import com.example.ludo.dto.UserDto;
import com.example.ludo.dto.response.PlayedBattle;
import com.example.ludo.dto.response.ProfileResponse;
import com.example.ludo.service.adapter.PlayedBattleTransformer;
import com.example.ludo.service.entity.LudoTable;
import com.example.ludo.service.entity.User;
import com.example.ludo.service.entity.UserBalance;
import com.example.ludo.service.repo.TableRepo;
import com.example.ludo.service.repo.UserBalanceRepo;
import com.example.ludo.service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        List<LudoTable> ludoTables = tableRepo.findByCreatorIdOrAcceptorIdAndStatus(userId);// tableStatuses.toArray());
        return ludoTables;
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
