package com.example.contracts.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import com.example.domain.entities.Actor;

public interface ActoresRepository extends JpaRepository<Actor, Integer>, JpaSpecificationExecutor<Actor> {	
	List<Actor> findTop5ByFirstNameStartingWithOrderByLastNameDesc(String prefijo);
	List<Actor> findTop5ByFirstNameStartingWith(String prefijo, Sort orderBy);
	
	@Meta(comment = "con el DSL")
	List<Actor> findByActorIdGreaterThanEqual(int id);
	
	@Meta(comment = "con JPQL")
	@Query("from Actor a where a.actorId >= ?1")
	List<Actor> findConJPQL(int id);
	@Meta(comment = "con SQL")
	@NativeQuery("select * from actor a where a.actor_id >= :id")
	List<Actor> findConSQL(int id);
	
}
