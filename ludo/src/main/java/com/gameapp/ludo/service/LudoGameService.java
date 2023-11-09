package com.gameapp.ludo.service;
import com.gameapp.core.dto.*;
import com.gameapp.ludo.entity.LudoTable;
import com.gameapp.ludo.repo.TableRepo;
import com.gameapp.corepersistence.entity.UserBalance;
import com.gameapp.core.lock.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gameapp.corepersistence.repo.UserBalanceRepo;
import com.gameapp.core.util.AppError;
import com.gameapp.core.util.AppException;

import java.time.Instant;

import static com.gameapp.core.util.AppUtils.getTableLockKey;
import static com.gameapp.core.util.AppUtils.getWalletLockKey;
import static com.gameapp.core.util.Message.INSUFFICIENT_BALANCE;


@Slf4j
@Service
@RequiredArgsConstructor
public class LudoGameService {
    private final LockService lockService;
    private final UserBalanceRepo userBalanceRepo;

    private final TableRepo tableRepo;

    private final LudoLobbyWsService ludoLobbyWsService;

    private final LudoKingService ludoKingService;

    @Transactional
    public void createTable(UserDto userDto, CreateLudoTableRequest createLudoTableRequest) throws InterruptedException {
        String userId = userDto.getLoggedInUserId();
        Double amount = createLudoTableRequest.getAmount();
        String walletLockKey = getWalletLockKey(userId);
        boolean walletLockTaken = false;

        try {
            walletLockTaken = lockService.waitForLock(walletLockKey, 30);
            UserBalance userBalance = userBalanceRepo.findByUserId(userId);
            if (userBalance.getBalance() >= amount) {
                createNewLudoTable(userId, amount, createLudoTableRequest.getType());
            } else {
                throw new AppException(AppError.INSUFFICIENT_BALANCE, INSUFFICIENT_BALANCE);
            }
        } catch (Exception exception) {
            log.error("error while creating ludo table : {} , {}", userId, createLudoTableRequest);
            throw exception;
        } finally {
            if (walletLockTaken) {
                lockService.releaseLock(walletLockKey);
            }
            ludoLobbyWsService.publishTables();
        }
    }

    private void createNewLudoTable(String userId, Double amount, AppGame type) {
        if (tableRepo.findByCreatedByAndAmountAndLudoType(userId, amount, type).isPresent()) {
            throw new AppException("Table already exists");
        }

        LudoTable ludoTable = new LudoTable();
        ludoTable.setCreatedBy(userId);
        ludoTable.setAmount(amount);
        ludoTable.setPrize(calculatePrize(amount, type));
        ludoTable.setLudoType(type);
        ludoTable.setStatus(TableStatus.NEW);

        tableRepo.save(ludoTable);
    }

    private Double calculatePrize(Double amount, AppGame type) {
        CommissionLevel commissionLevel = AppGame.getCommisionLevel(AppGame.LUDO_CLASSIC, amount);
        return Math.ceil(amount * 2 - amount * commissionLevel.getCommission()/100);
    }

