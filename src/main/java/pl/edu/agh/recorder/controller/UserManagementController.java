package pl.edu.agh.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.exception.application.UserExistsException;
import pl.edu.agh.recorder.message.DefaultResponse;
import pl.edu.agh.recorder.service.IUserManagementService;

@RestController
@RequestMapping("/user-management")
public class UserManagementController {

    @Autowired
    private IUserManagementService userManagementService;

    @PostMapping("/user")
    public DefaultResponse add(@RequestBody ApplicationUser applicationUser) throws UserExistsException {
        userManagementService.createUser(applicationUser);
        return new DefaultResponse("success");
    }
}
