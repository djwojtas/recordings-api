package pl.edu.agh.recorder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.recorder.entity.MarkTag;
import pl.edu.agh.recorder.entity.RecordingTag;
import pl.edu.agh.recorder.entity.Tag;
import pl.edu.agh.recorder.rest.TagResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);

    @Mapping(source = "tag.tag", target = "tag")
    @Mapping(source = "tag.id", target = "id")
    TagResponse toReponse(RecordingTag tag);

    @Mapping(source = "tag.tag", target = "tag")
    @Mapping(source = "tag.id", target = "id")
    TagResponse toReponse(MarkTag tag);

    List<TagResponse> toResponseList(List<Tag> tags);

    List<TagResponse> toRecordingTagResponseList(List<RecordingTag> tags);

    List<TagResponse> toMarkTagResponseList(List<MarkTag> tags);
}
