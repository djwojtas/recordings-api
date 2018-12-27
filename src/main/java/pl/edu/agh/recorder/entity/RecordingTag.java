package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RECORDING_TAGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecordingTag {

    @Id
    @NotNull
    @SequenceGenerator(name = "RECORDING_TAGS_SEQ", sequenceName = "RECORDING_TAGS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECORDING_TAGS_SEQ")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "RECORDING_ID")
    private Recording recording;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TAG_ID")
    private Tag tag;
}