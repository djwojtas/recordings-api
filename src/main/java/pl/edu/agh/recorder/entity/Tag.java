package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TAGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tag {

    @Id
    @NotNull
    @SequenceGenerator(name = "TAGS_SEQ", sequenceName = "TAGS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAGS_SEQ")
    private Long id;

    @NotNull
    @Column(name = "TAG")
    @Size(max = 32, message = "tag can be max 32 characters long")
    private String tag;
}