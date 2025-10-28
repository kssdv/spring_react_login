package com.bnd.backend.domain.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bnd.backend.domain.jwt.entity.RefreshEntity;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long>{
	Boolean existsByRefresh(String refresh);
	
	@Transactional // delete는 반드시 Transactional이 필요
	void deleteByRefresh(String refresh);
	
	@Transactional
	void deleteByUsername(String username);
}
