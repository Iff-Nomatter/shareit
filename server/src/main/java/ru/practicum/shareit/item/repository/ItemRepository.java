package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long ownerId, Pageable pageable);

    List<Item> findItemsByOwnerId(long ownerId);

    @Query(
            "SELECT i FROM Item i " +
                    "WHERE (UPPER(i.name) " +
                    "LIKE UPPER(CONCAT('%', ?1, '%')) OR " +
                    "UPPER(i.description) " +
                    "LIKE UPPER(CONCAT('%', ?1, '%'))) " +
                    "AND i.available=true"
    )
    List<Item> searchByNameAndDescriptionAndAvailable(String search, Pageable pageable);
}
