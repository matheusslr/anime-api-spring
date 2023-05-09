package com.matheusslr.springsecurity.service;

import com.matheusslr.springsecurity.controller.AnimeController;
import com.matheusslr.springsecurity.domain.Anime;
import com.matheusslr.springsecurity.domain.Producer;
import com.matheusslr.springsecurity.dto.AnimeDTO;
import com.matheusslr.springsecurity.repository.AnimeRepository;
import com.matheusslr.springsecurity.repository.ProducerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AnimeService {
    private AnimeRepository animeRepository;
    private ProducerRepository producerRepository;
    private PagedResourcesAssembler<Anime> assembler;

    public AnimeService(AnimeRepository animeRepository, ProducerRepository producerRepository, PagedResourcesAssembler<Anime> assembler) {
        this.animeRepository = animeRepository;
        this.producerRepository = producerRepository;
        this.assembler = assembler;
    }

    public PagedModel<EntityModel<Anime>> findAll(Pageable pageable) {
        Page<Anime> animePage = animeRepository.findAll(pageable);
        animePage.stream().forEach(anime -> anime.add(linkTo(methodOn(AnimeController.class).findById(anime.getId())).withSelfRel()));
        return assembler.toModel(animePage);
    }

    public Anime findById(Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Anime not found"));
        anime.add(linkTo(methodOn(AnimeController.class).findById(id)).withSelfRel());
        return anime;
    }

    public Anime save(AnimeDTO anime) {
        Producer producer = producerRepository.findById(anime.getId_producer())
                .orElseThrow(() -> new RuntimeException("This producer does not exist"));

        Anime animeSaved = animeRepository.save(
                Anime.builder()
                        .name(anime.getName())
                        .description(anime.getDescription())
                        .producer(producer)
                        .build()
        );
        animeSaved.add(linkTo(methodOn(AnimeController.class).findById(anime.getId())).withSelfRel());
        return animeSaved;
    }

    public void delete(Long id) {
        Anime animeToDelete = findById(id);
        animeRepository.delete(animeToDelete);
    }

    public Anime update(AnimeDTO anime) {
        Anime animeToUpdate = findById(anime.getId());
        if (animeToUpdate != null) {
            var producer = producerRepository.findById(anime.getId_producer())
                    .orElseThrow(() -> new NullPointerException("Producer from anime not found"));

            animeToUpdate.setName(anime.getName());
            animeToUpdate.setProducer(producer);
            animeToUpdate.setDescription(anime.getDescription());
            Anime animeSaved = animeRepository.save(animeToUpdate);
            animeSaved.add(linkTo(methodOn(AnimeController.class).findById(animeSaved.getId())).withSelfRel());
            return animeSaved;
        } else {
            throw new NullPointerException("Anime not found");
        }
    }
}
