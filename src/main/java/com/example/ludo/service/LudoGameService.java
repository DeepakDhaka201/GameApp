package com.example.ludo.service;

import com.example.ludo.dto.*;
import com.example.ludo.service.entity.LudoTable;
import com.example.ludo.service.entity.UserBalance;
import com.example.ludo.service.repo.TableRepo;
import com.example.ludo.service.repo.UserBalanceRepo;
import com.example.ludo.service.util.AppError;
import com.example.ludo.service.util.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;

import static com.example.ludo.service.util.AppUtils.*;
import static com.example.ludo.service.util.Message.INSUFFICIENT_BALANCE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LudoGameService {
    private final LockService lockService;
    private final UserBalanceRepo userBalanceRepo;

    private final TableRepo tableRepo;

    private final DashboardWsService dashboardWsService;

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
            dashboardWsService.publishTables();
        }
    }

    private void createNewLudoTable(String userId, Double amount, LudoType type) {
        //Todo: Check for duplicate tables
        LudoTable ludoTable = new LudoTable();
        ludoTable.setCreatedBy(userId);
        ludoTable.setAmount(amount);
        ludoTable.setPrize(calculatePrize(amount, type));
        ludoTable.setLudoType(type);
        ludoTable.setStatus(TableStatus.NEW);
        ludoTable.setCreatorBalanceDeducted(amount);

        tableRepo.save(ludoTable);
    }

    private Double calculatePrize(Double amount, LudoType type) {
        return Math.ceil(amount * 2 - amount * 0.05);
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
            dashboardWsService.publishTables();
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
            dashboardWsService.publishTables();
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

            String roomCode = ludoKingService.getLudoClassicRoomCode();

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
            dashboardWsService.publishTables();
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
            dashboardWsService.publishTables();
        }
    }


    public void updateTableResult(UpdateResultRequest updateResultRequest, UserDto userDto) {
        LudoTable ludoTable = tableRepo.findById(updateResultRequest.getTableId()).get();

        if (!ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId()) || ludoTable.getAcceptedBy().equals(userDto.getLoggedInUserId())) {
            throw new AppException("Invalid Request");
        }

        if (ludoTable.getStatus().equals(TableStatus.COMPLETED)) {
            throw new AppException("Invalid Request");
        }

        if (ludoTable.getStatus().equals(TableStatus.RUNNING) || ludoTable.getStatus().equals(TableStatus.UNDER_REVIEW)) {
            if (ludoTable.getCreatedBy().equals(userDto.getLoggedInUserId())) {
                tableRepo.updateTableOnCreatorResult(ludoTable.getId(),updateResultRequest.getScreenshotUrl(),
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