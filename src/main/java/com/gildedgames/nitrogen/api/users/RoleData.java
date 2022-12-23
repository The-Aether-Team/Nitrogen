package com.gildedgames.nitrogen.api.users;

import java.util.*;

public class RoleData {
    public static class Client {
        private static User CLIENT_USER;

        public static User getClientUser() {
            return CLIENT_USER;
        }

        public static void setClientUser(User clientUser) {
            CLIENT_USER = clientUser;
        }
    }

    public static class Server {
        private static final Map<UUID, User> STORED_USERS = new HashMap<>();

        public static void initializeForTesting() {
            List<UUID> uuids = new ArrayList<>();
            uuids.add(UUID.fromString("380df991-f603-344c-a090-369bad2a924a"));
            for (UUID uuid : uuids) {
                User user = new User();
                user.addRole(new Ranked(Ranked.Rank.GILDED_GAMES));
                STORED_USERS.put(uuid, user);
            }
        }

        public static Map<UUID, User> getStoredUsers() {
            return STORED_USERS;
        }
    }
}
