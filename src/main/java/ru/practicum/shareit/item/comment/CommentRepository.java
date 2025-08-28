package ru.practicum.shareit.item.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_Id(Long itemId, Sort sort);

    List<Comment> findByItemId(Long itemId);
}
