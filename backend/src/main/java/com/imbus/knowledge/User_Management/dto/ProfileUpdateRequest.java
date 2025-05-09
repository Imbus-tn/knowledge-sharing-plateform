package com.imbus.knowledge.User_Management.dto;

public record ProfileUpdateRequest(
        String name,
        String bio,
        String location,
        String phoneNumber,
        String github,
        String linkedin,
        String avatarUrl
) {}
