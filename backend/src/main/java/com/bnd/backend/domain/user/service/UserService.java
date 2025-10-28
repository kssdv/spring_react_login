package com.bnd.backend.domain.user.service;

import com.bnd.backend.domain.user.dto.UserRequestDTO;
import com.bnd.backend.domain.user.entity.SocialProviderType;
import com.bnd.backend.domain.user.entity.UserEntity;
import com.bnd.backend.domain.user.entity.UserRoleType;
import com.bnd.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	// 생성자 방식으로 주입
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}
	// 자체 로그인 회원 가입 (존재 여부)
	@Transactional(readOnly = true)
	public Boolean existUser(UserRequestDTO dto) {
		return userRepository.existsByUsername(dto.getUsername());
	}
	
	// 자체 로그인 회원 가입
	@Transactional
	public Long addUser(UserRequestDTO dto) {
		if(userRepository.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException();
		}
		
		UserEntity entity = UserEntity.builder()
	            .username(dto.getUsername())
	            .password(passwordEncoder.encode(dto.getPassword()))
	            .isLock(false)
	            .isSocial(false)
	            .roleType(UserRoleType.USER) // 우선 일반 유저로 가입
	            .nickname(dto.getNickname())
	            .email(dto.getEmail())
	            .build();

	    return userRepository.save(entity).getId();
	}
	
	// 자체 로그인
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }

    // 자체 로그인 회원 정보 수정
	@Transactional
	public Long updateUser(UserRequestDTO dto) throws AccessDeniedException {
		// 본인만 수정 가능 검증
	    String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
	    if (!sessionUsername.equals(dto.getUsername())) {
	        throw new AccessDeniedException("본인 계정만 수정 가능");
	    }
	    // 조회
	    UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(dto.getUsername(), false, false)
	            .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));
	    // 회원 정보 수정
	    entity.updateUser(dto);

	    return userRepository.save(entity).getId();
	}
    // 자체/소셜 로그인 회원 탈퇴

    // 소셜 로그인 (매 로그인시 : 신규 = 가입, 기존 = 업데이트)

    // 자체/소셜 유저 정보 조회
}
