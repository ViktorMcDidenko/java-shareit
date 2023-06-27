package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
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