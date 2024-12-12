package com.example.nhatrobackend.Sercurity;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationFacade {
    String getCurrentUserUuid();
    String getCurrentUserUuid(HttpServletRequest request);
}

