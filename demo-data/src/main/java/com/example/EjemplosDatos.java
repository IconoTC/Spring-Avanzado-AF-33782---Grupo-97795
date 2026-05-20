package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.example.contracts.domain.repositories.ActoresRepository;
import com.example.domain.entities.Actor;

@Component
public class EjemplosDatos {

	@Autowired
	ActoresRepository daoActors;
	
	public void consultas() {
//		daoActors.findAll().forEach(IO::println);
		daoActors.findTop5ByFirstNameStartingWithOrderByLastNameDesc("P").forEach(IO::println);
		daoActors.findTop5ByFirstNameStartingWith("P", Sort.by("FirstName")).forEach(IO::println);
//		daoActors.findByActorIdGreaterThanEqual(195).forEach(IO::println);
//		daoActors.findConJPQL(195).forEach(IO::println);
		daoActors.findConSQL(195).forEach(IO::println);
		daoActors.findAll((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("actorId"), 195)).forEach(IO::println);
	}
	
	public void actores() {
		System.out.println(">>> Create");
		var id = daoActors.save(new Actor("Pepito", "Grillo")).getActorId();
		daoActors.findAll().forEach(System.out::println);
		var item = daoActors.findById(id);
		if (item.isEmpty()) {
			System.err.println("No encontrado");
		} else {
			var actor = item.get();
			System.out.println("Leido: " + actor);
			actor.setFirstName(actor.getFirstName().toUpperCase());
			daoActors.save(actor);
		}
		System.out.println(">>> Update");
		daoActors.findAll().forEach(System.out::println);
		daoActors.deleteById(id);
		System.out.println(">>> Delete");
		daoActors.findAll().forEach(System.out::println);

	}


}
