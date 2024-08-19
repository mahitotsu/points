package com.mahitotsu.points.persistence.utils;

import java.util.UUID;

public class UUIDUtils {

    public static UUID next(final UUID uuid) {
        return new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() + 1);
    }

    public static UUID previous(final UUID uuid) {
        return new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() - 1);
    }

    private UUIDUtils() {
    }
}
