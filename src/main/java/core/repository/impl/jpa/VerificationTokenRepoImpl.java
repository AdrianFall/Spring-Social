package core.repository.impl.jpa;

import core.entity.VerificationToken;
import core.repository.VerificationTokenRepo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by Adrian on 14/05/2015.
 */
@Repository
@Transactional
public class VerificationTokenRepoImpl implements VerificationTokenRepo {

    @PersistenceContext
    private EntityManager emgr;

    @Override
    public VerificationToken createVerificationToken(VerificationToken token) {

        emgr.persist(token);
        emgr.flush();

        return token;
    }

    @Override
    public VerificationToken findVerificationToken(String verificationToken) {
        Query query = emgr.createQuery("SELECT v FROM verification_token v WHERE v.token = :token");
        query.setParameter("token", verificationToken);
        if (!query.getResultList().isEmpty())
            return (VerificationToken) query.getResultList().get(0);
        else
            return null;
    }
}
