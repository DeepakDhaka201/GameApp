package com.gameapp.colorprediction;

import com.gameapp.colorprediction.entity.CpBet;
import com.gameapp.colorprediction.entity.CpPeriod;
import com.gameapp.core.dto.CPAlertContext;
import com.gameapp.core.dto.CPBetStatus;
import com.gameapp.core.dto.CPColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CPScheduledTask {
    private final CPService cpService;
    private final AlertService<CPAlertContext> alertService;

    private static final Double NUMBER_BET_MULTIPLIER = 9.0;
    private static final Double RG_COLOR_BET_MULTIPLIER = 2.0;
    private static final Double RGV_COLOR_BET_MULTIPLIER = 1.5;
    private static final Double V_COLOR_BET_MULTIPLIER = 4.5;

    private static final Integer PERIOD_DURATION = 120 * 1000;


    @Transactional
    @Scheduled(fixedRate = 120 * 1000)
    public void run() {
        Long currentPeriodId = null;
        try {
            CpPeriod currentPeriod = cpService.getCurrentPeriod();
            log.info("Current period: {}", currentPeriod);
            System.out.println("Current period: " + currentPeriod);

            if (currentPeriod == null || !currentPeriod.getActive()) {
                log.info("No period found");
                return;
            }
            currentPeriodId = currentPeriod.getId();

            while (System.currentTimeMillis() < currentPeriod.getEndTime() - 7*1000) {
                try {
                    log.info("Waiting for bets to be placed");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Error while waiting for period to end", e);
                    throw new RuntimeException(e);
                }
            }

            List<CpBet> openBets = cpService.getOpenBets(currentPeriod.getId());

            Integer winningNumber = null;
            CPColor winningColor = null;

            if (openBets.isEmpty()) {
                winningNumber = new Random().nextInt(10);
                winningColor = determineWinningColor(winningNumber);
                log.info("No open bets found");
            } else {
                List<CpBet> cpBetList = cpService.getOpenBets(currentPeriod.getId());
                winningNumber = determineWinningNumber(cpBetList);
                winningColor = determineWinningColor(winningNumber);
                settleOpenBets(cpBetList, winningNumber, winningColor);
            }


            cpService.closePeriod(currentPeriod.getId(), winningNumber, winningColor, -1L);
            log.info("Period closed: {}", currentPeriod);

            while (System.currentTimeMillis() < currentPeriod.getEndTime()) {
                try {
                    log.info("Waiting for period to end");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Error while waiting for period to end", e);
                    throw new RuntimeException(e);
                }
            }

            CpPeriod newPeriod = cpService.startNewPeriod(currentPeriod.getId(), PERIOD_DURATION);
            log.info("New period started: {}", newPeriod);
        } catch (Exception e) {
            log.error("Error while running scheduled task", e);
            alertService.sendAlert(buildAlertContext(e, "Error while running scheduled task",
                    currentPeriodId));
        }
    }

    private CPAlertContext buildAlertContext(Exception e, String message, Long periodId) {
        return CPAlertContext.builder()
                .message(message)
                .periodId(periodId.toString())
                .error(e.getMessage())
                .build();
    }

    private void settleOpenBets(List<CpBet> cpBetList, Integer winningNumber, CPColor winningColor) {
        for (CpBet cpBet : cpBetList) {
            if (Objects.nonNull(cpBet.getNumber()) && cpBet.getNumber().equals(winningNumber)) {
                cpService.updateBetStatus(cpBet.getId(), CPBetStatus.WON);
                cpService.updateUserBalance(cpBet.getUserId(), cpBet.getAmount() * NUMBER_BET_MULTIPLIER);
            } else if (Objects.nonNull(cpBet.getColor())) {
                if (cpBet.getColor().equals(winningColor)) {
                    cpService.updateBetStatus(cpBet.getId(), CPBetStatus.WON);
                    cpService.updateUserBalance(cpBet.getUserId(), cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                } else if (cpBet.getColor().equals(CPColor.VIOLET) && (winningNumber == 0 || winningNumber == 5)) {
                    cpService.updateBetStatus(cpBet.getId(), CPBetStatus.WON);
                    cpService.updateUserBalance(cpBet.getUserId(), cpBet.getAmount() * V_COLOR_BET_MULTIPLIER);
                } else if (cpBet.getColor().equals(CPColor.RED) && winningColor.equals(CPColor.VIOLET_RED)) {
                    cpService.updateBetStatus(cpBet.getId(), CPBetStatus.WON);
                    cpService.updateUserBalance(cpBet.getUserId(), cpBet.getAmount() * RGV_COLOR_BET_MULTIPLIER);
                } else if (cpBet.getColor().equals(CPColor.GREEN) && winningColor.equals(CPColor.VIOLET_GREEN)) {
                    cpService.updateBetStatus(cpBet.getId(), CPBetStatus.WON);
                    cpService.updateUserBalance(cpBet.getUserId(), cpBet.getAmount() * RGV_COLOR_BET_MULTIPLIER);
                }
            } else {
                cpService.updateBetStatus(cpBet.getId(), CPBetStatus.LOST);
            }
        }
    }

    private CPColor determineWinningColor(Integer winningNumber) {
        if (winningNumber == 0) {
            return CPColor.VIOLET_RED;
        } else if (winningNumber == 5) {
            return CPColor.VIOLET_GREEN;
        } else if (winningNumber % 2 == 0) {
            return CPColor.RED;
        } else {
            return CPColor.GREEN;
        }
    }

    private Integer determineWinningNumber(List<CpBet> cpBetList) {
        Double totalBetAmount =
                cpBetList.stream().map(CpBet::getAmount).reduce(0.0, Double::sum);

        Map<Integer, Double> buckets = getEmptyBuckets();

        for (CpBet cpBet : cpBetList) {
            if (Objects.nonNull(cpBet.getNumber())) {
                buckets.put(cpBet.getNumber(),
                        buckets.get(cpBet.getNumber()) + cpBet.getAmount() * NUMBER_BET_MULTIPLIER);
            } else if (cpBet.getColor().equals(CPColor.GREEN)) {
                buckets.put(1, buckets.get(1) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(3, buckets.get(3) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(7, buckets.get(7) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(9, buckets.get(9) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);

                buckets.put(5, buckets.get(5) + cpBet.getAmount() * RGV_COLOR_BET_MULTIPLIER);
            } else if (cpBet.getColor().equals(CPColor.RED)) {
                buckets.put(2, buckets.get(2) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(4, buckets.get(4) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(6, buckets.get(6) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);
                buckets.put(8, buckets.get(8) + cpBet.getAmount() * RG_COLOR_BET_MULTIPLIER);

                buckets.put(0, buckets.get(5) + cpBet.getAmount() * RGV_COLOR_BET_MULTIPLIER);
            } else {
                buckets.put(0, buckets.get(0) + cpBet.getAmount() * V_COLOR_BET_MULTIPLIER);
                buckets.put(5, buckets.get(5) + cpBet.getAmount() * V_COLOR_BET_MULTIPLIER);
            }
        }

        log.info("Buckets: {}", buckets);

        Random random = new Random();
        double netProfit = buckets.get(random.nextInt(10)) - totalBetAmount;
        Integer winningNumber = 0;

        for(Map.Entry<Integer, Double> entry : buckets.entrySet()) {
            double profit = totalBetAmount - entry.getValue();
            if (profit >= 0 && profit < netProfit) {
                netProfit = profit;
                winningNumber = entry.getKey();
            } else if (profit < 0 && profit > netProfit) {
                netProfit = profit;
                winningNumber = entry.getKey();
            }
        }

        log.info("Winning number: {} and netProfit of period : {}", winningNumber, netProfit);
        return winningNumber;
    }


    Map<Integer, Double> getEmptyBuckets() {
        Map<Integer, Double> buckets = new HashMap<>();
        buckets.put(0, 0.0);
        buckets.put(1, 0.0);
        buckets.put(2, 0.0);
        buckets.put(3, 0.0);
        buckets.put(4, 0.0);
        buckets.put(5, 0.0);
        buckets.put(6, 0.0);
        buckets.put(7, 0.0);
        buckets.put(8, 0.0);
        buckets.put(9, 0.0);

        return buckets;
    }


}