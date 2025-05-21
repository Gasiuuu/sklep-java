//package com.sklep.sklep_backend.repository;
//
//import com.sklep.sklep_backend.entity.OrdersEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface OrdersRepo extends JpaRepository<OrdersEntity, Integer> {
//}


package com.sklep.sklep_backend.repository;

import com.sklep.sklep_backend.entity.OrdersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdersRepo {

    @PersistenceContext
    private EntityManager entityManager;

    /* ========== Odpowiedniki standardowych metod JpaRepository ========== */

    /** Pobranie zamówienia po ID. */
    public Optional<OrdersEntity> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(OrdersEntity.class, id));
    }

    /** Zapis nowego lub aktualizacja istniejącego zamówienia. */
    public OrdersEntity save(OrdersEntity order) {
        if (order.getId() == null) {
            entityManager.persist(order);     // nowe zamówienie
            return order;
        } else {
            return entityManager.merge(order); // aktualizacja
        }
    }

    /** Usunięcie zamówienia po ID. */
    public void deleteById(Integer id) {
        findById(id).ifPresent(entityManager::remove);
    }

    /** Pobranie wszystkich zamówień (odpowiednik findAll()). */
    public List<OrdersEntity> findAll() {
        String jpql = "SELECT o FROM OrdersEntity o";
        return entityManager.createQuery(jpql, OrdersEntity.class).getResultList();
    }

    /* ========== Dodatkowe metody możesz dodać w razie potrzeby ========== */
}

