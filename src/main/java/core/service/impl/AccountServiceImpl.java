package core.service.impl;

import core.entity.Account;
import core.entity.VerificationToken;
import core.event.OnRegistrationCompleteEvent;
import core.repository.AccountRepo;
import core.repository.VerificationTokenRepo;
import core.service.AccountService;
import core.service.exception.EmailExistsException;
import core.service.exception.EmailNotSentException;
import core.service.exception.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Adrian on 10/05/2015.
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private VerificationTokenRepo tokenRepo;


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Account createAccount(Account acc) throws UsernameExistsException, EmailExistsException {
        if (accountRepo.findAccountByEmail(acc.getEmail()) != null) {
            throw new EmailExistsException("Email already exists.");
        }

        // Hash the password
        acc.setPassword(passwordEncoder.encode(acc.getPassword()));

        return accountRepo.createAccount(acc);
    }

    @Override
    public Account updateAccount(Account acc) {
        return accountRepo.updateAccount(acc);
    }

    @Override
    public VerificationToken createVerificationToken(Account acc, String token) {
        return tokenRepo.createVerificationToken(new VerificationToken(token, acc));
    }

    @Override
    public VerificationToken findVerificationToken(String token) {
        return tokenRepo.findVerificationToken(token);
    }

}
