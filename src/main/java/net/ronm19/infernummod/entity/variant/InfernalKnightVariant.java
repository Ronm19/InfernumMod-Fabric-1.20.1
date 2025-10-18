package net.ronm19.infernummod.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum InfernalKnightVariant {
    DEFAULT(0),
    ELITE(1);

    private static final InfernalKnightVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(InfernalKnightVariant::getId)).toArray(InfernalKnightVariant[]::new);
    private final int id;

    InfernalKnightVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static InfernalKnightVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}