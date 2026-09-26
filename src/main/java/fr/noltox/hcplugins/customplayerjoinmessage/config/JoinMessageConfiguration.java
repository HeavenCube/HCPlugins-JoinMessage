package fr.noltox.hcplugins.customplayerjoinmessage.config;

import fr.noltox.hcplugins.customplayerjoinmessage.permission.Permissions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable configuration used by join and quit events.
 */
public record JoinMessageConfiguration(DefaultMessages defaults, List<Cosmetic> cosmetics) {

    public JoinMessageConfiguration {
        defaults = Objects.requireNonNull(defaults, "defaults");
        cosmetics = List.copyOf(cosmetics);
    }

    public record DefaultMessages(String join, String leave) {
        public DefaultMessages {
            join = Objects.requireNonNull(join, "join");
            leave = Objects.requireNonNull(leave, "leave");
        }
    }

    public record Cosmetic(String id, Optional<String> join, Optional<String> leave) {

        public Cosmetic {
            id = Objects.requireNonNull(id, "id");
            join = Objects.requireNonNull(join, "join");
            leave = Objects.requireNonNull(leave, "leave");
        }

        /**
         * Returns the cosmetic permission deterministically from its YAML identifier.
         */
        public String permission() {
            return Permissions.cosmetic(id);
        }
    }
}
