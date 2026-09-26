# HCPlugins-JoinMessage

Plugin Paper de messages de connexion et de déconnexion de HeavenCube.

## Compilation locale

Cloner `HCPlugins-Core` à côté de ce dépôt, puis lancer `./gradlew build`.
Le build compile l'API de Core depuis son code source ; HCCore et PlaceholderAPI
restent requis sur le serveur.

La CI utilise `HCPlugins-actions@main` et produit un JAR
`HCJoinMessage-AAAA.MM.JJ-bN.jar` pour chaque release de `main`.
