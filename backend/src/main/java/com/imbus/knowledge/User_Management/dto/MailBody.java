package com.imbus.knowledge.User_Management.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {

}
