package com.smartcoupon.service.smartcard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.smartcoupon.service.account.User;
import com.smartcoupon.service.account.UserRepository;
import com.smartcoupon.service.usercard.UserCard;
import com.smartcoupon.service.usercard.UserCardRepository;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SmartCardService {

    private static final String STATUS = "status";
    private static final String SUCCESSFUL = "successful";

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmartCardRepository smartCardRepository;

    @Value("${cpn-baseuri}")
    private String cpnUri;

    @Autowired
    private RestTemplate restTemplate;

    public Map createSmartCard(HttpServletRequest request, JsonNode json) throws ValidationException, JsonProcessingException {

        String email = request.getHeader("email");
        ArrayNode cardIds = (ArrayNode)  json.get("cardIds");
        Iterator<JsonNode> itr = cardIds.elements();

        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()){
            throw new ValidationException("User not found");
        }
        long totalRedeemedPointsToBeConverted = 0;

        while (itr.hasNext()){
            long id = itr.next().asLong();
            UserCard newCard = userCardRepository.getById(id);
            redeemCard(newCard);
            totalRedeemedPointsToBeConverted+=newCard.getAvailablePoints();
            newCard.setAvailablePoints(0);
            userCardRepository.save(newCard);
        }

        // create new smart cards
        UserCard userCard = createNewCard(user.get().getUsername(),totalRedeemedPointsToBeConverted);
        userCard.setUser(user.get());
        smartCardRepository.save(userCard);

        HashMap<String,String> map = new HashMap();
        map.put(STATUS,SUCCESSFUL);
        return map;
    }

    private void redeemCard(UserCard card) throws ValidationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("cardNo",card.getCardNo());
        headers.set("pointsToRedeem", String.valueOf(card.getAvailablePoints()));
        HttpEntity entity = new HttpEntity<>(card,headers);
        ResponseEntity<String> result;
        try{
            result = restTemplate.exchange(cpnUri+"/redemptions", HttpMethod.PUT, entity, String.class);
            if (result.getStatusCode() != HttpStatus.OK) {
                throw new ValidationException("Could not redeem the card");
            }
        }catch (Exception e){
            throw new ValidationException("Could not redeem the card");
        }
    }
    private UserCard createNewCard(String cardName,long availablePoints) throws ValidationException, JsonProcessingException {

        HashMap map = new HashMap<>();
        map.put("cardName",cardName);
        map.put("availablePoints",availablePoints);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(map,headers);
        ResponseEntity<String> result = restTemplate.exchange(cpnUri, HttpMethod.POST, entity, String.class);
        if (result.getStatusCode() != HttpStatus.CREATED) {
            throw new ValidationException("Could not create the card");
        }
        return getSmartCardObjFromMapper(result);
    }

    private UserCard getSmartCardObjFromMapper( ResponseEntity result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode obj = mapper.readTree(result.getBody().toString());
        UserCard newSmartCard = new UserCard();
        newSmartCard.setCardNo(obj.get("cardNo").textValue());
        newSmartCard.setCvv(obj.get("cvv").asLong());
        newSmartCard.setExpMonth(obj.get("expMonth").asLong());
        newSmartCard.setExpYear(obj.get("expYear").asLong());
        newSmartCard.setCardName(obj.get("cardName").textValue());
        newSmartCard.setAvailablePoints(obj.get("availablePoints").asLong());
        newSmartCard.setSmart(true);
        return newSmartCard;
    }

    public List<UserCard> getSmartCards(String email) throws ValidationException {

        for (UserCard card:smartCardRepository.findByUserEmailAndIsSmart(email,true)) {
                card.setAvailablePoints(validateCard(card));
                smartCardRepository.save(card);
        }
        return smartCardRepository.findByUserEmailAndIsSmart(email,true);
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
}