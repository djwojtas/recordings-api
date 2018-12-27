package pl.edu.agh.recorder.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "RECORDINGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Recording {

    @Id
    @NotNull
    @SequenceGenerator(name = "RECORDINGS_SEQ", sequenceName = "RECORDINGS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECORDINGS_SEQ")
    private Long id;

    @Column(name = "TITLE")
    @Size(max = 64, message = "Title need to be under 64 characters")
    private String title;

    @NotNull
    @Column(name = "UPLOAD_TIME")
    private Timestamp uploadTime;

    @NotNull
    @Column(name = "DURATION")
    private Long duration;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @Column(name = "FILE_NAME")
    @Size(min = 32, max = 32, message = "filename should equal 32")
    private String fileName;
}