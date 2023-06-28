package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    User requestor;
    @CreationTimestamp
    LocalDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items = new ArrayList<>();

    public ItemRequest(String description, User requestor) {
        this.description = description;
        this.requestor = requestor;
    }
}