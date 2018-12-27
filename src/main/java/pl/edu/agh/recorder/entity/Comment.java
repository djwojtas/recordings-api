package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "COMMENTS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {

    @Id
    @NotNull
    @SequenceGenerator(name = "COMMENTS_SEQ", sequenceName = "COMMENTS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENTS_SEQ")
    private Long id;

    @Column(name = "COMMENT")
    private String comment;

    @NotNull
    @Column(name = "COMMENT_TIME")
    private Timestamp commentTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "MARK_ID")
    private Mark mark;
}