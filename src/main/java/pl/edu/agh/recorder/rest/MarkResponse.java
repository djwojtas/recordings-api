package pl.edu.agh.recorder.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkResponse {
    private Long id;
    private String name;
    private Long markTime;
    private String username;
    private Long recordingId;
    private String recordingTitle;
}
