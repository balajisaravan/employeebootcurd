package com.service;

import com.dto.Employee;
import java.util.List;

public interface EmployeeService {

	List<Employee> getAllEmployee();

	Employee getEmployeeById(long id);
	
	List<Employee> getEmployeeByEmpId(String empId);

	Employee getEmployeeByEmail(String email);

	void saveOrUpdateEmployee(Employee employee);

	void deleteEmployee(long id);

	Employee checkLogin(String email, String password);
	
	List<Employee> search(String keyword);
	
	String getEmployeePassword(String email);
	
	void deleteAllEmployee();

}
