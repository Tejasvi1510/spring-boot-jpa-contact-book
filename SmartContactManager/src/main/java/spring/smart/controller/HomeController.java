package spring.smart.controller;

import javax.naming.spi.DirStateFactory.Result;
import javax.validation.Valid;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import spring.smart.dao.UserRepository;
import spring.smart.entities.User;
import spring.smart.entities.contact;
import spring.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title", "Home page");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title", "About page");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title", "Sign Up page");
		m.addAttribute("user", new User());
		return "signup";
	}
	
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerForm(@Valid @ModelAttribute("user") User user,
			BindingResult result,
			@RequestParam(value="agreement",defaultValue = "false") boolean agreement,
			Model m,
			HttpSession session)
	{
//		System.out.println(agreement);
//		System.out.println(user);
		try
		{
			 if(!agreement)
			 {
				 System.out.println("You have not agreed terms and conditions");
				 throw new Exception("You have not agreed terms and conditions");
				 
			 }
			 if(result.hasErrors())
			 {
				 m.addAttribute("user", user);
				 return "signup";
			 }
			 user.setEnabled(true);
			 user.setRole("ROLE_USER");
			 user.setImageUrl("default.png");
			 user.setPassword(passwordEncoder.encode(user.getPassword()));
			 System.out.println(user);
			 userRepository.save(user);
			 session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			 m.addAttribute("user", new User());
			 return "signup";
		}catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		
	}
	@GetMapping("/signin")
	public String customLoginPage()
	{
		return "login";
	}
	@RequestMapping(value="/doLogin",method=RequestMethod.POST)
	public String afterLogin()
	{
        return null;
	}
	
	
	
	
}
