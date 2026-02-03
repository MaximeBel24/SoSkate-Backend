package com.soskate.api.services.common;

import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailValidationServiceImpl implements EmailValidationService{

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public void validateEmailUnique(String email) {

        email = normalizeEmail(email);

        if (customerRepository.existsByEmail(email) || instructorRepository.existsByEmail(email)) {
            log.warn("Email déjà existant: {}", email);
            throw new EmailAlreadyExistsException("Email déjà existant");
        }
    }

    @Override
    public void validateEmailUniqueForUpdate(String email, Long currentUserId, boolean isCustomer) {

        email = normalizeEmail(email);

        if (isCustomer) {
            if (customerRepository.existsByEmailAndIdNot(email, currentUserId) || instructorRepository.existsByEmail(email)) {
                log.warn("Email déjà existant: {}", email);
                throw new EmailAlreadyExistsException("Email déjà existant");
            }
        } else {
            if (customerRepository.existsByEmail(email) || instructorRepository.existsByEmailAndIdNot(email, currentUserId)) {
                log.warn("Email déjà existant: {}", email);
                throw new EmailAlreadyExistsException("Email déjà existant");
            }
        }
    }

    @Override
    public boolean emailExists(String email) {
        email = normalizeEmail(email);
         return customerRepository.existsByEmail(email) || instructorRepository.existsByEmail(email);
    }

    private String normalizeEmail(String email){
        return email == null ? "" : email.trim().toLowerCase();
    }
}
