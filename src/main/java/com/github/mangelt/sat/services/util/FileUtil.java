package com.github.mangelt.sat.services.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.github.mangelt.sat.services.exception.FileUtilException;

@Component
@Slf4j
public class FileUtil {

	public Flux<File> streamFrom(File compressFile){
		try (ZipFile zipFile = new ZipFile(compressFile)) {
			
			Object[] compressedFiles = zipFile.stream()
			.flatMap(zipEntry->{
				try {
					File file = this.createTmpFile(zipEntry);
					FileUtils.copyInputStreamToFile(zipFile.getInputStream(zipEntry), file);
					return Stream.of(file);
				} catch (Exception e) {
					log.error("Something strange happened trying to get the File: {}" , zipEntry.getName());
					log.error("ERROR CLASS: {} ERROR CAUSE: {}" , e.getClass(), e.getCause());
					return Stream.empty();
				}
			})
			.toArray();
			
			return Flux.fromArray(compressedFiles)
			.flatMap(item->{
				File file = (File) item;
				return Mono.just(file);
			});
			
	    } catch (IOException e) {
	    	return Flux.error(new FileUtilException(e));
	    }
	} 
	
	public File createTmpFile(String extension) throws IOException
    {
		ZonedDateTime now = ZonedDateTime.now();
		Timestamp timestamp = Timestamp.from(now.toInstant());
		String fileName = "tmp_" + Date.from(timestamp.toInstant()).getTime();
        File sourceFile = File.createTempFile(fileName, ".".concat(extension));
        return sourceFile;
    }
	
	public File createTmpFile(ZipEntry zipEntry) throws IOException
    {
		String fileName = zipEntry.getName();
		String baseName = FilenameUtils.getBaseName(fileName);
		String fileExtension = "."+FilenameUtils.getExtension(fileName);
        File sourceFile = File.createTempFile(baseName, fileExtension);
        return sourceFile;
    }
	
}
