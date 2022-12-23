package com.gildedgames.nitrogen.api.users;

public class Booster implements Role {
    @Override
    public Type getType() {
        return Type.BOOSTER;
    }
}
