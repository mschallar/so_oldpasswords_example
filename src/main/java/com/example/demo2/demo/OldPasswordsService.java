package com.example.demo2.demo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldPasswordsService {

	@Autowired
	private OldPasswordsRepository oldPasswordsRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	public List<OldPasswords> findByOwnerId(Integer ownerId) {
		String hql = "select e from OldPasswords e where e.passwordOwnerId = :passwordOwnerId ORDER BY e.createdAt DESC";
		TypedQuery<OldPasswords> query = entityManager.createQuery(hql, OldPasswords.class).setMaxResults(3)
				.setParameter("passwordOwnerId", ownerId);
		List<OldPasswords> list = query.getResultList();
		return list;
	}

	public void save(OldPasswords oldPasswords) {
		oldPasswordsRepository.save(oldPasswords);
	}

}
