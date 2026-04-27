package com.cardcase.server.controller;

import com.cardcase.server.repository.CardGroup;
import com.cardcase.server.service.GroupService;
import com.cardcase.server.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/groups")
    public String CreateGroup(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String request) {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        try {
            var node = objectMapper.readTree(request);
            int userId = node.get("userId").asInt();
            String groupName = node.get("groupName").asText();
            boolean success = groupService.createGroup(userId, groupName);
            if (success) {
                return "{"success":true,"message":"Group created successfully."}";
            } else {
                return "{"success":false,"message":"Group already exists."}";
            }
        } catch (Exception e) {
            return "{"success":false,"message":"Error creating group."}";
        }
    }

    @GetMapping("/groups/user/{userId}")
    public String GetGroups(@RequestHeader(name = "Authorization") String authHeader, @PathVariable int userId) {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        List<String> groups = groupService.getGroupNames(userId);
        try {
            String groupsJson = objectMapper.writeValueAsString(groups);
            return "{"success":true,"groups":" + groupsJson + "}";
        } catch (Exception e) {
            return "{"success":false,"message":"Error fetching groups."}";
        }
    }

    @PostMapping("/groups/cards")
    public String AddCardToGroup(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String request) {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        try {
            var node = objectMapper.readTree(request);
            int userId = node.get("userId").asInt();
            long cardId = node.get("cardId").asLong();
            String groupName = node.get("groupName").asText();
            boolean success = groupService.addCardToGroup(userId, cardId, groupName);
            if (success) {
                return "{"success":true,"message":"Card added to group."}";
            } else {
                return "{"success":false,"message":"Failed to add card."}";
            }
        } catch (Exception e) {
            return "{"success":false,"message":"Error adding card to group."}";
        }
    }

    @DeleteMapping("/groups/cards")
    public String RemoveCardFromGroup(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String request) {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        try {
            var node = objectMapper.readTree(request);
            int userId = node.get("userId").asInt();
            long cardId = node.get("cardId").asLong();
            boolean success = groupService.removeCardFromGroup(userId, cardId);
            if (success) {
                return "{"success":true,"message":"Card removed from group."}";
            } else {
                return "{"success":false,"message":"Failed to remove card."}";
            }
        } catch (Exception e) {
            return "{"success":false,"message":"Error removing card from group."}";
        }
    }
}
