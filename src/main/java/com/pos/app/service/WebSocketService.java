package com.pos.app.service;

public interface WebSocketService {
    void sendMessage(String endpoint, String message);
}
