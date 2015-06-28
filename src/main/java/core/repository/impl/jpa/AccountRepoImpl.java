package core.repository.impl.jpa;

import core.entity.Account;
import core.entity.Role;
import core.repository.AccountRepo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adrian on 10/05/2015.
 */
@Repository
@Transactional
public class AccountRepoImpl implements AccountRepo {

    @PersistenceContext
    private EntityManager emgr;

    @Override
    public Account findAccount(Long id) {
        return emgr.find(Account.class, id);
    }

    @Override
    public Account findAccountByUsername(String username) {
        Query query = emgr.createQuery("SELECT a FROM Account a WHERE a.username = :username");
        query.setParameter("username", username);

        if (!query.getResultList().isEmpty())
            return (Account) query.getResultList().get(0);
        else
            return null;
    }

    @Override
    public Account findAccountByEmail(String email) {
        Query query = emgr.createQuery("SELECT a FROM Account a WHERE a.email = :email");
        query.setParameter("email", email);

        if (!query.getResultList().isEmpty())
            return (Account) query.getResultList().get(0);
        else
            return null;
    }

    @Override
    public Account createAccount(Account acc) {
        // Set account disabled by default
        acc.setEnabled(false);

        // Find the user role
        Query query = emgr.createQuery("SELECT r FROM Role r WHERE r.role = :role");
        query.setParameter("role", "ROLE_USER");

        if (query.getResultList().isEmpty())
            return null;

        Role role = (Role) query.getResultList().get(0);

        // Create new account roles obj
        Set<Role> accRoles = new HashSet<Role>();
        accRoles.add(role);
        acc.setAccRoles(accRoles);

        emgr.persist(acc);
        emgr.flush();

        // Attach the role to the acc obj
       /* Set<Role> roles = new HashSet<Role>(0);
        roles.add(role);
        acc.setRoles(roles);*/

        return acc;
    }

    @Override
    public Account updateAccount(Account acc) {
        return emgr.merge(acc);
    }
}
