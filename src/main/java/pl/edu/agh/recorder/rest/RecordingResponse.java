package pl.edu.agh.recorder.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordingResponse {
    private Long id;
    private String title;
    private Timestamp uploadTime;
    private Long duration;
    private String username;
}
