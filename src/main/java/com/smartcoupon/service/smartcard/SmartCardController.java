package com.smartcoupon.service.smartcard;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class SmartCardController {

    private static final String STATUS = "status";
    private static final String FAILED = "failed";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    SmartCardService smartCardService;

    @PostMapping(value = "/smartcards",consumes = "application/json", produces = "application/json")
    public ResponseEntity createSmartCard(HttpServletRequest request, @RequestBody JsonNode json){
        try {
            return new ResponseEntity(smartCardService.createSmartCard(request,json),HttpStatus.CREATED);
        }catch (Exception e){
            Map map = new HashMap();
            map.put(STATUS,FAILED);
            map.put(ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/smartcards", produces = "application/json")
    public ResponseEntity getSmartCards(@RequestParam String email) throws ValidationException {
        return new ResponseEntity<>(smartCardService.getSmartCards(email),HttpStatus.OK);
    }
}