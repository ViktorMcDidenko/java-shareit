package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    @Column(name = "request_id")
    private Long requestId;

    public Item(User owner, String name, String description) {
        this.owner = owner;
        this.name = name;
        this.description = description;
    }
}