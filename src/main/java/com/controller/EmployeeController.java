package com.controller;

import com.dto.Employee;
import com.service.EmployeeService;
import com.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@Autowired
	Utils utils;

	@Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor ste=new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, ste);
	}

	@GetMapping(value = { "/", "/home", "/index", "default" })
	public ModelAndView showHomePage() {

		return new ModelAndView("index");
	}

	@GetMapping("/employees")
	public ModelAndView showAllEmployees() {
		ModelAndView mav = new ModelAndView("employees");
		List<Employee> employeeList = employeeService.getAllEmployee();
		mav.addObject("employeeList", employeeList);

		return mav;
	}

	@GetMapping("/sign-up")
	public ModelAndView showSignupForm() {
		ModelAndView mav = new ModelAndView("sign-up");
		mav.addObject("employee-sign-up", new Employee());

		return mav;
	}

	@GetMapping("/sign-in")
	public ModelAndView showSigninForm() {
		ModelAndView mav = new ModelAndView("sign-in");
		mav.addObject("employee-sign-in", new Employee());

		return mav;
	}

	@GetMapping("/welcome")
	public ModelAndView welcomeMessage() {

		return new ModelAndView("welcome");
	}

	@PostMapping("/saveEmployee")
	public ModelAndView createEmployee(@Valid @ModelAttribute("employee-sign-up") Employee employee, BindingResult br, HttpServletRequest request, HttpSession session) throws ParseException {
		if(request.getParameter("testData") == null || request.getParameter("testData").isEmpty() || request.getParameter("testData").equals("")) {
			Employee employeeExist = employeeService.getEmployeeByEmail(employee.getEmail());

			if (employeeExist != null) {
				br.rejectValue("email", "error.employee", "This email already exists!");
			}

			if (br.hasErrors()) {
				return new ModelAndView("sign-up");
			} else {
				String firstName = employee.getFirstName();
				String lastName = employee.getLastName();
				String nameChar = firstName.substring(0, 1) + lastName.substring(0, 1);

				String empId = nameChar.toUpperCase() + utils.generateRandomNumber(); 

				employee.setEmpId(empId);
				employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));

				Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

				employee.setCreateDate(currentTimeStamp);

				employeeService.saveOrUpdateEmployee(employee);
				System.out.println(empId + "=>This Employee has saved. Now redirecting...");

				session.setAttribute("ename", firstName+" "+lastName);

				return new ModelAndView("redirect:/employee/employees");
			}
		}
		else {
			if (br.hasErrors()) {

				return new ModelAndView("sign-up");
			} else {
				String firstName = employee.getFirstName();
				String lastName = employee.getLastName();

				String nameChar = firstName.substring(0, 1) + lastName.substring(0, 1);

				String empId = nameChar.toUpperCase() + utils.generateRandomNumber();
				employee.setEmpId(empId);
				employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
				employeeService.saveOrUpdateEmployee(employee);

				return new ModelAndView("redirect:/employee/employees");
			}
		}
	}

	@PostMapping("/validateEmployee")
	public ModelAndView validateEmployee(@Valid @ModelAttribute("employee-sign-in") Employee employee, BindingResult br, HttpSession session) {

		Employee employeeExist = employeeService.getEmployeeByEmail(employee.getEmail());

		if (employeeExist == null) {

			br.rejectValue("email", "error.employee", "This email does not exists!");
		} else {
			String employeeEncodedPassword = employeeService.getEmployeePassword(employee.getEmail());
			boolean checkPassStatus = bCryptPasswordEncoder.matches(employee.getPassword(), employeeEncodedPassword);

			if (checkPassStatus) {
				employee = employeeService.checkLogin(employee.getEmail(), employeeEncodedPassword);
				if (employee != null) {
					session.setAttribute("ename", employeeExist.getFirstName()+" "+employeeExist.getLastName());

					return new ModelAndView("welcome");
				} else {
					br.rejectValue("password", "error.employee", "Password mismatch.");
				}
			} else {
				System.out.println(this.getClass().getSimpleName() + ":Password doesn't match.");
				br.rejectValue("password", "error.employee", "Password doesn't match.");
			}
		}
		return new ModelAndView("sign-in");
	}

	@GetMapping("/updateEmployee/{id}")
	public ModelAndView editEmployee(@PathVariable("id") long id, Model model) {
		System.out.println(this.getClass().getSimpleName() + ":update employee..." + id);
		Employee employee = employeeService.getEmployeeById(id);
		ModelAndView mav = new ModelAndView("sign-up");
		mav.addObject("employee-sign-up", employee);
		model.addAttribute("testData", "testData");
		model.addAttribute("passwords", employee.getPassword());

		return mav;
	}

	@GetMapping("/deleteEmployee/{id}")
	public ModelAndView removeEmployee(@PathVariable("id") long id) {
		employeeService.deleteEmployee(id);

		return new ModelAndView("redirect:/employee/employees");
	}

	@PostMapping("/search")
	public ModelAndView searchCustomers(@RequestParam("empId") String empId) {
		System.out.println(this.getClass().getSimpleName() + ":Searching employee... " + empId);
		ModelAndView mav = new ModelAndView("employees");
		List<Employee> employeeList = employeeService.getEmployeeByEmpId(empId);
		mav.addObject("employeeList", employeeList);

		return mav;
	}

	@PostMapping("/search-keyword")
	public ModelAndView search(@RequestParam("keyword") String keyword) {
		List<Employee> result = employeeService.search(keyword);
		ModelAndView mav = new ModelAndView("search-result");
		mav.addObject("result", result);

		return mav;
	}

	@GetMapping("/deleteAll")
	public ModelAndView removeAllEmployee() {
		employeeService.deleteAllEmployee();

		return new ModelAndView("redirect:/employee/employees");
	}
	
}
