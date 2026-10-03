# HCPlugins-JoinMessage

**CI :** sources/ressources/build seulement ; docs seules sans runner. Pour les exceptions,
voir [la politique CI et les marqueurs de skip](https://github.com/HeavenCube/HCPlugins-actions/blob/main/docs/CI_COSTS.md).

Plugin Paper de messages de connexion et de déconnexion de HeavenCube.

**Licence :** code source consultable et contributions bienvenues, mais usage
réservé aux serveurs HeavenCube. Toute réutilisation ou distribution exige une
autorisation écrite préalable. Voir [LICENSE](LICENSE).

## Compilation locale

Cloner `HCPlugins-Core` à côté de ce dépôt, puis lancer `./gradlew build`.
Le build compile l'API de Core depuis son code source ; HCCore et PlaceholderAPI
restent requis sur le serveur.

La configuration est `plugins/HCPlugins/HCJoinMessage.yml`. L'ancien fichier
`plugins/HCJoinMessage/config.yml` n'est pas repris automatiquement.

La CI utilise `HCPlugins-actions@main` et produit un JAR
`HCJoinMessage-AAAA.MM.JJ-bN.jar` pour chaque release de `main`.

## Liens importants

- [HCPlugins-Core](https://github.com/HeavenCube/HCPlugins-Core) : HCCore, services communs et guide de création des plugins.
- [HCPlugins-actions](https://github.com/HeavenCube/HCPlugins-actions) : workflows GitHub Actions partagés.
- [HCPack-CustomAssets](https://github.com/HeavenCube/HCPack-CustomAssets) : resource pack Nexo 26.3 : glow, police et effets de texte.
- [HCPlugins-AdvancementsRedirect](https://github.com/HeavenCube/HCPlugins-AdvancementsRedirect)
- [HCPlugins-Glowing](https://github.com/HeavenCube/HCPlugins-Glowing)
- [HCPlugins-HuskHomesGUI](https://github.com/HeavenCube/HCPlugins-HuskHomesGUI)
- [HCPlugins-ItemFrame](https://github.com/HeavenCube/HCPlugins-ItemFrame)
- [HCPlugins-JoinMessage](https://github.com/HeavenCube/HCPlugins-JoinMessage)
- [HCPlugins-PlaceholdersExtra](https://github.com/HeavenCube/HCPlugins-PlaceholdersExtra)

## Maintenance et documentation technique

HCCore est obligatoire. Pour toute modification technique, commencer par [AGENTS.md](AGENTS.md),
puis [le guide du plugin](docs/TECHNICAL.md) et le Core voisin.
Le [guide commun](https://github.com/HeavenCube/HCPlugins-Core/blob/main/docs/ECOSYSTEM.md) décrit les conventions de toute la suite.
`CLAUDE.md` et `GEMINI.md` renvoient aux mêmes instructions, sans copie des règles.
Le catalogue commun `plugins/HCPlugins/translations.yml` se recharge par `/hcplugins core reload`.
