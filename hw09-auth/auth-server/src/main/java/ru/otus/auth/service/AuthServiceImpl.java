package ru.otus.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.auth.dto.TokenDto;
import ru.otus.auth.exception.NotAuthorizedException;
import ru.otus.auth.exception.NotFoundException;
import ru.otus.auth.service.jwt.JwtService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final Map<String, Set<String>> gamesAndUsers;
    private final JwtService jwtService;

    public AuthServiceImpl(JwtService jwtService) {
        this.gamesAndUsers = new ConcurrentHashMap<>();
        this.jwtService = jwtService;
    }

    @Override
    public String registerGame(List<String> users) {
        String gameId = UUID.randomUUID().toString();
        gamesAndUsers.put(gameId, Set.copyOf(users));
        return gameId;
    }

    @Override
    public String token(String gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NotAuthorizedException();
        }
        String userName = authentication.getName();
        Set<String> users = gamesAndUsers.get(gameId);
        if (users == null) {
            throw new NotFoundException("Game not found");
        }
        if (!users.contains(userName)) {
            throw new NotAuthorizedException();
        }
        return jwtService.token(userName, gameId);
    }

    @Override
    public TokenDto verify(String token, String gameId) {
        return jwtService.verify(token, gameId);
    }
}
