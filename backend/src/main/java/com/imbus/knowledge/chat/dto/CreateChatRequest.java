package com.imbus.knowledge.chat.dto;

import java.util.List;

public class CreateChatRequest {
    private List<Long> participantIds;
    private String name;

    // Getters
    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public String getName() {
        return name;
    }

    // Setters (optional, but recommended)
    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public void setName(String name) {
        this.name = name;
    }
}