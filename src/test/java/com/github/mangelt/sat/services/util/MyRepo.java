package com.github.mangelt.sat.services.util;

import org.springframework.stereotype.Component;

import com.github.mangelt.sat.services.repository.CosmosRepository;

@Component
public class MyRepo extends CosmosRepository<String, String> {
	
}
