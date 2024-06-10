package spring.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import spring.smart.dao.ContactRepository;
import spring.smart.dao.UserRepository;
import spring.smart.entities.User;
import spring.smart.entities.contact;
import spring.smart.helper.Message;

@Controller
@EnableWebSecurity
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	@RequestMapping("/index")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	 public String dashboard(Model m)
    {
		
		 m.addAttribute("title", "user dashboard");
   	 return "normal/user_dashboard";
    }
	
	@ModelAttribute
	public void commonData(Model m,Principal principal)
	{
		 String username=principal.getName();
		 System.out.println(username);
		 User user=userRepository.getUserByEmail(username);
		 m.addAttribute("user", user);
	}
	
	
	//add open contact form
	@GetMapping("/add_contact_form")
	public String openContactForm(Model m)
	{
		m.addAttribute("title", "Add Contact Form");
		m.addAttribute("contact",new contact());
		return "normal/add_contact_form";
	}
	
	@PostMapping("/processAddContact")
	public String processContact(@ModelAttribute contact contact,
			@RequestParam("profileImage")MultipartFile file,Principal principal,
			HttpSession session)
	{
		try
		{
		String username=principal.getName();
		User user=userRepository.getUserByEmail(username);
		contact.setUser(user);
		 if(file.isEmpty())
		 {
			 System.out.println("fiel is empty");
			 contact.setImage("contact.png");
		 }else {
			 contact.setImage(file.getOriginalFilename());
			 File saveFile=new ClassPathResource("static/img").getFile();
			 Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			 System.out.println("img uploaded");
		 }
		
		
		user.getContacts().add(contact);
		
		userRepository.save(user);
		System.out.println("USer saved with contact");
		
		System.out.println("DAta : "+contact);
		
		session.setAttribute("message", new Message("Contact added successfully","alert-success"));
		
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			session.setAttribute("message", new Message("Something went wrong!!","alert-danger"));
			
		}
		return "normal/add_contact_form";
	}
    
	@GetMapping("/show_contact")
	public String viewAllContact(Model m,Principal principal)
	{
		m.addAttribute("title", "View Contacts");
		String username=principal.getName();
		User user=userRepository.getUserByEmail(username);
	    List<contact> contacts=contactRepository.getAllContactById(user.getId());
	    m.addAttribute("contacts", contacts);
		return "normal/show_contact";
	}
	//to show particular contact
	@RequestMapping("/contactSingle/{cId}")
	public String viewContact(@PathVariable("cId") Integer cId,Model m,Principal principal)
	{
		System.out.println(cId);
		Optional<contact> contactOptional=contactRepository.findById(cId);
		String username=principal.getName();
		User user=userRepository.getUserByEmail(username);
		
		contact contactName=contactOptional.get();
		m.addAttribute("title", "Contact Info");
		if(user.getId()==contactName.getUser().getId())
		{
		m.addAttribute("contact", contactName);
		}
		return "normal/contactSingle";
	}
	
	//delete contact
	@GetMapping("/deleteContact/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,HttpSession session)
	{
		Optional<contact> contactOptional= contactRepository.findById(cid);
		contact contact=contactOptional.get();
		contactRepository.delete(contact);
		session.setAttribute("message", new Message("Contact deleted successfully","alert-success"));
		
		return "redirect:/user/show_contact";
		
	}
	//update contact
	@PostMapping("/updateContact/{cid}")
	public String openUpdateForm(@PathVariable("cid") Integer cid,Model m)
	{
		contact contact=contactRepository.getById(cid);
		m.addAttribute("title", "Update Form");
		m.addAttribute("contact", contact);
		return "normal/updateContactForm";
	}
	//update Contact
	
	@PostMapping("/processUpdateContact")
	public String updateContact(@ModelAttribute contact contact,
			Principal principal,@RequestParam("profileImage")MultipartFile file,
			HttpSession session,Model model)
	{
		
		//old contact detail
		contact oldcontact=contactRepository.findById(contact.getcId()).get();
		System.out.println("name"+contact.getName());
		System.out.println("Id"+contact.getcId());
		try
		{
			if(!file.isEmpty())
			{
				//delete old file
				File deleteFile=new ClassPathResource("static/img").getFile();
				File f1=new File(deleteFile,oldcontact.getImage());
				f1.delete();
				
				//update new file
				 File saveFile=new ClassPathResource("static/img").getFile();
				 Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			     contact.setImage(file.getOriginalFilename());
			}
			else
			{
				contact.setImage(oldcontact.getImage());
			}
			
			String uname=principal.getName();
			User user=userRepository.getUserByEmail(uname);
			
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Updated successfully!!","alert-success"));
			
			
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return "redirect:/user/contactSingle/"+contact.getcId();
	}
	
	//your profile
	  @GetMapping("/profile")
	  public String displaayProfile(Model m,Principal principal)
	  {
		  String uname=principal.getName();
		  User user=userRepository.getUserByEmail(uname);
		  m.addAttribute("title", "Profile page");
		  m.addAttribute("user", user);
		  return "normal/profile";
	  }
	  
	  //change password form
	  @GetMapping("/changePassword")
	  public String changePasswordForm()
	  {
		  
		  return "normal/changePasswordForm";
	  }
	  @PostMapping("/changePasswordProcess")
	  public String changePassword(@RequestParam("oldPassword") String oldPassword,
			  @RequestParam("newPassword") String newPassword,Model m,HttpSession session,
			  Principal principal)
	   {
		  String uname=principal.getName();
		  User currentUser=userRepository.getUserByEmail(uname);
		  if(bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		  {
			  //change pwd
			  currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			  session.setAttribute("message", new Message("Password changed successfully!!","alert-success"));
			  userRepository.save(currentUser);
		  }
		  else
		  {
			  session.setAttribute("message", new Message("Please enter correct old password!!","alert-danger"));
				
			  return "redirect:/user/changePassword";
		       
		  }
		  return "redirect:/user/index";
	  }
	
}
