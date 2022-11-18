package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Card;
import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.CardRepository;
import com.egs.eval.atm.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankCardServiceTest {
    @InjectMocks
    private BankCardService cardService;
    @Mock
    private CardRepository repository;

    private final PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void Given_CardExistsWithGivenCardNumber_When_TryToGetUserByCardNumber_Then_UserWouldBeReturn() {
        String cardNumber = podamFactory.manufacturePojo(String.class);
        Card card = podamFactory.manufacturePojo(Card.class);
        when(repository.findByCardNumber(cardNumber)).thenReturn(Optional.of(card));
        User result = cardService.getUserByCardNumber(cardNumber);
        verify(repository, times(1)).findByCardNumber(cardNumber);
        assertNotNull(result);
        assertEquals(card.getUser(), result);
    }

    @Test
    void Given_CardDoesNotExistsWithGivenCardNumber_When_TryToGetUserByCardNumber_Then_NotFoundExceptionWouldBeThrown() {
        String cardNumber = podamFactory.manufacturePojo(String.class);
        when(repository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.getUserByCardNumber(cardNumber));
        verify(repository, times(1)).findByCardNumber(cardNumber);
    }
}
