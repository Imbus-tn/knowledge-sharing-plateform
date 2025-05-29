package com.imbus.knowledge.Content_Management.entities;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ReactionType {
    LIKE("👍"),
    LOVE("❤️"),
    HAHA("😊"),
    CELEBRATE("🎉"),
    WOW("🤔"),
    CLAP("👏"),
    FIRE("🔥"),
    HUNDRED("💯"),
    SPARKLE("✨"),
    RAISE("🙌");

    private final String emoji;

    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public static String getEmoji(String type) {
        try {
            return valueOf(type.toUpperCase()).emoji;
        } catch (IllegalArgumentException e) {
            // If type not found, assume it's already an emoji (from frontend)
            return isEmoji(type) ? type : LIKE.emoji;
        }
    }

    // Utility to check if a string is an emoji
    private static final Set<String> SUPPORTED_EMOJIS = Arrays.stream(values())
            .map(ReactionType::getEmoji)
            .collect(Collectors.toSet());

    private static boolean isEmoji(String value) {
        return SUPPORTED_EMOJIS.contains(value);
    }
}