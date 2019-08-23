package com.github.mangelt.sat.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mangelt.sat.services.util.ErrorUtil;
import com.github.mangelt.sat.services.util.FileUtil;

@Configuration
public class BeanCommonConfiguration {

	@Bean("fileUtil")
	public FileUtil fileUtil(){
		return new FileUtil();
	}
	
	@Bean("errorUtil")
	public ErrorUtil errorUtil(){
		return new ErrorUtil();
	}
	
}
