package spring.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyConfig{
	@Bean
	public UserDetailsService getUserDetailsService()
	{
		return new UserDetailServiceImpl();
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration c)throws Exception
	{
		return c.getAuthenticationManager();
	}
	
//	protected void configure(AuthenticationManagerBuilder auth)throws Exception
//	{
//		auth.authenticationProvider(authenticationProvider());
//	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception
	{
		
		
//		http.csrf().disable()
//		.authorizeRequests().requestMatchers("/admin/**").hasRole("ADMIN")
//		.requestMatchers("/user/**").hasRole("USER").
		
		
		
//		http.authorizeHttpRequests(
//				authorize->authorize.requestMatchers("/admin/**").hasRole("ADMIN")
//		.requestMatchers("/user/**").hasRole("USER")
//		.requestMatchers("/**").permitAll());
//		
//		http.authorizeHttpRequests().requestMatchers("/").permitAll();
//		http.authorizeHttpRequests().requestMatchers("/admin/**").hasAuthority("ADMIN");
//		http.authorizeHttpRequests().requestMatchers("/user/**").hasAuthority("USER");
//		
		http.csrf().disable()
        .authorizeHttpRequests().requestMatchers("/admin/**").authenticated()
        .requestMatchers("/user/**").authenticated()
        .and().authorizeHttpRequests().requestMatchers("/**").permitAll()
        .and().formLogin().loginPage("/signin")
        .loginProcessingUrl("/dologin")
        .defaultSuccessUrl("/user/index");
        
        
		
		http.authenticationProvider(authenticationProvider());
		return http.build();
		
	}
	

}
