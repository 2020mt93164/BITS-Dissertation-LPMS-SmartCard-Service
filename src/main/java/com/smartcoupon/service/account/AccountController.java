package com.smartcoupon.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private static final String STATUS = "status";
    private static final String FAILED = "failed";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    AccountService accountService;

    @PostMapping(value = "/users",consumes = "application/json", produces = "application/json")
    public ResponseEntity registerUser(@RequestBody User user){
        try {
            return new ResponseEntity(accountService.registerUser(user),HttpStatus.CREATED);
        }catch (Exception e){
            Map map = new HashMap();
            map.put(STATUS,FAILED);
            map.put(ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(value = "/authentications", consumes = "application/json", produces = "application/json")
    public ResponseEntity validateLogin(@RequestBody User user){
        try {
            return new ResponseEntity(accountService.validateLogin(user),HttpStatus.ACCEPTED);
        }catch (Exception e){
            Map map = new HashMap();
            map.put(STATUS,FAILED);
            map.put(ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
    }
}