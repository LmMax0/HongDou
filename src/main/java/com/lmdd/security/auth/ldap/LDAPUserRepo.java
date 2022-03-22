package com.lmdd.security.auth.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LDAPUserRepo extends LdapRepository<LDAPUser> {
    Optional<LDAPUser> findByUsername(String username);

    Optional<LDAPUser> findByUsernameAndPassword(String username, String password);

    List<LDAPUser> findByUsernameLikeIgnoreCase(String username);
}

