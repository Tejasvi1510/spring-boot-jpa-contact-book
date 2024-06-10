package spring.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import spring.smart.dao.UserRepository;
import spring.smart.entities.User;

public class UserDetailServiceImpl implements UserDetailsService{
 
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.getUserByEmail(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("could not found user");
		}
		CustomUserDetail c=new CustomUserDetail(user);
		return c;
	}

}
