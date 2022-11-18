package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Card;
import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.UserRepository;
import com.egs.eval.atm.service.exception.NotAuthenticatedException;
import com.egs.eval.atm.service.exception.NumberOfAttemptsExceededException;
import com.egs.eval.atm.service.model.UserQueryModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankUserServiceTest {
    @InjectMocks
    private BankUserService userService;
    @Mock
    private CardService cardService;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void Given_todayFailsIsNull_When_TryToGetUserByQueryModel_Then_UserWouldBeReturned() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(null);
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(true);
        User result = userService.getUserByQueryModel(queryModel);
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        assertNotNull(result);
        assertEquals(card.getUser(), result);
    }

    @Test
    void Given_todayFailsHasNotReachedToMaxAllowedFails_When_TryToGetUserByQueryModel_Then_UserWouldBeReturned() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(1);
        card.getUser().setDayOfFailLogin(getDayNumber());
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(true);
        User result = userService.getUserByQueryModel(queryModel);
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(repository, times(1)).save(card.getUser());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        assertNotNull(result);
        assertEquals(card.getUser(), result);
    }

    @Test
    void Given_todayFailsHasReachedToMaxAllowedFails_When_TryToGetUserByQueryModel_Then_NumberOfAttemptsExceededExceptionWouldBeThrown() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(3);
        card.getUser().setDayOfFailLogin(getDayNumber());
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        assertThrows(NumberOfAttemptsExceededException.class, () -> userService.getUserByQueryModel(queryModel));
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
    }

    @Test
    void Given_todayFailsHasReachedToMaxAllowedFailsButDayIsChanged_When_TryToGetUserByQueryModel_Then_NumberOfAttemptsExceededExceptionWouldBeReturned() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(3);
        card.getUser().setDayOfFailLogin(getDayNumber() - 1);
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(true);
        User result = userService.getUserByQueryModel(queryModel);
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(repository, times(1)).save(card.getUser());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        assertNotNull(result);
        assertEquals(card.getUser(), result);
        assertNull(card.getUser().getTodayFailedLoginAttempts());
        assertNull(card.getUser().getDayOfFailLogin());
    }

    @Test
    void Given_AuthenticationTypeIsPinAndItsCorrect_When_TryToGetUserByQueryModel_Then_UserWouldBeReturned() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(null);
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(true);
        User result = userService.getUserByQueryModel(queryModel);
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        assertNotNull(result);
        assertEquals(card.getUser(), result);
    }

    @Test
    void Given_AuthenticationTypeIsFingerprintAndItsCorrect_When_TryToGetUserByQueryModel_Then_UserWouldBeReturned() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(null);
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(false);
        when(passwordEncoder.matches(queryModel.getFingerprint(), card.getUser().getFingerprint())).thenReturn(true);
        User result = userService.getUserByQueryModel(queryModel);
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        verify(passwordEncoder, times(1)).matches(queryModel.getFingerprint(), card.getUser().getFingerprint());
        assertNotNull(result);
        assertEquals(card.getUser(), result);
    }

    @Test
    void Given_AuthenticationIsIncorrect_When_TryToGetUserByQueryModel_Then_NotAuthenticatedExceptionWouldBeThrown() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(null);
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(false);
        when(passwordEncoder.matches(queryModel.getFingerprint(), card.getUser().getFingerprint())).thenReturn(false);
        assertThrows(NotAuthenticatedException.class, () -> userService.getUserByQueryModel(queryModel));
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(repository, times(1)).save(card.getUser());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        verify(passwordEncoder, times(1)).matches(queryModel.getFingerprint(), card.getUser().getFingerprint());
        assertEquals(1, card.getUser().getTodayFailedLoginAttempts());
        assertEquals(getDayNumber(), card.getUser().getDayOfFailLogin());
    }

    @Test
    void Given_AuthenticationIsIncorrectForSeveralTimes_When_TryToGetUserByQueryModel_Then_NotAuthenticatedExceptionWouldBeThrown() {
        Card card = podamFactory.manufacturePojo(Card.class);
        UserQueryModel queryModel = podamFactory.manufacturePojo(UserQueryModel.class);
        card.getUser().setTodayFailedLoginAttempts(1);
        card.getUser().setDayOfFailLogin(getDayNumber());
        when(cardService.getUserByCardNumber(queryModel.getCard())).thenReturn(card.getUser());
        when(passwordEncoder.matches(queryModel.getPin(), card.getUser().getPin())).thenReturn(false);
        when(passwordEncoder.matches(queryModel.getFingerprint(), card.getUser().getFingerprint())).thenReturn(false);
        assertThrows(NotAuthenticatedException.class, () -> userService.getUserByQueryModel(queryModel));
        verify(cardService, times(1)).getUserByCardNumber(queryModel.getCard());
        verify(repository, times(1)).save(card.getUser());
        verify(passwordEncoder, times(1)).matches(queryModel.getPin(), card.getUser().getPin());
        verify(passwordEncoder, times(1)).matches(queryModel.getFingerprint(), card.getUser().getFingerprint());
        assertEquals(2, card.getUser().getTodayFailedLoginAttempts());
    }

    private long getDayNumber() {
        return new Date().getTime() / (24L * 3600000);
    }
}
