package com.example.ludo.service;

import com.example.ludo.dto.TableDto;
import com.example.ludo.dto.TableStatus;
import com.example.ludo.service.adapter.TableDtoTransformer;
import com.example.ludo.service.entity.LudoTable;
import com.example.ludo.service.entity.User;
import com.example.ludo.service.repo.TableRepo;
import com.example.ludo.service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardWsService {
    private final TableRepo tableRepo;
    private final UserRepo userRepo;

    private final SimpMessagingTemplate simpMessagingTemplate;
    public void publishTables() {
        List<TableDto> tableDtos = collectVisibleTables();
        simpMessagingTemplate.convertAndSend("/topic/ludo-dash", tableDtos);
    }

    public List<TableDto> collectVisibleTables() {
        List<TableDto> tableDtos = new ArrayList<>();

        List<TableStatus> tableStatusList = Arrays.asList(TableStatus.NEW, TableStatus.RUNNING, TableStatus.MATCHING);
        List<LudoTable> ludoTables = tableRepo.findAllByStatus();

        List<String> userIds = new ArrayList<>();
        for(LudoTable ludoTable : ludoTables) {
            userIds.add(ludoTable.getCreatedBy());
            userIds.add(ludoTable.getAcceptedBy());
        }

        List<User> users = (List<User>) userRepo.findAllById(userIds);
        Map<String, User> usersById = new HashMap<>();

        for(User user : users) {
            usersById.put(user.getId(), user);
        }

        for(LudoTable ludoTable : ludoTables) {
            tableDtos.add(TableDtoTransformer.transform(ludoTable, usersById));
        }
        return tableDtos;
    }
}