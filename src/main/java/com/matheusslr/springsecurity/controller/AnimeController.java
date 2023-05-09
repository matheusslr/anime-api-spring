package com.matheusslr.springsecurity.controller;

import com.matheusslr.springsecurity.domain.Anime;
import com.matheusslr.springsecurity.dto.AnimeDTO;
import com.matheusslr.springsecurity.service.AnimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animes")
public class AnimeController {
    private AnimeService animeService;

    public AnimeController(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Anime>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "1") Integer size
            ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(animeService.findAll(pageable));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(
            @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(animeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody AnimeDTO anime){
        return new ResponseEntity<>(animeService.save(anime), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "id") Long id){
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Anime> update(@RequestBody AnimeDTO anime){
        return new ResponseEntity<>(animeService.update(anime), HttpStatus.NO_CONTENT);
    }
}
