package fr.noltox.hcplugins.customplayerjoinmessage.config;

import fr.noltox.hcplugins.customplayerjoinmessage.message.CosmeticResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinMessageConfigurationParserTest {

    @Test
    void preservesYamlPriorityAndFallsBackForMissingCosmeticMessages() throws Exception {
        var configuration = parse("""
                default-messages:
                  join: default join
                  leave: default leave
                custom-join-messages:
                  second:
                    join: second join
                  first:
                    leave: first leave
                """);
        var selected = CosmeticResolver.resolve(configuration, permission -> true);
        assertEquals("second", selected.cosmetic().orElseThrow().id());
        assertEquals("second join", selected.join());
        assertEquals("default leave", selected.leave());
        assertEquals(2, selected.matchingCosmetics());
        var defaults = CosmeticResolver.resolve(configuration, permission -> false);
        assertEquals("default join", defaults.join());
        assertEquals("default leave", defaults.leave());
        assertTrue(defaults.cosmetic().isEmpty());
    }

    @Test
    void rejectsInvalidMiniMessageAndWrongYamlTypes() {
        assertThrows(ConfigurationException.class, () -> parse("""
                default-messages:
                  join: '<red>unclosed'
                  leave: leave
                """));
        assertThrows(ConfigurationException.class, () -> parse("""
                default-messages:
                  join: 42
                  leave: leave
                """));
    }

    private static JoinMessageConfiguration parse(String content) throws Exception {
        var yaml = new YamlConfiguration();
        yaml.loadFromString(content);
        return JoinMessageConfigurationParser.parse(yaml);
    }
}
