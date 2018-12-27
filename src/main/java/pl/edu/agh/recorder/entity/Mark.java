package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MARKS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Mark {

    @Id
    @NotNull
    @SequenceGenerator(name = "MARKS_SEQ", sequenceName = "MARKS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MARKS_SEQ")
    private Long id;

    @Column(name = "NAME")
    @Size(max = 64, message = "Name need to be under 64 characters")
    private String name;

    @NotNull
    @Column(name = "MARK_TIME")
    private Long markTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "RECORDING_ID")
    private Recording recording;
}