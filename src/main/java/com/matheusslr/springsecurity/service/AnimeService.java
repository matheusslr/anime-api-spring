package com.matheusslr.springsecurity.service;

import com.matheusslr.springsecurity.domain.Anime;
import com.matheusslr.springsecurity.domain.Producer;
import com.matheusslr.springsecurity.dto.AnimeDTO;
import com.matheusslr.springsecurity.repository.AnimeRepository;
import com.matheusslr.springsecurity.repository.ProducerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AnimeService {
    private AnimeRepository animeRepository;

    private ProducerRepository producerRepository;

    public AnimeService(AnimeRepository animeRepository, ProducerRepository producerRepository) {
        this.animeRepository = animeRepository;
        this.producerRepository = producerRepository;
    }

    public Page<Anime> findAll(Pageable pageable){
        return animeRepository.findAll(pageable);
    }

    public Anime findById(Long id){
        return animeRepository.findById(id).get();
    }

    public Anime save(AnimeDTO anime){
        Producer producer = producerRepository.findById(anime.getId_producer())
                .orElseThrow(() -> new RuntimeException("This producer does not exist"));

        return animeRepository.save(
                Anime.builder()
                        .name(anime.getName())
                        .description(anime.getDescription())
                        .producer(producer)
                        .build()
        );
    }

    public void delete(Long id){
        Anime animeToDelete = findById(id);
        animeRepository.delete(animeToDelete);
    }

    public Anime update(AnimeDTO anime){
        Anime animeToUpdate = findById(anime.getId());
        if(animeToUpdate != null) {
            var producer = producerRepository.findById(anime.getId_producer())
                    .orElseThrow(() -> new NullPointerException("Producer from anime not found"));

            animeToUpdate.setName(anime.getName());
            animeToUpdate.setProducer(producer);
            animeToUpdate.setDescription(anime.getDescription());
            return animeRepository.save(animeToUpdate);
        }
        else{
            throw new NullPointerException("Anime not found");
        }
    }
}
