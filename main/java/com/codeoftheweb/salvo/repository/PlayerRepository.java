package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

//esta es la clase de java que maneja la base de datos de los player

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Optional<Player> findByUserName(@Param("userName") String userName);
   Player findByUserName(@Param("userName") String userName);
    // por que es asi esto??
    // si es findByUSERNAME, entre comillas poner lo mismo!!! sino no encuentra!! USERNAME :D
}
