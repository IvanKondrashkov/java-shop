package ru.yandex.practicum.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.BalanceRepository;
import ru.yandex.practicum.repository.UserRepository;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.server.model.PaymentRequest;
import ru.yandex.practicum.server.model.PaymentResponse;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Mono<PaymentResponse> processPayment(PaymentRequest paymentRequest) {
        return userRepository.findById(paymentRequest.getUserId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found!")))
                .then(balanceRepository.findByUserId(paymentRequest.getUserId()))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Balance not found!")))
                .flatMap(balance -> {
                    Payment payment = Payment.builder()
                            .currency(paymentRequest.getCurrency())
                            .amount(paymentRequest.getAmount())
                            .status(PaymentResponse.StatusEnum.SUCCESS.getValue())
                            .createdAt(LocalDateTime.now())
                            .userId(paymentRequest.getUserId())
                            .build();

                    if (balance.getBalance().compareTo(paymentRequest.getAmount()) < 0) {
                        payment.setStatus(PaymentResponse.StatusEnum.FAILED.getValue());
                        return paymentRepository.save(payment);
                    }
                    balance.setBalance(balance.getBalance().subtract(paymentRequest.getAmount()));
                    return balanceRepository.save(balance).then(paymentRepository.save(payment));
                })
                .map(PaymentMapper::paymentToPaymentResponse);
    }
}