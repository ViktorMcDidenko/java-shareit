package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @CreationTimestamp
    private LocalDateTime created;

    public Comment(String text, Item item, User author) {
        this.text = text;
        this.item = item;
        this.author = author;
    }
}