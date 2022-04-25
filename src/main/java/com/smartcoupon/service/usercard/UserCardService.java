package com.smartcoupon.service.usercard;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcoupon.service.account.User;
import com.smartcoupon.service.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserCardService {

    private static final String STATUS = "status";
    private static final String SUCCESSFUL = "successful";

    @Autowired
    private UserRepository userRepository;

    @Value("${cpn-baseuri}")
    private String cpnUri;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Map addCard(HttpServletRequest request, UserCard userCard) throws ValidationException {
        HashMap<String,String> map = new HashMap();
        String email = request.getHeader("email");

        try{
            long availablePoints = validateCard(userCard);
            Optional<User> user = userRepository.findByEmail(email);
            if (!user.isPresent()){
                throw new ValidationException("User not found");
            }
            userCard.setUser(user.get());
            userCard.setCardName(user.get().getUsername());
            userCard.setAvailablePoints(availablePoints);
            userCardRepository.save(userCard);

            map.put(STATUS,SUCCESSFUL);
            return map;
        }catch (Exception e){
            throw new ValidationException(e.getMessage());
        }
    }

    private long validateCard(UserCard card) throws ValidationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(card,headers);
        ResponseEntity<JsonNode> result = restTemplate.exchange(cpnUri+"/validations", HttpMethod.POST, entity, JsonNode.class);
        if (result.getStatusCode() != HttpStatus.OK) {
            throw new ValidationException("Card validation failed");
        }
        return result.getBody().get("availablePoints").asLong();
    }

    public List<UserCard> findByUserEmail(String email) {
        return userCardRepository.findByUserEmailAndIsSmart(email,false);
    }

    public Map<String, String> deleteCard(Long id) {
        userCardRepository.deleteById(id);
        HashMap<String,String> map = new HashMap();
        map.put(STATUS,SUCCESSFUL);
        return map;
    }
}
