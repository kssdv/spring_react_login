package com.bnd.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bnd.backend.domain.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	Boolean existsByUsername(String username);
	
	Optional<UserEntity> findByUsernameAndIsSocial(String username, Boolean social);
	Optional<UserEntity> findByUsernameAndIsLock(String username, Boolean isLock);
	Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);
	
	@Transactional
	void deleteByUsername(String username);
}
