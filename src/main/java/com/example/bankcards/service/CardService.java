package com.example.bankcards.service;


import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    /** Создать новую карту для пользователя */
    @Transactional
    public CardDTO createCard(CardDTO cardDTO) {
        Long userId = cardDTO.getUserId();
        if (userId == null) {
            throw new RuntimeException("User ID must be provided in CardDTO");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        BigDecimal balance = cardDTO.getBalance() != null ? cardDTO.getBalance() : BigDecimal.ZERO;

        Card card = Card.builder()
                .number(cardDTO.getNumber())
                .expirationDate(LocalDate.now().plusYears(3))
                .status(cardDTO.getStatus() != null ? Status.valueOf(cardDTO.getStatus()) : Status.ACTIVE)
                .balance(balance)
                .owner(user.getUsername())
                .user(user)
                .build();

        Card savedCard = cardRepository.save(card);
        return CardDTO.fromEntity(savedCard);
    }

    /** Получить пользователя по authentication */
    public User findUserByAuthentication(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user;
    }

    /** Получить карту по ее id  */
    public Card findCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        return card;
    }

    /** Получить карту по id */
    public CardDTO getCardDTO(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        return CardDTO.fromEntity(card);
    }

    /** Получить все карты пользователя (для ADMIN) */
    public List<CardDTO> getAllUserCards(Long userId) {
        List<Card> cards = cardRepository.findAllByUserId(userId);
        return cards.stream()
                .map(card -> new CardDTO(card.getId(), card.getNumber(), card.getOwner(),
                        card.getUser().getId(), card.getExpirationDate(), card.getStatus().name(), card.getBalance())).collect(Collectors.toList());
    }

    /** Получить все свои карты */
    @Transactional
    public List<CardDTO> getAllMyCards(Authentication authentication) {
        User user = findUserByAuthentication(authentication);
        List<Card> cards = cardRepository.findAllByUserId(user.getId());
        System.out.println("User: " + user);
        System.out.println("User's cards: " + user.getCards());
        System.out.println("Количество карт пользователя: " + user.getCards().size());
        return cards.stream()
                .map(card -> new CardDTO(card.getId(), card.getNumber(), card.getOwner(),
                        card.getUser().getId(), card.getExpirationDate(), card.getStatus().name(), card.getBalance())).collect(Collectors.toList());
    }


    /** Получить все карты (для ADMIN) */
    public List<CardDTO> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(CardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** Блокировка карты */
    @Transactional
    public CardDTO blockCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(Status.BLOCKED);
        return CardDTO.fromEntity(cardRepository.save(card));
    }

    /** Подать запрос на блокировку карты */
    @Transactional
    public CardDTO blocQueryCard(Long cardId, Authentication authentication) {
        Card card = findCardById(cardId);
        User user = findUserByAuthentication(authentication);
        if (card.getUser().equals(user)) {
            card.setStatus(Status.LOCK_REQUEST);
        } else
            throw new IllegalArgumentException("Cards belong to different users");

        return CardDTO.fromEntity(cardRepository.save(card));
    }

    /** Активация карты */
    @Transactional
    public CardDTO activateCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(Status.ACTIVE);
        return CardDTO.fromEntity(cardRepository.save(card));
    }

    /** Перевод между своими картами */
    @Transactional
    public void transfer(Long fromCardId, Long toCardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Card fromCard = findCardById(fromCardId);
        Card toCard = findCardById(toCardId);

        if (!fromCard.getUser().getId().equals(toCard.getUser().getId())) {
            throw new IllegalArgumentException("Cards belong to different users");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    /** Удаление карты */
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = findCardById(cardId);
        cardRepository.delete(card);
    }

    /** Просмотр баланса */
    @Transactional
    public BigDecimal checkBalance(Long cardId, Authentication authentication) {
        Card card = findCardById(cardId);

        User user = findUserByAuthentication(authentication);

        if (user.equals(card.getUser())) {
            return card.getBalance();
        } else
            throw new IllegalArgumentException(
                    "Вы можете просматривать баланс только своих карт"
            );
    }
}
