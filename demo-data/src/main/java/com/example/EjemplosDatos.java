package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.contracts.domain.repositories.ActoresRepository;
import com.example.contracts.domain.repositories.CategoriasRepository;
import com.example.domain.entities.Actor;
import com.example.domain.entities.models.ActorDTO;
import com.example.domain.entities.models.ActorShort;

import tools.jackson.databind.json.JsonMapper;

@Component
public class EjemplosDatos {

	@Autowired
	ActoresRepository daoActors;
	
	public void consultas() {
//		daoActors.findAll().forEach(IO::println);
//		daoActors.findTop5ByFirstNameStartingWithOrderByLastNameDesc("P").forEach(IO::println);
//		daoActors.findTop5ByFirstNameStartingWith("P", Sort.by("FirstName")).forEach(IO::println);
//		daoActors.findByActorIdGreaterThanEqual(195).forEach(IO::println);
//		daoActors.findConJPQL(195).forEach(IO::println);
//		daoActors.findConSQL(195).forEach(IO::println);
//		daoActors.findAll((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("actorId"), 195)).forEach(IO::println);
//		daoActors.findByActorIdGreaterThanEqual(195).forEach(item -> IO.println(ActorDTO.from(item)));
//		daoActors.readByActorIdGreaterThanEqual(195).forEach(IO::println);
//		daoActors.queryByActorIdGreaterThanEqual(195).forEach(item -> IO.println("%d - %s".formatted(item.getId(), item.getNombre())));
		daoActors.findByActorIdGreaterThanEqual(195, ActorDTO.class).forEach(IO::println);
		daoActors.findByActorIdGreaterThanEqual(195, ActorShort.class).forEach(item -> IO.println("%d - %s".formatted(item.getId(), item.getNombre())));
	}
	
//	@Transactional
	public void carga() {
//		var actor = daoActors.findById(1).get();
//		System.out.println(actor);
//		actor.getFilmActors().forEach(item -> System.out.println("%d %s".formatted(item.getFilm().getFilmId(), item.getFilm().getTitle())));
		daoActors.findConJPQL(195).forEach(actor -> {
			System.out.println(actor);
			actor.getFilmActors().forEach(item -> System.out.println("%d %s".formatted(item.getFilm().getFilmId(), item.getFilm().getTitle())));
		});
		
	}
	
	@Transactional(/*rollbackFor = {Exception.class}*/)
	public void actores() throws Exception {
		System.out.println(">>> Create");
		var id = daoActors.save(new Actor("Pepito", "Grillo")).getActorId();
		daoActors.findAll().forEach(System.out::println);
		var item = daoActors.findById(id);
//		item.get();
		if (item.isEmpty()) {
			System.err.println("No encontrado");
			throw new Exception("No encontrado");
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

	public void valida() {
		var actor = new Actor(null, "12345678z");
//		if(actor.isInvalid()) {
//			System.err.println(actor.getErrorsMessage());
//		} else {
			daoActors.save(actor);
//		}
	}
	
	@Autowired
	CategoriasRepository daoCategorias;
	
	public void categorias() {
		var objectMapper = new JsonMapper();
		IO.println(objectMapper.writeValueAsString(daoCategorias.findAll()));
//		Person person = new ObjectMapper().readValue(jsonText, Person.class);
		var xmlMapper = new tools.jackson.dataformat.xml.XmlMapper();
		System.out.println(xmlMapper.writeValueAsString(daoCategorias.findAll()));
	}
}
