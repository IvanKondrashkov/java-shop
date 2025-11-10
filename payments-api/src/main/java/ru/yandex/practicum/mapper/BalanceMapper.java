package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.server.model.BalanceResponse;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BalanceMapper {
    public static BalanceResponse balanceToBalanceResponse(Balance balance) {
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setCurrency(balance.getCurrency());
        balanceResponse.setBalance(balance.getBalance());
        balanceResponse.setUserId(balance.getUserId());
        return balanceResponse;
    }
}