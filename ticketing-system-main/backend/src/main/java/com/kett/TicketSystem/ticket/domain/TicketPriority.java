package com.kett.TicketSystem.ticket.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketPriority {
    URGENT, HIGH, LOW, MEDIUM;

    @JsonCreator
    public static TicketPriority fromString(String value) {
        return switch (value.toUpperCase()) {
            case "URGENT" -> URGENT;
            case "HIGH" -> HIGH;
            case "LOW" -> LOW;
            case "MEDIUM" -> MEDIUM;
            default -> throw new IllegalArgumentException("Invalid priority: " + value);
        };
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase(); // Serialize as lowercase
    }
}

