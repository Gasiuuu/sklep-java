//package com.sklep.sklep_backend.repository;
//
//import com.sklep.sklep_backend.entity.ProductsEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface ProductsRepo extends JpaRepository<ProductsEntity, Integer> {
//    Optional<ProductsEntity> findByName(String name);
//    Optional<ProductsEntity> findById(Integer id);
//}


package com.sklep.sklep_backend.repository;

import com.sklep.sklep_backend.entity.ProductsEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductsRepo {

    @PersistenceContext
    private EntityManager entityManager;

    /* ---------- metody wyszukiwania ---------- */

    public Optional<ProductsEntity> findByName(String name) {
        String jpql = "SELECT p FROM ProductsEntity p WHERE p.name = :name";
        return entityManager.createQuery(jpql, ProductsEntity.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<ProductsEntity> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(ProductsEntity.class, id));
    }

    /* ---------- pełny odpowiednik findAll() i count() ---------- */

    public List<ProductsEntity> findAll() {
        String jpql = "SELECT p FROM ProductsEntity p";
        return entityManager.createQuery(jpql, ProductsEntity.class).getResultList();
    }

    public long count() {
        String jpql = "SELECT COUNT(p) FROM ProductsEntity p";
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    /* ---------- zapisz / usuń ---------- */

    public ProductsEntity save(ProductsEntity product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        }
        return entityManager.merge(product);
    }

    public void deleteById(Integer id) {
        findById(id).ifPresent(entityManager::remove);
    }
}


