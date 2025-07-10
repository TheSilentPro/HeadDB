# HeadDB

> **Thousands of custom Minecraft heads—instantly accessible in‑game!**

---

## 📦 Table of Contents
1. [Features](#features)  
2. [Download & Installation](#download--installation)  
3. [Reporting Issues](#reporting-issues)  
4. [Using the API](#using-the-api)  
   - [Adding the Dependency](#adding-the-dependency)  
   - [Obtaining the API](#obtaining-the-api)  
   - [Waiting for Database Ready](#waiting-for-database-ready)  
   - [Examples](#examples)  
5. [API Reference](#api-reference)  
6. [Contributing](#contributing)  
7. [License](#license)

---

## 🔍 Features
- **Massive Head Library**  
  Browse thousands of player heads, from popular themes to custom community submissions.  
- **Lightweight API**  
  Decoupled `headdb-api` module keeps your plugin lean—no extra dependencies at runtime.  
- **Async Loading**  
  The database loads on a background thread.  
- **Flexible Querying**  
  Search by name, ID,category, or tags.  

---

## 🚀 Download & Installation

Choose your preferred source:

- **Releases (GitHub)**  
  https://github.com/TheSilentPro/HeadDB/releases  
- **Modrinth**  
  https://modrinth.com/plugin/hdb  
- **Hangar (PaperMC)**  
  https://hangar.papermc.io/Silent/HeadDB  
- **Spigot** *(Not recommended)*  
  https://www.spigotmc.org/resources/84967/  

---

## 🐞 Reporting Issues

Found a bug or have a feature request? Open an issue:

[HeadDB Issue Tracker](https://github.com/TheSilentPro/HeadDB/issues)

---

## 🤝 Using the API

### 1. Adding the Dependency

HeadDB publishes its API module via JitPack.

#### Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.TheSilentPro.HeadDB</groupId>
    <artifactId>HeadDB</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>
```

#### Gradle
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.TheSilentPro.HeadDB:HeadDB:VERSION"
}
```

---

### 2. Obtaining the API

HeadDB’s main `HeadAPI` is registered with Bukkit’s Services Manager:

```java
RegisteredServiceProvider<HeadAPI> rsp = Bukkit.getServicesManager().getRegistration(HeadAPI.class);
if (rsp == null) {
    // HeadDB is not installed or failed to register
    return;
}
HeadAPI api = rsp.getProvider();
```

---

### 3. Waiting for Database Ready

The head database loads asynchronously. Use these methods to wait on it:

```java
// Check if ready without blocking
boolean ready = api.isReady();

// Block until initial load completes
api.awaitReady();

// Asynchronously wait; returns CompletableFuture<List<Head>>
api.onReady().thenAccept(headList -> {
    System.out.println("Loaded " + headList.size() + " heads!");
});
```

---

### 4. Examples

```java
api.onReady().thenAccept(heads -> {
    System.out.println("Total heads: " + heads.size());
    api.findByCategory("Alphabet")
       .thenAccept(catHeads -> System.out.println("Alphabet category: " + catHeads.size()));
});

api.onReady().thenRun(() -> {
    api.findById(1).thenAccept(optHead -> {
        optHead.ifPresentOrElse(
            head -> System.out.println("Head #1: " + head.getName()),
            ()   -> System.out.println("No head with ID 1 found")
        );
    });

    api.findByTexture("cbc826aaafb8dbf67881e68944414f13985064a3f8f044d8edfb4443e76ba")
       .thenAccept(optHead -> {
           optHead.ifPresentOrElse(
               head -> System.out.println("Texture match: " + head.getName()),
               ()   -> System.out.println("No head for that texture")
           );
       });
});
```

---

## 📖 API Reference

All available methods live in the [HeadAPI class on GitHub](https://github.com/TheSilentPro/HeadDB/blob/master/headdb-api/src/main/java/com/github/thesilentpro/headdb/api/HeadAPI.java).

| Method                                    | Description                                                      |
|-------------------------------------------|------------------------------------------------------------------|
| `void awaitReady()`                       | Blocks until the database finishes initial load.                 |
| `boolean isReady()`                       | Returns true if the database is fully loaded (success/failure).  |
| `CompletableFuture<List<Head>> onReady()` | Async callback once initial load completes.                      |
| `searchByName(String name, boolean lenient)` | Fuzzy or exact name searches.                                 |
| `findById(int id)`                        | Lookup by internal head ID.                                      |
| `findByTexture(String texture)`           | Lookup by skin texture hash.                                     |
| `findByCategory(String category)`         | Get all heads in a given category.                               |
| `findByTags(String... tags)`              | Get heads matching any of the supplied tags.                     |
| `getHeads()`                              | Retrieve the full list of loaded heads (async).                  |
| `computeLocalHeads()`                     | Generate `ItemStack`s for all currently online players.          |
| `computeLocalHead(UUID uniqueId)`         | Generate an `ItemStack` for a specific player UUID.              |
| `List<String> findKnownCategories()`      | List all category names.                                         |
| `ExecutorService getExecutor()`           | Access the internal executor for advanced workflows.             |

---

## 🤗 Contributing

1. Fork the repository  
2. Create a feature branch (`git checkout -b feature/YourFeature`)  
3. Commit your changes (`git commit -m "Add awesome feature"`)  
4. Push to your branch (`git push origin feature/YourFeature`)  
5. Open a Pull Request  

Please follow the existing code style.

---

## 📜 License

Distributed under the [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html).
