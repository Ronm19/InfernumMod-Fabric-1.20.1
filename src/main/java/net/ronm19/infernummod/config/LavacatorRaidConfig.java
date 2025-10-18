package net.ronm19.infernummod.config;

public final class LavacatorRaidConfig {
    // 7-wave default: 0,0,0,1,1,2,3
    public static int[] counts = {0, 0, 0, 1, 1, 2, 3};

    /** Returns how many Lavacators to spawn for a given wave index */
    public static int getCountForWave(int wave) {
        if (wave < 0) return 0;
        return wave < counts.length ? counts[wave] : counts[counts.length - 1];
    }

    /** Hot-reload placeholder — replace with a file or JSON loader later */
    public static void reload() {
        // read values from infernum_config.json if you wish
    }

    private LavacatorRaidConfig() {}
}
