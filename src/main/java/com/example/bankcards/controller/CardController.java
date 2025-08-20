package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Validated
public class CardController {

    private final CardService cardService;

    /** Посмотреть все карты пользователя
     * (ADMIN) - просматривает у всех
    **/
    @GetMapping("/userCards/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDTO>> getAllUserCards(@PathVariable Long userId) {
        List<CardDTO> cards = cardService.getAllUserCards(userId);
        return ResponseEntity.ok(cards);
    }

    /** Посмотреть все карты пользователя
     * (USER) - просматривает свои карты
     **/
    @GetMapping("/userCards/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDTO>> getAllMyCards(Authentication authentication) {
        List<CardDTO> cards = cardService.getAllMyCards(authentication);
        return ResponseEntity.ok(cards);
    }

    /** Создать новую карту (только для ADMIN) */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
        CardDTO createdCard = cardService.createCard(cardDTO);
        return ResponseEntity.ok(createdCard);
    }

    /** Получить карту по id*/
    @GetMapping("get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        CardDTO card = cardService.getCardDTO(id);
        return ResponseEntity.ok(card);
    }

    /** Удалить карту (только для ADMIN) */
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    /** Заявка на блокировку карты */
    @PostMapping("blockQuery/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> blockQueryCard(@PathVariable Long id, Authentication authentication) {
        cardService.blocQueryCard(id, authentication);
        return ResponseEntity.noContent().build();
    }

    /** Блокировка карты (только для ADMIN) */
    @PostMapping("block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> blockCard(@PathVariable Long id) {
        CardDTO blockCard = cardService.blockCard(id);
        return ResponseEntity.ok(blockCard);
    }

    /** Активация карты (только для ADMIN) */
    @PostMapping("activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> activateCard(@PathVariable Long id) {
        CardDTO activatedCard = cardService.activateCard(id);
        return ResponseEntity.ok(activatedCard);
    }

    /** Перевод между своими картами */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        cardService.transfer(request.getFromCardId(), request.getToCardId(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    /** Проверить баланс карты (только для USER) */
    @GetMapping("/balance/{cardId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> checkBalance(@PathVariable Long cardId, Authentication authentication) {
        BigDecimal balance = cardService.checkBalance(cardId, authentication);
        return ResponseEntity.ok(balance);
    }
}