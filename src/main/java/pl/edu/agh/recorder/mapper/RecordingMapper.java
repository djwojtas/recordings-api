package pl.edu.agh.recorder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.rest.RecordingResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecordingMapper {
    @Mapping(source = "user.username", target = "username")
    RecordingResponse toResponse(Recording recording);

    List<RecordingResponse> toResponseList(List<Recording> recordings);
}
