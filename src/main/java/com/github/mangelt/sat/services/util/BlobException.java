package com.github.mangelt.sat.services.util;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Custom exception used by the  BlobTodoHandler class to solve exception to the client
 * @author mangelt
 *
 */
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class BlobException extends RuntimeException
{
	protected HttpStatus status;
	protected String message;
}