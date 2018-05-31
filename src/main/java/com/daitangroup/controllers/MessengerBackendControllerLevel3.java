package com.daitangroup.controllers;

import com.daitangroup.ResponseContent;
import com.daitangroup.User;
import com.daitangroup.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
public class MessengerBackendControllerLevel3 {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value="lm_3/messenger/user", method=POST, produces="application/json")
    @ResponseBody
    public ResponseEntity createUser(@RequestParam(name="name", required=false, defaultValue="") String name,
                                     @RequestParam(name="password", required=false, defaultValue="") String password) {

        HttpStatus httpStatus = HttpStatus.CREATED;

        List<User> users = new ArrayList<User>();

        User user = new User();

        user.setName(name);
        user.setPassword(password);

        ResponseContent responseContent = new ResponseContent();

        try {
            User addedUser = userRepository.insert(user);
            responseContent.setService("create");
            users.add(addedUser);
            responseContent.setUsers(users);
        } catch (Exception e) {
            responseContent.setService(e.toString());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        addLinksToContentForUser(responseContent, user);

        return new ResponseEntity<>(responseContent, httpStatus);
    }

    @RequestMapping(value="lm_3/messenger/user", method=GET, produces="application/json")
    @ResponseBody
    public ResponseEntity readUser(@RequestParam(name="id", required=false) String id) {

        HttpStatus httpStatus = HttpStatus.OK;

        List<User> users = new ArrayList<User>();

        ResponseContent responseContent = new ResponseContent();

        try {
            if (id != null) {
                Optional<User> gotUser = userRepository.findById(id);
                if (!gotUser.isPresent()) {
                    httpStatus = HttpStatus.NOT_FOUND;
                } else {
                    users.add(gotUser.get());
                }
                responseContent.setUsers(users);
            } else {
                List gotUsers = userRepository.findAll();
                responseContent.setUsers(gotUsers);
            }
            responseContent.setService("read");
        } catch (Exception e) {
            responseContent.setService(e.toString());
            httpStatus = HttpStatus.NOT_FOUND;
        }

        addLinksToContentForUser(responseContent, null);

        return new ResponseEntity<>(responseContent, httpStatus);
    }


    @RequestMapping(value="lm_3/messenger/user", method=PUT, produces="application/json")
    @ResponseBody
    public ResponseEntity updateUser(@RequestParam(name="id") String id,
                                      @RequestParam(name="name", required=false, defaultValue="") String name,
                                      @RequestParam(name="password", required=false, defaultValue="") String password) {

        HttpStatus httpStatus = HttpStatus.OK;

        List<User> users = new ArrayList<User>();

        User user = new User();

        user.setName(name);
        user.setPassword(password);

        ResponseContent responseContent = new ResponseContent();

        try {
            user.setId(id);
            User updatedUser = userRepository.save(user);
            responseContent.setService("update");
            users.add(updatedUser);
            responseContent.setUsers(users);
        } catch (Exception e) {
            responseContent.setService(e.toString());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        addLinksToContentForUser(responseContent, user);

        return new ResponseEntity<>(responseContent, httpStatus);
    }

    @RequestMapping(value="lm_3/messenger/user", method=DELETE, produces="application/json")
    @ResponseBody
    public ResponseEntity deleteUser(@RequestParam(name="id") String id) {

        HttpStatus httpStatus = HttpStatus.OK;

        List<User> users = new ArrayList<User>();

        User user = new User();

        ResponseContent responseContent = new ResponseContent();

        try {
            user.setId(id);
            userRepository.delete(user);
            responseContent.setService("delete");
            users.add(user);
            responseContent.setUsers(users);
        } catch (Exception e) {
            responseContent.setService(e.toString());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        addLinksToContentForUser(responseContent, user);

        return new ResponseEntity<>(responseContent, httpStatus);
    }

    private void addLinksToContentForUser(ResponseContent responseContent, User user) {
        String id = null;
        String name;
        String password;

        if (user != null) {
            id = user.getId();
            name = user.getName();
            password = user.getPassword();
        } else {
            name = "username";
            password = "password";
        }

        responseContent.add(linkTo(methodOn(MessengerBackendControllerLevel3.class).createUser(name, password)).withSelfRel().withType("POST"));
        responseContent.add(linkTo(methodOn(MessengerBackendControllerLevel3.class).readUser(id)).withSelfRel().withType("GET"));
        responseContent.add(linkTo(methodOn(MessengerBackendControllerLevel3.class).updateUser(id, name, password)).withSelfRel().withType("PUT"));
        responseContent.add(linkTo(methodOn(MessengerBackendControllerLevel3.class).deleteUser(id)).withSelfRel().withType("DELETE"));
    }
}