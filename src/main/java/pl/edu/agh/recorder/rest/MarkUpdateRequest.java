package pl.edu.agh.recorder.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkUpdateRequest {
    private Long id;
    private String name;
    private Long markTime;
}
