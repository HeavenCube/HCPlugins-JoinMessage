package fr.noltox.hcplugins.customplayerjoinmessage.message;

import fr.noltox.hcplugins.customplayerjoinmessage.config.JoinMessageConfiguration;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Selects the first configured cosmetic whose permission is present.
 */
public final class CosmeticResolver {

    private CosmeticResolver() {
    }

    public static ResolvedMessages resolve(JoinMessageConfiguration configuration, Predicate<String> hasPermission) {
        JoinMessageConfiguration.Cosmetic selected = null;
        int matches = 0;
        for (JoinMessageConfiguration.Cosmetic cosmetic : configuration.cosmetics()) {
            if (hasPermission.test(cosmetic.permission())) {
                matches++;
                if (selected == null) {
                    selected = cosmetic;
                }
            }
        }

        if (selected == null) {
            return new ResolvedMessages(
                    configuration.defaults().join(),
                    configuration.defaults().leave(),
                    Optional.empty(),
                    0
            );
        }
        return new ResolvedMessages(
                selected.join().orElse(configuration.defaults().join()),
                selected.leave().orElse(configuration.defaults().leave()),
                Optional.of(selected),
                matches
        );
    }

    public record ResolvedMessages(
            String join,
            String leave,
            Optional<JoinMessageConfiguration.Cosmetic> cosmetic,
            int matchingCosmetics
    ) {
        public boolean hasMultipleMatches() {
            return matchingCosmetics > 1;
        }
    }
}
