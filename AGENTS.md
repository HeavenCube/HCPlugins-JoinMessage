# HCPlugins-JoinMessage

- Java 25, Paper 26.2, Gradle Kotlin DSL.
- `HCCore` owns `/hcplugins`; contribute `joinmessage` through the Core API.
- Compile Core's API from the sibling `HCPlugins-Core` source checkout, never from Maven.
- PlaceholderAPI is required; PremiumVanish is optional.
- Resolve PlaceholderAPI before parsing MiniMessage once.
- Preserve ordered cosmetic selection, dynamic permissions, atomic reload, and silent vanish behavior.
- Use `./gradlew build` before finalizing Java or Gradle changes.
- Do not commit, push, reset, rebase, stash, or change branches without explicit user authorization.
