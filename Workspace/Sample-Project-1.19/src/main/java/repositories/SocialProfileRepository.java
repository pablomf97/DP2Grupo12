package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.SocialProfile;

@Repository
public interface SocialProfileRepository extends JpaRepository<SocialProfile, Integer> {
		
//@Query("select s from SocialProfile s where s.actor.id= ?0 ")
	//Collection<SocialProfile> socialProfilesByUser(int userId);
@Query("select s from SocialProfile s where s.actor.userAccount.username='?1'")
Collection<SocialProfile> socialProfilesByUser(String username);





}
