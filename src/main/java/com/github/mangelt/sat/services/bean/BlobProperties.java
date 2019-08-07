package com.github.mangelt.sat.services.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlobProperties {

	protected String connectionString;
	protected String containerName;
	
}
