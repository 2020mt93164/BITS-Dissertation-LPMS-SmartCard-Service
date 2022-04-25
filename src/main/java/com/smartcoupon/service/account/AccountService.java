package com.smartcoupon.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    private static final String STATUS = "status";
    private static final String SUCCESSFUL = "successful";

    @Autowired
    private UserRepository userRepository;


    public Map registerUser(User user) throws ValidationException {
        try {
            Map map = new HashMap();
            map.put(STATUS,SUCCESSFUL);
            userRepository.save(user);
            return map;
        }catch (Exception e){
            throw new ValidationException("This email is already taken", String.valueOf(HttpStatus.BAD_REQUEST));
        }
    }

    public Map validateLogin(User user) throws ValidationException {
        Optional<User> fetechedUser = userRepository.findByEmail(user.getEmail());
        if (fetechedUser.isPresent()){
            if (!fetechedUser.get().getPassword().equals(user.getPassword())){
                throw new ValidationException("Either your email or password is wrong", String.valueOf(HttpStatus.NOT_FOUND));
            }
            Map map = new HashMap();
            map.put(STATUS,SUCCESSFUL);
            return map;
        }else {
            throw new ValidationException("Either your email or password is wrong", String.valueOf(HttpStatus.NOT_FOUND));
        }
    }
}
