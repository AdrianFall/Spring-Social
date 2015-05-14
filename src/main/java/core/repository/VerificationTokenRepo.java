package core.repository;

import core.entity.VerificationToken;

/**
 * Created by Adrian on 14/05/2015.
 */
public interface VerificationTokenRepo {
    public VerificationToken createVerificationToken(VerificationToken token);
    public VerificationToken findVerificationToken(String verificationToken);
}
