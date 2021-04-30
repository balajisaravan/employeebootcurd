package com.dao;

import com.dto.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

	@Query(value = "SELECT e FROM Employee e WHERE e.firstName LIKE '%' || :keyword || '%'"
            + " OR e.email LIKE '%' || :keyword || '%'"
            + " OR e.empId LIKE '%' || :keyword || '%'"
            + " OR e.lastName LIKE '%' || :keyword || '%'")
    public List<Employee> search(@Param("keyword") String keyword);
	
	@Query(value = "select e from Employee e where e.empId like ?1")
	public List<Employee> findEmployeeByEmpId(String empId);
	
	public Employee findByEmail(String email);
	
	@Query(value = "select e from Employee e where e.email like ?1 and e.password like ?2")
	public Employee validateEmployee(String email, String password);
	
	@Query(value = "select e.password from Employee e where e.email like ?1")
	public String findEmployeePassword(String email); 
}
