package pl.edu.agh.recorder.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.service.IAuthenticationService;

@Service
public class AuthenticationService implements IAuthenticationService {

    @Override
    public ApplicationUser getAuthenticatedUser() {
        return (pl.edu.agh.recorder.entity.ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
