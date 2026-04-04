package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Setter
@Table(name = "event_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id", nullable = false)
    @ToString.Exclude
    private User commenter;

    @Column(name = "text", nullable = false, length = 2000)
    private String text;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = true)
    private LocalDateTime updatedOn;

    @Column(name = "posted_on", nullable = true)
    private LocalDateTime postedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CommentStatus status;

    @Column(name = "reason_delete", nullable = true, length = 200)
    private String reasonDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_editor_id", nullable = true)
    @ToString.Exclude
    private User statusEditor;

    @Builder.Default
    @Column(name = "status_changed_by_admin", nullable = true)
    private Boolean statusChangedByAdmin = false;
}
