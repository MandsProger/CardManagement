package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String number;

    private String ownerUsername;

    @NotNull(message = "UserId is required")
    private Long userId;

    private LocalDate expirationDate;

    private String status;

    private BigDecimal balance;

    public static CardDTO fromEntity(Card card) {
        if (card == null) return null;
        CardDTO dto = new CardDTO();
        dto.id = card.getId();
        dto.number = maskCardNumber(card.getNumber());
        dto.ownerUsername = card.getOwner();
        dto.userId = card.getUser().getId();
        dto.expirationDate = card.getExpirationDate();
        dto.status = card.getStatus() != null ? card.getStatus().name() : null;
        dto.balance = card.getBalance();
        return dto;
    }

    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}

