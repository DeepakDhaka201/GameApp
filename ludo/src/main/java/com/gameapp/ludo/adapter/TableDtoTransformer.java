package com.gameapp.ludo.adapter;

import com.gameapp.core.dto.TableDto;
import com.gameapp.core.dto.TableUser;
import com.gameapp.ludo.entity.LudoTable;
import com.gameapp.corepersistence.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
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
