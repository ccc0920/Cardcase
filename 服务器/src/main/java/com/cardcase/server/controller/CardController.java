package com.cardcase.server.controller;

import com.cardcase.server.repository.CardId;
import com.cardcase.server.service.CardService;
import com.cardcase.server.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create-card")
    public String CardCreation(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String request) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        JsonNode rootNode = objectMapper.readTree(request);
        long cardId = cardService.insertCard(rootNode.get("userId").asInt(), rootNode.get("avatar").asText(), rootNode.get("background").asText(), rootNode.get("design").toString());
        if(cardId != 0) {
            for (JsonNode childNode : rootNode.get("elements")) {
                JsonNode position = childNode.get("position");
                cardService.insertElement(new CardId(cardId, childNode.get("type").asText()), childNode.get("content").asText(), position.get("x").asInt(), position.get("y").asInt(), childNode.get("style").toString());
            }
            return "{\"success\":true,\"cardId\":" + cardId + ",\"message\":\"Card created successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Card creation failed.\"}";
        }
    }

    @PutMapping("/cards/{cardId}")
    public String CardEdition(@RequestHeader(name = "Authorization") String authHeader, @PathVariable long cardId, @RequestBody String request) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        JsonNode rootNode = objectMapper.readTree(request);
        int userId = cardService.updateCard(cardId, rootNode.get("avatar").asText(), rootNode.get("background").asText(), rootNode.get("design").toString());
        if(userId == rootNode.get("userId").asInt()) {
            for (JsonNode childNode : rootNode.get("elements")) {
                JsonNode position = childNode.get("position");
                cardService.updateElement(new CardId(cardId, childNode.get("type").asText()), childNode.get("content").asText(), position.get("x").asInt(), position.get("y").asInt(), childNode.get("style").toString());
            }
            return "{\"success\":true,\"message\":\"Card updated successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Card update failed.\"}";
        }
    }

    @GetMapping("/cards/{cardId}")
    public String CardGet(@RequestHeader(name = "Authorization") String authHeader, @PathVariable long cardId) {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        String cardInfo = cardService.getCardById(cardId);
        if(cardInfo != null) {
            return "{\"success\":true,\"card\":" + cardInfo + "}";
        } else {
            return "{\"success\":false}";
        }
    }

    @GetMapping("/cards/user/{userId}")
    public String UserCardsGet(@RequestHeader(name = "Authorization") String authHeader, @PathVariable int userId) {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        String cards = cardService.getCardsByUserId(userId);
        return "{\"success\":true,\"cards\":" + cards + "}";
    }

    @DeleteMapping("/cards/{cardId}")
    public String CardDeletion(@RequestHeader(name = "Authorization") String authHeader, @PathVariable long cardId) {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        boolean success = cardService.deleteCardById(cardId);
        if(success) {
            return "{\"success\":true,\"message\":\"Card deleted successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Card deletion failed.\"}";
        }
    }
}
