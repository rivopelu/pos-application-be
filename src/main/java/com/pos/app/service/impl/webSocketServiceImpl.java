package com.pos.app.service.impl;

import com.pos.app.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class webSocketServiceImpl implements WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessage(String endpoint, String message) {
        simpMessagingTemplate.convertAndSend(endpoint, message);
    }
}
