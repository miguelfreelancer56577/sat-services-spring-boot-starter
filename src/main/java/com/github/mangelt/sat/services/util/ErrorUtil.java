package com.github.mangelt.sat.services.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

public class ErrorUtil {

	public String errorMessage(Throwable error){
		Optional<String> msg = Optional.of(error.getMessage());
		return msg.orElse(error.getCause().getMessage());
	}
	
}
