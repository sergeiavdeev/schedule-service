package ru.avdeev.scheduleservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final Sender sender;
    private final ObjectMapper objectMapper;

    public Mono<Object> send(String messageType, String exchange, Object message) {

        log.info("Send message to exchange {}: {}", exchange, message);

        return sender.send(getOutboundMessage(exchange, messageType, message))
                .then(Mono.just(message));
    }

    private Mono<OutboundMessage> getOutboundMessage(String exchange, String messageType, Object message) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", messageType);
        AMQP.BasicProperties props = new AMQP.BasicProperties(
                null,
                null,
                headers,
                2,
                null, null, null,
                null, null, null,
                null, null, null, null
        );

        return Mono.just(new OutboundMessage(exchange, "", props, toBytes(message)));
    }

    private byte[] toBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }
}
