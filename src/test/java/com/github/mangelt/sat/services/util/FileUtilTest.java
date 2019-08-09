package com.github.mangelt.sat.services.util;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileUtil.class})
@DirtiesContext
@Slf4j
public class FileUtilTest {

	@Autowired
	FileUtil fileUtil;
	
	@Value("classpath:zip_files/")
	String zipFiles;
	
	public static Map<String, Object> cache = new HashMap<>();
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Before
	public void setup() {
		if(cache.isEmpty()) {
			List<File> files = new ArrayList<File>();
			cache.put("zipFiles", files);
			
			try
			{
				Files.list(Paths.get(resourceLoader.getResource(this.zipFiles).getFile().getPath()))
				.forEach(path -> files.add(path.toFile()));
			}
			catch (Exception e)
			{
				log.error(e.getMessage());
			}
		}
	}
	
	@Test
	public void getFilesFromZip(){
		List<File> files = (List<File>)cache.get("zipFiles");
		File compressFile = files.get(0);
		
		Flux<File> xmlFiles = fileUtil.streamFrom(compressFile).log();
		
		xmlFiles.subscribe(file->{
			try {
				String content = new String (Files.readAllBytes(file.toPath()),Charset.forName("UTF-8"));
				log.info("first subscriber: {}", content);
			} catch (Exception e) {
				log.error("There was an error trying to get the content file: {}", e.getClass());
			} 
		});
		
//		Flux.from(fileUtil.streamFrom(compressFile))
//		.flatMap(file->{
//			File xmlFile = (File)file;
//			try {
//				String content = new String (Files.readAllBytes(xmlFile.toPath()),Charset.forName("UTF-8"));
//				return Mono.just(content);
//			} catch (Exception e) {
//				return Mono.empty();
//			}
//		})
//		.subscribe(content->{
//			try {
//				log.info("invoice content: {}", content);
//			} catch (Exception e) {
//				log.error("There was an error trying to get the content file: {}", e.getClass());
//			} 
//		});
		
//		Flux<File> xmlFiles = Flux.from(fileUtil.streamFrom(compressFile));
//		
		Flux<String> contentFiles = xmlFiles.flatMap(file->{
			File xmlFile = (File)file;
			try {
				String content = new String (Files.readAllBytes(xmlFile.toPath()),Charset.forName("UTF-8"));
				return Mono.just(content);
			} catch (Exception e) {
				return Mono.empty();
			}
		}).log();
		
//		contentFiles.doOnError(error->{
//			log.error("There was an unexpected error of type: {}" , error.getClass());
//		});
		
		contentFiles.subscribe(content->{
			try {
				log.info("second subscriber: {}", content);
			} catch (Exception e) {
				log.error("There was an error trying to get the content file: {}", e.getClass());
			} 
		});
		
		log.info("finished main thread.");
		
		
	}
	
}
