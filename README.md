# HCPlugins-JoinMessage

Plugin Paper de messages de connexion et de déconnexion de HeavenCube.

**Licence :** code source consultable et contributions bienvenues, mais usage
réservé aux serveurs HeavenCube. Toute réutilisation ou distribution exige une
autorisation écrite préalable. Voir [LICENSE](LICENSE).

## Compilation locale

Cloner `HCPlugins-Core` à côté de ce dépôt, puis lancer `./gradlew build`.
Le build compile l'API de Core depuis son code source ; HCCore et PlaceholderAPI
restent requis sur le serveur.

La CI utilise `HCPlugins-actions@main` et produit un JAR
`HCJoinMessage-AAAA.MM.JJ-bN.jar` pour chaque release de `main`.
