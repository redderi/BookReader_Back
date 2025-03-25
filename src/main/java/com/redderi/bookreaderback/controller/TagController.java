package com.redderi.bookreaderback.controller;

import com.redderi.bookreaderback.dto.TagDTO;
import com.redderi.bookreaderback.model.Book;
import com.redderi.bookreaderback.model.Tag;
import com.redderi.bookreaderback.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        TagDTO tagDTO = convertToDTO(createdTag);
        return ResponseEntity.ok(tagDTO);
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagDTO> tagDTOs = tags.stream()
                                    .map(this::convertToDTO)
                                    .collect(Collectors.toList());
        return ResponseEntity.ok(tagDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        return tagService.getTagById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @RequestBody Tag tagDetails) {
        Tag updatedTag = tagService.updateTag(id, tagDetails);
        TagDTO tagDTO = convertToDTO(updatedTag);
        return ResponseEntity.ok(tagDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok("The tag was successfully deleted");
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());

        Set<String> bookTitles = tag.getBooks().stream()
                                    .map(Book::getTitle)
                                    .collect(Collectors.toSet());
        dto.setBookTitles(bookTitles);

        return dto;
    }
}