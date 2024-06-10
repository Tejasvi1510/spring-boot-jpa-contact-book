package spring.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import spring.smart.entities.User;
import spring.smart.entities.contact;

public interface ContactRepository extends JpaRepository<contact, Integer> {
	
	@Query("from contact as c where c.user.id=:usreId")
	public List<contact> getAllContactById(@Param("usreId") int usreId);
	
	public List<contact> findByNameContainingAndUser(String name,User user);

}
