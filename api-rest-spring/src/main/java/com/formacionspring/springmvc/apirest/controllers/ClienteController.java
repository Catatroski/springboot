package com.formacionspring.springmvc.apirest.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.formacionspring.springmvc.apirest.entity.Cliente;
import com.formacionspring.springmvc.apirest.service.ClienteService;

@RestController
@RequestMapping("/api")
public class ClienteController {
	
	@Autowired
	private ClienteService servicio;
	
	@GetMapping("/clientes")
	public List<Cliente> cliente() {
		return servicio.findAll();
		
	}
	
//	@GetMapping("/clientes/{id}")
//	public Cliente clienteShow(@PathVariable Long id) {
//		return servicio.findById(id);
//	}
	
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> clienteShow(@PathVariable Long id){
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			cliente = servicio.findById(id);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error al realizar consulta a la base de datos :(");
			response.put("Error", e.getMessage().concat("_").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if (cliente == null) {
			response.put("Mensaje, ","El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
		
	}
	
//	@PostMapping("/clientes")
//	@ResponseStatus(HttpStatus.CREATED)
//	public Cliente saveCliente(@RequestBody Cliente cliente) {
//		return servicio.save(cliente);
//	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> saveCliente(@RequestBody Cliente cliente) {
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteNew = servicio.save(cliente);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error al realizar insert a la base de datos :(");
			response.put("Error", e.getMessage().concat("_").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("Mensaje","El cliente ha sido creado correctamente :D");
		response.put("Cliente", clienteNew);
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	
	
	
//	@PutMapping("/clientes/{id}")
//	@ResponseStatus(HttpStatus.CREATED)
//	public Cliente updateCliente(@RequestBody Cliente cliente, @PathVariable  Long id) {
//		Cliente clienteUpdate= servicio.findById(id);
//		
//		clienteUpdate.setNombre(cliente.getNombre());
//		clienteUpdate.setApellido(cliente.getApellido());
//		clienteUpdate.setEmail(cliente.getEmail());
//		clienteUpdate.setTelefono(cliente.getTelefono());
//		clienteUpdate.setCreatedAt(cliente.getCreatedAt());
//		
//		return servicio.save(clienteUpdate);
//	}
//
//	@DeleteMapping("/clientes/{id}")
//	@ResponseStatus(HttpStatus.OK)
//	public Cliente deleteClienteMostrado(@PathVariable  Long id) {
//		Cliente clienteUpdate= servicio.findById(id);
//		servicio.delete(id);
//		return clienteUpdate;
//	}
	
	
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> updateCliente(@RequestBody Cliente cliente, @PathVariable Long id) {
		
		Cliente clienteActual = servicio.findById(id);
		Cliente clienteUpdated = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setCreatedAt(cliente.getCreatedAt());
			
			servicio.save(clienteActual);
			
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error al actualizar en la base de datos :(");
			response.put("Error", e.getMessage().concat("_").concat(e.getMostSpecificCause().getMessage())); 
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("Mensaje","El cliente ha sido actualizado :D");
		response.put("cliente", clienteActual);
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	
	
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteClienteMostrado(@PathVariable  Long id) {
		Cliente clienteBorrado= servicio.findById(id);
		Map<String, Object> response = new HashMap<>();

		
		try {	
			
			if (clienteBorrado == null) {
				response.put("Mensaje, ","El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
			servicio.delete(id);
			
			String nombreFotoAnterior = clienteBorrado.getImagen();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
				
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				
				if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}	
			
			
			} 
		}
			catch (DataAccessException e) {
			response.put("Mensaje", "Error al borrar en la base de datos :(");
			response.put("Error", e.getMessage().concat("_").concat(e.getMostSpecificCause().getMessage())); 
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
		response.put("Mensaje","El cliente ha borrado actualizado :D");
		response.put("cliente", clienteBorrado);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> uploadImagen(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		
		Map<String, Object> response = new HashMap<>();
		Cliente cliente = servicio.findById(id);
		
		if(!archivo.isEmpty()) {
			//String nombreArchivo = archivo.getOriginalFilename();
			String nombreArchivo = UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" "," ");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				response.put("Mensaje", "Error al insertar imagen en la base de datos :(");
				response.put("Error", e.getMessage().concat("_").concat(e.getCause().getMessage())); 
			}
				
			String nombreFotoAnterior = cliente.getImagen();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
				
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				
				if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}	
			}
			
			cliente.setImagen(nombreArchivo);
			
			servicio.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Has subido correctamenete la imagen :D " + nombreArchivo);
		} else {
			response.put("archivo", "No se pudo encontrar");
		}
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
}
