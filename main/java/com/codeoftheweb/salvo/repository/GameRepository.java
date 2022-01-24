package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
//@RepositoryRestResource trae el import de abajo
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//3) creamos la interfaz del repo
@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    // recordar la estrucura de esta interfaz: JpaRepository<T, ID>
    //creamos la lista de Games
    List<Game> findByDate(LocalDateTime date);
   Optional<Game> findById(long id);

}
