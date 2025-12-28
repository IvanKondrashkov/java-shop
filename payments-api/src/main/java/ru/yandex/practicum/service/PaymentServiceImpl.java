package ru.yandex.practicum.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Balance;
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
                .flatMap(balance -> processWithBalance(balance, paymentRequest))
                .map(PaymentMapper::paymentToPaymentResponse);
    }

    private Mono<Payment> processWithBalance(Balance balance, PaymentRequest request) {
        return hasEnoughFunds(balance, request)
                ? processSuccessfulPayment(balance, request)
                : processFailedPayment(request);
    }

    private boolean hasEnoughFunds(Balance balance, PaymentRequest request) {
        return balance.getBalance().compareTo(request.getAmount()) >= 0;
    }

    private Mono<Payment> processFailedPayment(PaymentRequest request) {
        Payment payment = buildPayment(request, PaymentResponse.StatusEnum.FAILED.getValue());
        return paymentRepository.save(payment);
    }

    private Mono<Payment> processSuccessfulPayment(Balance balance, PaymentRequest request) {
        balance.setBalance(balance.getBalance().subtract(request.getAmount()));
        Payment payment = buildPayment(request, PaymentResponse.StatusEnum.SUCCESS.getValue());

        return balanceRepository.save(balance)
                .then(paymentRepository.save(payment));
    }

    private Payment buildPayment(PaymentRequest request, String status) {
        return Payment.builder()
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .status(status)
                .createdAt(LocalDateTime.now())
                .userId(request.getUserId())
                .build();
    }
}