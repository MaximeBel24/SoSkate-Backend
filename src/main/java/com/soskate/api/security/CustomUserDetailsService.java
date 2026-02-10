package com.soskate.api.security;

import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.UserEntity;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Search in customers
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmail(username);

        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            return new CustomUserDetails(customer, buildAuthorities(customer));
        }


        // 2. Search in instructors
        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(username);

        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            return new CustomUserDetails(instructor, buildAuthorities(instructor));
        }

        // 3. No user found
        throw new UsernameNotFoundException("No user found with email " + username);
    }

    private List<GrantedAuthority> buildAuthorities(UserEntity userEntity) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userEntity instanceof CustomerEntity) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        if (userEntity instanceof InstructorEntity) {
            authorities.add(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));
        }
        if (Boolean.TRUE.equals(userEntity.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }
}
