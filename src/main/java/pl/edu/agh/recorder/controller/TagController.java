package pl.edu.agh.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.recorder.exception.application.TagAlreadyExistsException;
import pl.edu.agh.recorder.mapper.TagMapper;
import pl.edu.agh.recorder.rest.TagPostRequest;
import pl.edu.agh.recorder.rest.TagResponse;
import pl.edu.agh.recorder.service.ITagService;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private ITagService tagService;

    @Autowired
    private TagMapper tagMapper;

    @PostMapping
    public TagResponse addTag(@RequestBody TagPostRequest tagPostRequest) throws TagAlreadyExistsException {
        return tagMapper.toResponse(tagService.addTag(tagPostRequest.getTag()));
    }
}
