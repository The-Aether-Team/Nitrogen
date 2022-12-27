package com.gildedgames.nitrogen.api.users;

import com.google.common.collect.ImmutableMap;

import java.util.*;

public class UserData {
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
            User user1 = new User();
            user1.addRole(new Ranked(Ranked.Rank.GILDED_GAMES));
            STORED_USERS.put(UUID.fromString("380df991-f603-344c-a090-369bad2a924a"), user1);

            User user2 = new User();
            Donor donor = new Donor("blue_moa", "natural_moa_skins", "angel_moa_skins_1");
            user2.addRole(donor);
            STORED_USERS.put(UUID.fromString("58a5d694-a8a6-4605-ab33-d6904107ad5f"), user2);
        }

        public static Map<UUID, User> getStoredUsers() {
            return ImmutableMap.copyOf(STORED_USERS);
        }
    }
}
