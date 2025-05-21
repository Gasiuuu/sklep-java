//package com.sklep.sklep_backend.repository;
//
//
//import com.sklep.sklep_backend.entity.OurUsersEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface UsersRepo extends JpaRepository<OurUsersEntity, Integer> {
//
//    Optional<OurUsersEntity> findByEmail(String email);
//
//}


package com.sklep.sklep_backend.repository;

import com.sklep.sklep_backend.entity.OurUsersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsersRepo {

    @PersistenceContext
    private EntityManager entityManager;

    /* ---------- metody wyszukiwania ---------- */

    public Optional<OurUsersEntity> findByEmail(String email) {
        String jpql = "SELECT u FROM OurUsersEntity u WHERE u.email = :email";
        return entityManager.createQuery(jpql, OurUsersEntity.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<OurUsersEntity> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(OurUsersEntity.class, id));
    }

    /* ---------- pełny odpowiednik findAll() i count() ---------- */

    public List<OurUsersEntity> findAll() {
        String jpql = "SELECT u FROM OurUsersEntity u";
        return entityManager.createQuery(jpql, OurUsersEntity.class).getResultList();
    }

    public long count() {
        String jpql = "SELECT COUNT(u) FROM OurUsersEntity u";
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    /* ---------- zapisz / usuń ---------- */

    public OurUsersEntity save(OurUsersEntity user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        }
        return entityManager.merge(user);
    }

    public void deleteById(Integer id) {
        findById(id).ifPresent(entityManager::remove);
    }
}

