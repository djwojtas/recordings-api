package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PERMISSIONS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Permission {

    @Id
    @NotNull
    @SequenceGenerator(name = "PERMISSIONS_SEQ", sequenceName = "PERMISSIONS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISSIONS_SEQ")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "RECORDING_ID")
    private Recording recording;
}