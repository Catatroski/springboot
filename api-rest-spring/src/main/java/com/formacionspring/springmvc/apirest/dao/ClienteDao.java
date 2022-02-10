package com.formacionspring.springmvc.apirest.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.formacionspring.springmvc.apirest.entity.Cliente;

@Repository
public interface ClienteDao extends CrudRepository<Cliente, Long> {

}
