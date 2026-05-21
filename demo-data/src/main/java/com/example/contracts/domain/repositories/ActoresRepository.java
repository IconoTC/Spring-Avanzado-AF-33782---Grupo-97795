package com.example.contracts.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.domain.entities.Actor;
import com.example.domain.entities.models.ActorDTO;
import com.example.domain.entities.models.ActorShort;

@RepositoryRestResource(path = "actores", itemResourceRel = "actor", collectionResourceRel = "actores")
public interface ActoresRepository extends JpaRepository<Actor, Integer>, JpaSpecificationExecutor<Actor> {	
	@RestResource(path = "por-nombre")
	List<Actor> findTop5ByFirstNameStartingWithOrderByLastNameDesc(String prefijo);
	List<Actor> findTop5ByFirstNameStartingWith(String prefijo, Sort orderBy);
	
	@Meta(comment = "con el DSL")
	List<Actor> findByActorIdGreaterThanEqual(int id);
	
	@Meta(comment = "con JPQL")
	@Query("from Actor a where a.actorId >= ?1")
	@EntityGraph(attributePaths = {"filmActors", "filmActors.film"})
	List<Actor> findConJPQL(int id);
	@Meta(comment = "con SQL")
	@NativeQuery("select * from actor a where a.actor_id >= :id")
	List<Actor> findConSQL(int id);

	List<ActorDTO> readByActorIdGreaterThanEqual(int id);
	List<ActorShort> queryByActorIdGreaterThanEqual(int id);

	@RestResource(exported = false)
	<T> List<T> findByActorIdGreaterThanEqual(int id, Class<T> tipo);

	@Override
	@RestResource(exported = false)
	void deleteById(Integer id);
}
