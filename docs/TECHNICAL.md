# Guide technique — HCJoinMessage

## Point d’entrée

Ce dépôt appartient à la suite privée d’usage HeavenCube, publiée comme source consultable.
Il dépend obligatoirement de HCCore. Lire d’abord [AGENTS.md](../AGENTS.md), puis le Core voisin.
Le [guide commun](https://github.com/HeavenCube/HCPlugins-Core/blob/main/docs/ECOSYSTEM.md) décrit les règles Java/Paper, les contrats Core,
le packaging et la CI. Ce guide local décrit les particularités à préserver ; le code reste l’autorité.

## Dépendances et compilation

HCCore et PlaceholderAPI obligatoires ; PremiumVanish facultatif.

Cloner Core à côté ; JAR standard. Les APIs PlaceholderAPI et PremiumVanish restent compileOnly ; aucune bibliothèque serveur embarquée.

```powershell
.\gradlew.bat build
```

Utiliser JDK 25. Sous Linux : `./gradlew build`. Le JAR est dans `build/libs/` ; installer aussi
les plugins serveur requis. Un clone Core modifié affecte le classpath local ; noter son commit.
Après extension d’API commune, construire Core séparément et installer sa version compatible en premier.

## Commandes et permissions

`/hcplugins joinmessage reload` ; `preview <cosmétique> [--player <joueur>]` ; `preview default --player <joueur>`. Reload et preview : opérateurs. Permissions dynamiques `hcplugins.joinmessage.cosmetic.<id>`, défaut false. Ne pas réintroduire un argument joueur positionnel pour preview default.

La branche canonique est `/hcplugins joinmessage` ; elle est enregistrée chez Core, pas comme
une deuxième racine. Les résultats de reload, refus opérateur et autres textes partagés utilisent
`HCPluginsCore.translations(plugin)`. `{duration}` inclut déjà l’unité `ms`.

## Fichiers et données

`plugins/HCPlugins/HCJoinMessage.yml`. Configuration candidate chargée et validée avant remplacement ; échec conserve le précédent état. Pas de persistance joueur propre au plugin.

Valeurs par défaut dans `src/main/resources/`, jamais écrasées à chaque démarrage. Aucun import
automatique des anciens dossiers du monorepo. Messages communs dans `plugins/HCPlugins/translations.yml` ;
messages métier locaux. Modifier le fichier partagé se recharge avec `/hcplugins core reload`.

## Chemin d’exécution

ConfigurationLoader et le parser construisent les cosmétiques ordonnés. CosmeticResolver choisit le cosmétique selon les permissions. MessageRenderer résout PlaceholderAPI sur le texte, puis parse MiniMessage une seule fois. JoinMessageService diffuse aux joueurs/console ; JoinQuitListener et l’intégration PremiumVanish contrôlent les événements et la discrétion.

## Carte du code pour une modification

| Fichier | Responsabilité et points à préserver |
| --- | --- |
| [HCJoinMessage.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/HCJoinMessage.java) | Enable, intégrations, synchronisation des permissions et reload. |
| [ConfigurationLoader.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/config/ConfigurationLoader.java) | Lecture du fichier partagé et validation candidate. |
| [JoinMessageConfigurationParser.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/config/JoinMessageConfigurationParser.java) | Schéma, validation des cosmétiques et ordre. |
| [CosmeticResolver.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/message/CosmeticResolver.java) | Choix du cosmétique selon permissions. |
| [MessageRenderer.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/message/MessageRenderer.java) | PlaceholderAPI avant MiniMessage ; rendu ignoré en cas de panne. |
| [JoinMessageService.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/message/JoinMessageService.java) | Diffusion et gestion des messages. |
| [JoinQuitListener.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/listener/JoinQuitListener.java) | Connexion/déconnexion, messages vanilla et vanish. |
| [PremiumVanishListener.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/listener/PremiumVanishListener.java) | Intégration optionnelle et silence des joueurs masqués. |
| [PreviewCommand.java](../src/main/java/fr/noltox/hcplugins/customplayerjoinmessage/command/PreviewCommand.java) | Grammaire preview et contexte joueur explicite. |

`src/main/resources/paper-plugin.yml` définit identité, dépendances et permissions serveur.
`settings.gradle.kts` définit les builds composites ; `build.gradle.kts` le packaging.
`.github/workflows/build.yml` appelle les actions partagées à `@main` ; `.github/dependabot.yml`
maintient les dépendances. Une mise à jour de dépendance doit conserver ces contrats.

## Invariants et zones à risque

- Plugin serveur `HCJoinMessage`, HCCore obligatoire ; module `joinmessage`.
- HCCore et PlaceholderAPI obligatoires ; PremiumVanish facultatif.
- Préserver l’ordre des cosmétiques et la sélection actuelle : ne pas trier arbitrairement la configuration.
- Ne pas parser MiniMessage avant PlaceholderAPI, ni reparcourir un Component rendu pour substituer du texte.
- Préserver le silence vanish et les chemins de fallback ; une API facultative ne doit pas être chargée inconditionnellement.
- Préserver le reload atomique et le nettoyage des permissions dynamiques.

Avant une nouvelle logique transversale : chercher les usages dans Core et les autres plugins ;
ajouter au Core le contrat partagé réellement nécessaire avant le raccordement local. Ne pas recopier
un loader YAML, un registre de commandes ou un catalogue de traductions. Garder les événements et
états spécifiques ici. Thread serveur pour le jeu ; considérer callbacks et APIs tierces selon leur
thread réel, puis revalider le contexte avant mutation.

## Validation et limites

Pas de suite de tests Java dans ce dépôt actuellement ; `build` valide compilation/packaging, pas le rendu des messages. En jeu : join/quit, plusieurs permissions, placeholder valide/invalide, preview default avec cible, console, vanish actif/inactif et reload invalide.

Les placeholders et événements PremiumVanish doivent être vérifiés avec les plugins installés. Le traitement Preview n’est pas une diffusion publique.

Documentation seule : vérifier les liens locaux et le diff. Modification runtime : build, tests ciblés,
et scénario serveur correspondant. Rapporter seulement ce qui a été exécuté, avec résultat et limite.
Pour transfert entre IA, donner le commit Core testé et les fichiers/changements encore non committés.
