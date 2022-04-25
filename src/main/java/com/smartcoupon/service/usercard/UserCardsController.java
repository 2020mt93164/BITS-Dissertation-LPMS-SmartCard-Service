package com.smartcoupon.service.usercard;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class UserCardsController {






    private static final String STATUS = "status";
    private static final String FAILED = "failed";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    UserCardService userCardService;

    @PostMapping(value = "/cards",consumes = "application/json", produces = "application/json")
    public ResponseEntity addCard(HttpServletRequest request, @RequestBody UserCard userCard){
        try {
            return new ResponseEntity(userCardService.addCard(request,userCard), HttpStatus.CREATED);
        }catch (Exception e){
            Map map = new HashMap();
            map.put(STATUS,FAILED);
            map.put(ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/cards", produces = "application/json")
    public ResponseEntity getCards(@RequestParam String email){
        return new ResponseEntity<>(userCardService.findByUserEmail(email),HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/cards", produces = "application/json")
    public ResponseEntity deleteCard(@RequestParam Long id){
        return new ResponseEntity<>(userCardService.deleteCard(id),HttpStatus.OK);
    }
}