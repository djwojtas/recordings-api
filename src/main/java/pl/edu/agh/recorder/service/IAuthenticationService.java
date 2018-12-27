package pl.edu.agh.recorder.service;

import pl.edu.agh.recorder.entity.ApplicationUser;

public interface IAuthenticationService {
    ApplicationUser getAuthenticatedUser();
}
