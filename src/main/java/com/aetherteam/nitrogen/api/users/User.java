package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class User {
    private final List<Role> roles = new ArrayList<>();

    protected User() { }

    public List<Role> getRoles() {
        return this.roles;
    }

    protected void addRoles(Role... roles) {
        for (Role role : roles) {
            if (!this.isRole(role.getType())) { // Makes sure this User doesn't already have a role.
                this.getRoles().add(role);
            }
        }
    }

    protected void addRole(Role role) {
        if (!this.isRole(role.getType())) { // Makes sure this User doesn't already have a role.
            this.getRoles().add(role);
        }
    }

    public boolean hasRank(Ranked.Rank rank) {
        return this.getRoles().stream().anyMatch((role) -> role.getType() == Role.Type.RANKED && ((Ranked) role).getRanks().contains(rank));
    }

    public boolean isPatron() {
        return this.isRole(Role.Type.PATRON);
    }

    public boolean isPledging() {
        return this.getRoles().stream().anyMatch((role) -> role.getType() == Role.Type.PATRON && ((Patron) role).isPledging());
    }

    public boolean hasPatreonTier(Patron.Tier tier) {
        return this.getRoles().stream().anyMatch((role) -> role.getType() == Role.Type.PATRON && ((Patron) role).getTier() == tier);
    }

    public Patron.Tier getPatreonTier() {
        Optional<Role> roleOptional = this.getRoles().stream().filter((role) -> role.getType() == Role.Type.PATRON).findFirst();
        return roleOptional.map(roleValue -> ((Patron) roleValue).getTier()).orElse(null);
    }

    public int getPatronTierLevel() {
        Patron.Tier tier = this.getPatreonTier();
        return tier != null ? tier.getLevel() : 0;
    }

    public boolean isDonor() {
        return this.isRole(Role.Type.DONOR);
    }

    public List<String> getLifetimeSkins() {
        List<String> skins = new ArrayList<>();
        this.getRoles().stream().filter((role) -> role instanceof Donor).forEach((role) -> skins.addAll(((Donor) role).getLifetimeSkins()));
        return skins;
    }

    public boolean hasLifetimeSkin(String skin) {
        return this.getRoles().stream().anyMatch((role) -> role instanceof Donor donor && donor.getLifetimeSkins().contains(skin));
    }

    public boolean isBooster() {
        return this.isRole(Role.Type.BOOSTER);
    }

    public boolean isRole(Role.Type type) {
        return this.getRoles().stream().anyMatch(role -> role.getType() == type);
    }

    public static User read(FriendlyByteBuf buffer) {
        User user = new User();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            String name = buffer.readUtf();
            Role.Type type = Role.Type.valueOf(name);
            switch(type) {
                case BOOSTER -> user.addRole(new Booster());
                case DONOR -> user.addRole(Donor.read(buffer));
                case PATRON -> user.addRole(Patron.read(buffer));
                case RANKED -> user.addRole(Ranked.read(buffer));
            }
        }
        return user;
    }

    public static void write(FriendlyByteBuf buffer, User user) {
        List<Role> roles = user.getRoles();
        buffer.writeInt(roles.size());
        for (Role role : roles) {
            role.write(buffer);
        }
    }
}
