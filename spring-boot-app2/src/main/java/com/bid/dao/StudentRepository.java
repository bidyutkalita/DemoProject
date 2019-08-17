package com.bid.dao;

import org.springframework.data.repository.CrudRepository;

import com.bid.model.Student;

public interface StudentRepository  extends CrudRepository<Student, Integer> {

}
