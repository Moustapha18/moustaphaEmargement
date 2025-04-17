package org.example.projetdemargement.utils;

import lombok.Getter;
import lombok.Setter;
import org.example.projetdemargement.models.User;

public class SessionManager {
    @Setter
    @Getter
    private static User currentUser;

    public static void clearSession() {
        currentUser = null;
    }

    public static void logout() {
    }
}
