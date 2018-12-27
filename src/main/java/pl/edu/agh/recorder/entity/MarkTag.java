package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MARK_TAGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MarkTag {

    @Id
    @NotNull
    @SequenceGenerator(name = "MARK_TAGS_SEQ", sequenceName = "MARK_TAGS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MARK_TAGS_SEQ")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "MARK_ID")
    private Mark mark;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TAG_ID")
    private Tag tag;
}