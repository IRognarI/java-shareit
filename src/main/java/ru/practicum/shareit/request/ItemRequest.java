package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "requestor"})
@Setter(AccessLevel.NONE)
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
