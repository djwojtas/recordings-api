package pl.edu.agh.recorder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.rest.MarkResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "recording.id", target = "recordingId")
    @Mapping(source = "recording.title", target = "recordingTitle")
    MarkResponse toResponse(Mark mark);

    List<MarkResponse> toResponseList(List<Mark> marks);
}
