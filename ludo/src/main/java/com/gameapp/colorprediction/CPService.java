package com.gameapp.colorprediction;

import com.gameapp.colorprediction.entity.CpBet;
import com.gameapp.colorprediction.entity.CpPeriod;
import com.gameapp.colorprediction.repo.CpBetRepo;
import com.gameapp.colorprediction.repo.CpPeriodRepo;
import com.gameapp.core.dto.CPBetStatus;
import com.gameapp.core.dto.CPColor;
import com.gameapp.core.dto.PlaceCPBetRequest;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.request.CancelCPBetRequest;
import com.gameapp.core.dto.response.PlaceCPBetResponse;
import com.gameapp.core.util.AppError;
import com.gameapp.core.util.AppException;
import com.gameapp.corepersistence.entity.UserBalance;
import com.gameapp.corepersistence.repo.UserBalanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


import java.util.List;

import static com.gameapp.core.util.Message.INSUFFICIENT_BALANCE;

@Service
@RequiredArgsConstructor
public class CPService {
    private final CpBetRepo cpBetRepo;
    private final UserBalanceRepo userBalanceRepo;
    private final CpPeriodRepo cpPeriodRepo;

    private static final Double CP_BET_COMMISSION = 5.0;

    @Transactional
    public PlaceCPBetResponse placeCPBet(PlaceCPBetRequest placeCPBetRequest, UserDto userDto) {
        if (!placeCPBetRequest.getPeriodId().equals(getCurrentPeriodId())) {
            throw new AppException(AppError.INVALID_OPERATION, "Invalid period");
        }

        if (!isPeriodOpen()) {
            throw new AppException(AppError.INVALID_OPERATION, "Period is not running");
        }

        UserBalance userBalance = userBalanceRepo.findByUserId(userDto.getLoggedInUserId());

        if (userBalance.getBalance() < placeCPBetRequest.getAmount()) {
            throw new AppException(AppError.INSUFFICIENT_BALANCE, INSUFFICIENT_BALANCE);
        }

        userBalanceRepo.deductBalance(userDto.getLoggedInUserId(), placeCPBetRequest.getAmount());

        CpBet cpBet = new CpBet();
        cpBet.setUserId(userDto.getLoggedInUserId());
        cpBet.setPeriodId(placeCPBetRequest.getPeriodId());
        cpBet.setAmount(placeCPBetRequest.getAmount());
        cpBet.setNumber(placeCPBetRequest.getNumber());
        cpBet.setColor(placeCPBetRequest.getColor());
        cpBet.setStatus(CPBetStatus.PENDING);
        cpBet.setCreatedAt(System.currentTimeMillis());
        cpBet.setUpdatedAt(System.currentTimeMillis());

        cpBet = cpBetRepo.save(cpBet);

        PlaceCPBetResponse placeCPBetResponse = new PlaceCPBetResponse();
        placeCPBetResponse.setBetId(cpBet.getId());
        placeCPBetResponse.setPeriodId(cpBet.getPeriodId());
        return placeCPBetResponse;
    }

    @Transactional
    public void cancelCPBet(CancelCPBetRequest placeCPBetRequest, UserDto userDto) {
        if(!placeCPBetRequest.getPeriodId().equals(getCurrentPeriodId())) {
            throw new AppException(AppError.INVALID_OPERATION, "Invalid period");
        }

        if (!isPeriodOpen()) {
            throw new AppException(AppError.INVALID_OPERATION, "Period is not running");
        }

        CpBet cpBet = cpBetRepo.findById(placeCPBetRequest.getBetId())
                .orElseThrow(() -> new AppException(AppError.BET_NOT_FOUND, "Bet not found"));

        if (!cpBet.getUserId().equals(userDto.getLoggedInUserId())) {
            throw new AppException(AppError.INVALID_OPERATION, "You can not perform this action");
        }

        if (!cpBet.getStatus().equals(CPBetStatus.PENDING)) {
            throw new AppException(AppError.INVALID_OPERATION, "You can not perform this action");
        }

        userBalanceRepo.addBalance(userDto.getLoggedInUserId(), cpBet.getAmount());
        cpBetRepo.updateStatus(cpBet.getId(), CPBetStatus.CANCELLED_BY_USER);
    }

    public Long getCurrentPeriodId() {
        return cpPeriodRepo.findFirstByOrderByIdDesc().getId();
    }

    public CpPeriod getCurrentPeriod() {
        return cpPeriodRepo.findFirstByOrderByIdDesc();
    }

    public Boolean isPeriodOpen() {
        return cpPeriodRepo.findFirstByOrderByIdDesc().getEndTime() - 7000 > System.currentTimeMillis();
    }

    public List<CpBet> getOpenBets(Long id) {
        return cpBetRepo.findAllByPeriodIdAndStatus(id, CPBetStatus.PENDING);
    }

    @Transactional
    public void closePeriod(Long id, Integer winningNumber, CPColor winningColor, Long declaredBy) {
        CpPeriod cpPeriod = cpPeriodRepo.findById(id)
                .orElseThrow(() -> new AppException(AppError.PERIOD_NOT_FOUND, "Period not found"));

        cpPeriod.setActive(false);
        cpPeriod.setNumber(winningNumber);
        cpPeriod.setColor(winningColor);
        cpPeriod.setResultDeclaredBy(declaredBy);
        cpPeriod.setResultDeclaredAt(System.currentTimeMillis());
        cpPeriod.setUpdatedAt(System.currentTimeMillis());

        cpPeriodRepo.save(cpPeriod);
    }

    @Transactional
    public CpPeriod startNewPeriod(Long currentPeriodId, Integer duration) {
        CpPeriod cpPeriod = new CpPeriod();

        cpPeriod.setId(currentPeriodId + 1);
        cpPeriod.setActive(true);
        cpPeriod.setStartTime(System.currentTimeMillis());
        cpPeriod.setEndTime(System.currentTimeMillis() + duration);
        cpPeriod.setCreatedAt(System.currentTimeMillis());
        cpPeriod.setUpdatedAt(System.currentTimeMillis());

        return cpPeriodRepo.save(cpPeriod);
    }

    @Transactional
    public void updateUserBalance(String userId, double value) {
        userBalanceRepo.addBalance(userId, value - value * CP_BET_COMMISSION/100);
    }

    @Transactional
    public void updateBetStatus(Long id, CPBetStatus cpBetStatus) {
        cpBetRepo.updateStatus(id, cpBetStatus);
    }

    public List<CpPeriod> getShortHistory() {
        return cpPeriodRepo.find20ByOrderByIdDesc();
    }

    public List<CpPeriod> getFullHistory() {
        return cpPeriodRepo.find200ByOrderByIdDesc();
    }
}
