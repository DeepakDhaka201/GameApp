package com.example.ludo.service.adapter;

import com.example.ludo.dto.TableDto;
import com.example.ludo.dto.TableUser;
import com.example.ludo.service.entity.LudoTable;
import com.example.ludo.service.entity.User;

import java.util.Map;
import java.util.Objects;

public class TableDtoTransformer {
    public static TableDto transform(LudoTable ludoTable, Map<String, User> usersById) {
        TableDto tableDto = new TableDto();
        tableDto.setTableId(ludoTable.getId());
        tableDto.setPrize(ludoTable.getPrize());
        tableDto.setAmount(ludoTable.getAmount());
        tableDto.setTableStatus(ludoTable.getStatus());

        User createdBy = usersById.get(ludoTable.getCreatedBy());
        User acceptedBy = usersById.get(ludoTable.getAcceptedBy());

        if (Objects.nonNull(createdBy)) {
            TableUser creator = TableUser.builder().id(createdBy.getId())
                    .name(createdBy.getUserName())
                    .avatar(createdBy.getAvatar())
                    .build();
            tableDto.setCreator(creator);
        }

        if (Objects.nonNull(acceptedBy)) {
            TableUser acceptor = TableUser.builder().id(acceptedBy.getId())
                    .name(createdBy.getUserName())
                    .avatar(createdBy.getAvatar())
                    .build();
            tableDto.setAcceptor(acceptor);
        }
        return tableDto;
    }
}
