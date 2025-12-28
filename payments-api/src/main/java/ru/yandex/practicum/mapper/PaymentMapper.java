package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.server.model.PaymentResponse;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentMapper {
    public static PaymentResponse paymentToPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(payment.getId());
        paymentResponse.setUserId(payment.getUserId());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setCurrency(payment.getCurrency());
        paymentResponse.setStatus(PaymentResponse.StatusEnum.fromValue(payment.getStatus()));
        paymentResponse.setTimestamp(OffsetDateTime.now());
        return paymentResponse;
    }
}