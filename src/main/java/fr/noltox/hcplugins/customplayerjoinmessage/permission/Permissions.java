package fr.noltox.hcplugins.customplayerjoinmessage.permission;

/**
 * Permission nodes owned by the join-message module.
 */
public final class Permissions {

    public static final String COSMETIC_PREFIX = "hcplugins.joinmessage.cosmetic.";

    private Permissions() {
    }

    public static String cosmetic(String id) {
        return COSMETIC_PREFIX + id;
    }
}
