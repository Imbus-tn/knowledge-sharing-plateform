package com.imbus.knowledge.User_Management.dto;

import lombok.Builder;

@Builder
public record RegisterRequest (
    String name,
    String password,
    String repeatPassword
) {}
