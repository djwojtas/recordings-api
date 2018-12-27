package pl.edu.agh.recorder.service;

import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.exception.application.UserExistsException;

public interface IUserManagementService {
    void createUser(ApplicationUser applicationUser) throws UserExistsException;
}
