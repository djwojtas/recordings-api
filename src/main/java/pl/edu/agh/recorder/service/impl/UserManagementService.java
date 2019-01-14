package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.exception.application.UserExistsException;
import pl.edu.agh.recorder.repository.ApplicationUserRepository;
import pl.edu.agh.recorder.service.IUserManagementService;

@Service
public class UserManagementService implements IUserManagementService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(ApplicationUser applicationUser) throws UserExistsException {
        if (applicationUserRepository.findByUsername(applicationUser.getUsername()).isPresent()) {
            throw new UserExistsException();
        }
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        applicationUserRepository.save(applicationUser);
    }
}