    @Transactional
    public void requestJoinLudoTable(Long tableId, UserDto userDto) throws InterruptedException {
        String tableLockKey = getTableLockKey(tableId);
        String walletLockKey = getWalletLockKey(userDto.getLoggedInUserId());
        boolean tableLockTaken = false;
        boolean walletLockTaken = false;

        try {
            tableLockTaken = lockService.waitForLock(tableLockKey, 5);

            LudoTable ludoTable = tableRepo.findById(tableId).get();
            if (ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())) {
                throw new AppException("Invalid Request");
            }
            if (ludoTable.getStatus() == TableStatus.NEW) {
                walletLockTaken = lockService.waitForLock(walletLockKey, 5);
                UserBalance userBalance = userBalanceRepo.findByUserId(userDto.getLoggedInUserId());
                if (userBalance.getBalance() >= ludoTable.getAmount()) {
                    tableRepo.updateTableOnAcceptRequest(tableId, TableStatus.MATCHING, userDto.getLoggedInUserId());
                } else {
                    throw new AppException(AppError.INSUFFICIENT_BALANCE, INSUFFICIENT_BALANCE);
                }
            } else {
                throw new AppException("Table not available. Please retry");
            }
        } finally {
            if (walletLockTaken) {
                lockService.releaseLock(walletLockKey);
            }

            if (tableLockTaken) {
                lockService.releaseLock(tableLockKey);
            }
            ludoLobbyWsService.publishTables();
        }
    }

    public void deleteLudoTable(Long tableId, UserDto userDto) throws InterruptedException {
        String tableLockKey = getTableLockKey(tableId);
        boolean tableLockTaken = false;

        try {
            tableLockTaken = lockService.waitForLock(tableLockKey, 5);

            LudoTable ludoTable = tableRepo.findById(tableId).get();
            if (!ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())) {
                throw new AppException("Invalid Request");
            }

            if (ludoTable.getStatus() == TableStatus.NEW) {
                tableRepo.updateTableStatus(tableId, TableStatus.DELETED);
            } else {
                throw new AppException("Invalid Request");
            }
        } finally {
            if (tableLockTaken) {
                lockService.releaseLock(tableLockKey);
            }
            ludoLobbyWsService.publishTables();
        }
    }

    @Transactional
    public void acceptJoinLudoTableRequest(Long tableId, UserDto userDto) throws InterruptedException {
        String tableLockKey = getTableLockKey(tableId);
        String creatorWalletLockKey = null;
        String acceptorWalletLockKey = null;
        boolean tableLockTaken = false;
        boolean creatorWalletLockTaken = false;
        boolean acceptorWalletLockTaken = false;

        try {
            tableLockTaken = lockService.waitForLock(tableLockKey, 5);
            LudoTable ludoTable = tableRepo.findById(tableId).get();
            if (!ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())) {
                throw new AppException("Invalid Request");
            }

            if (!ludoTable.getStatus().equals(TableStatus.MATCHING)) {
                throw new AppException("Invalid Request");
            }

            String roomCode = ludoKingService.getLudoKingRoomCode(ludoTable.getLudoType());

            creatorWalletLockKey = getWalletLockKey(ludoTable.getCreatedBy());
            acceptorWalletLockKey = getWalletLockKey(ludoTable.getAcceptedBy());

            creatorWalletLockTaken = lockService.acquireLock(creatorWalletLockKey, 5);
            acceptorWalletLockTaken = lockService.acquireLock(acceptorWalletLockKey, 5);

            UserBalance creatorBalance = userBalanceRepo.findByUserId(ludoTable.getCreatedBy());
            UserBalance acceptorBalance = userBalanceRepo.findByUserId(ludoTable.getAcceptedBy());

            if (creatorBalance.getBalance() < ludoTable.getAmount() ||
                    acceptorBalance.getBalance() < ludoTable.getAmount() ) {
                throw new AppException(AppError.INSUFFICIENT_BALANCE, INSUFFICIENT_BALANCE);
            }

            userBalanceRepo.deductBalanceAndUpdateOnHold(ludoTable.getCreatedBy(), ludoTable.getAmount());
            userBalanceRepo.deductBalanceAndUpdateOnHold(ludoTable.getAcceptedBy(), ludoTable.getAmount());
            tableRepo.updateTableOnAccept(tableId, roomCode, TableStatus.RUNNING, ludoTable.getAmount(),
                    Instant.now().toEpochMilli());
        } finally {
            if (tableLockTaken) {
                lockService.releaseLock(tableLockKey);
            }

            if (creatorWalletLockTaken) {
                lockService.releaseLock(creatorWalletLockKey);
            }

            if (acceptorWalletLockTaken) {
                lockService.releaseLock(acceptorWalletLockKey);
            }
            ludoLobbyWsService.publishTables();
        }
    }

    @Transactional
    public void rejectJoinLudoTableRequest(Long tableId, UserDto userDto) throws InterruptedException {
        String tableLockKey = getTableLockKey(tableId);
        boolean tableLockTaken = false;

        try {
            tableLockTaken = lockService.waitForLock(tableLockKey, 10);
            LudoTable ludoTable = tableRepo.findById(tableId).get();

            if (!ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())) {
                throw new AppException("Invalid Request");
            }

            if (!ludoTable.getStatus().equals(TableStatus.MATCHING)) {
                throw new AppException("Invalid Request");
            }

            System.out.println(tableId);
            tableRepo.updateTableOnAcceptRequest(tableId, TableStatus.NEW, null);
        } finally {
            if (tableLockTaken) {
                lockService.releaseLock(tableLockKey);
            }
            ludoLobbyWsService.publishTables();
        }
    }


    public void updateTableResult(UpdateResultRequest updateResultRequest, UserDto userDto) {
        LudoTable ludoTable = tableRepo.findById(updateResultRequest.getTableId()).get();

        if (!(ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())
                || ludoTable.getAcceptedBy().equals(userDto.getLoggedInUserId()))) {
            throw new AppException("Invalid Request");
        }

        if (ludoTable.getStatus().equals(TableStatus.COMPLETED)) {
            throw new AppException("Invalid Request");
        }

        if (ludoTable.getStatus().equals(TableStatus.RUNNING) || ludoTable.getStatus().equals(TableStatus.UNDER_REVIEW)) {
            if (ludoTable.getCreatedBy().equalsIgnoreCase(userDto.getLoggedInUserId())) {
                tableRepo.updateTableOnCreatorResult(ludoTable.getId(), updateResultRequest.getScreenshotUrl(),
                        updateResultRequest.getStatus(), updateResultRequest.getReason(), TableStatus.UNDER_REVIEW);
            }

            if (ludoTable.getAcceptedBy().equals(userDto.getLoggedInUserId())) {
                tableRepo.updateTableOnAcceptorResult(ludoTable.getId(), updateResultRequest.getScreenshotUrl(),
                        updateResultRequest.getStatus(), updateResultRequest.getReason(), TableStatus.UNDER_REVIEW);
            }
        } else {
            throw new AppException("Invalid Request");
        }
    }

    public String getRoomCode(Long tableId, UserDto userDto) {
        LudoTable ludoTable = tableRepo.findById(tableId).get();
        if (userDto.getLoggedInUserId().equals(ludoTable.getCreatedBy()) ||
                userDto.getLoggedInUserId().equals(ludoTable.getAcceptedBy())) {
            return ludoTable.getRoomCode();
        } else {
            throw new AppException("Invalid Request");
        }
    }
}