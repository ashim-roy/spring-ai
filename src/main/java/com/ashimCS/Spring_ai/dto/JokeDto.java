package com.ashimCS.Spring_ai.dto;

public record JokeDto(
        String text,
        String category,
        Double laughScore,
        Boolean isNSFW             // not safe for work
) {
}
