package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository  extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("select i from Item i where LOWER(i.name) like CONCAT('%', :text, '%') or LOWER(i.description) like CONCAT('%', :text, '%')")
    List<Item> getItemsBySearchQuery(@Param("text") String text);

}

