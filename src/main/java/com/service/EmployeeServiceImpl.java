package com.service;

import com.dao.EmployeeDao;
import com.dto.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeDao employeeDao;

	@Override
	public List<Employee> getAllEmployee() {

		return employeeDao.findAll();
	}

	@Override
	public Employee getEmployeeById(long id) {

		return employeeDao.findById(id).get();
	}

	@Override
	public Employee getEmployeeByEmail(String email) {

		return employeeDao.findByEmail(email);
	}

	@Override
	public void saveOrUpdateEmployee(Employee employee) {

		employeeDao.save(employee);
	}

	@Override
	public void deleteEmployee(long id) {

		employeeDao.deleteById(id);
	}

	@Override
	public List<Employee> search(String keyword) {

		return employeeDao.search(keyword);
	}

	@Override
	public List<Employee> getEmployeeByEmpId(String empId) {

		return employeeDao.findEmployeeByEmpId(empId);
	}

	@Override
	public Employee checkLogin(String email, String password) {

		return employeeDao.validateEmployee(email, password);
	}

	@Override
	public String getEmployeePassword(String email) {

		return employeeDao.findEmployeePassword(email);
	}

	@Override
	public void deleteAllEmployee() {

		employeeDao.deleteAll();
	}

}
