<p align="center">
  <a href="https://www.algolia.com">
    <img alt="Algolia for Java" src="https://user-images.githubusercontent.com/22633119/59595532-4c6bd280-90f6-11e9-9d83-9afda3c85e96.png" >
  </a>

  <h4 align="center">The perfect starting point to integrate <a href="https://algolia.com" target="_blank">Algolia</a> within your Java project</h4>

  <p align="center">
    <a href="https://search.maven.org/artifact/com.algolia/algoliasearch/"><img src="https://img.shields.io/maven-central/v/com.algolia/algoliasearch.svg" alt="Maven"></img></a>
        <a href="https://travis-ci.org/algolia/algoliasearch-client-java-2"><img src="https://travis-ci.org/algolia/algoliasearch-client-java-2.svg?branch=master" alt="Travis"></img></a>
    <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="Licence"></img></a>
  </p>
</p>

<p align="center">
  <a href="https://www.algolia.com/doc/api-client/getting-started/install/java/" target="_blank">Documentation</a>  •
  <a href="https://discourse.algolia.com" target="_blank">Community Forum</a>  •
  <a href="http://stackoverflow.com/questions/tagged/algolia" target="_blank">Stack Overflow</a>  •
  <a href="https://github.com/algolia/algoliasearch-client-java-2/issues" target="_blank">Report a bug</a>  •
  <a href="https://www.algolia.com/support" target="_blank">Support</a>
</p>

## ✨ Features

* Support Java 8 and above
* Asynchronous and synchronous methods to interact with Algolia's API
* Thread-safe clients
* Typed requests and responses
* Injectable HTTP client

 **Migration note from v2.x to v3.x**
>
> In June 2019, we released v3 of our Java client. If you are using version 2.x of the client, read the [migration guide to version 3.x](https://www.algolia.com/doc/api-client/getting-started/upgrade-guides/java/).
Version 2.x will **no longer** be under active development.

## 💡 Getting Started

**WARNING:**
The JVM has an infinite cache on successful DNS resolution. As our hostnames points to multiple IPs, the load could be not evenly spread among our machines, and you might also target a dead machine.

You should change this TTL by setting the property `networkaddress.cache.ttl`. For example to set the cache to 60 seconds:
```java
java.security.Security.setProperty("networkaddress.cache.ttl", "60");
```

#### Install

With [Maven](https://maven.apache.org/), add the following dependency to your `pom.xml` file:

  ```xml
  <dependency>
        <groupId>com.algolia</groupId>
        <artifactId>algoliasearch-core</artifactId>
        <version>3.0.0</version>
  </dependency>
  ```

   ```xml
  <dependency>
        <groupId>com.algolia</groupId>
        <artifactId>algoliasearch-apache</artifactId>
        <version>3.0.0</version>
  </dependency>
  ```

#### POJO, JSON & Jackson2

The `SearchIndex` class is parametrized with a Java class. If you specify one, it lets you have type safe method results.
This parametrized Java class should follow the POJO convention:
  * A constructor without parameters
  * Getters & setters for every field you want to (de)serialize

All the serialization/deserialization is done with [Jackson2](https://github.com/FasterXML/jackson-core/wiki).

Example:

```java
public class Contact {

  private String name;
  private int age;

  public Contact() {}

  public String getName() {
    return name;
  }

  public Contact setName(String name) {
    this.name = name;
    return this;
  }

  public int getAge() {
    return age;
  }

  public Contact setAge(int age) {
    this.age = age;
    return this;
  }
}
```

In 30 seconds, this quick start tutorial will show you how to index and search objects.

### Initialize the client

To start, you need to initialize the client. To do this, you need your **Application ID** and **API Key**.
You can find both on [your Algolia account](https://www.algolia.com/api-keys).

```java
SearchClient client = DefaultSearchClient.create("YourApplicationID", "YourAdminAPIKey");
SearchIndex index = client.InitIndex("your_index_name");
```

### Push data

Without any prior configuration, you can start indexing contacts in the `contacts` index using the following code:

```java
class Contact {
  private String firstname;
  private String lastname;
  private int followers;
  private String company;
  private String objectID;
  // Getters/setters ommitted
}

SearchIndex<Contact> index = SearchIndex.initIndex("contacts", Contact.class);

index.saveObject(new Contact()
   .setObjectID("one")
   .setFirstname("Jimmie")
   .setLastname("Barninger")
   .setFollowers(93)
   .setCompany("California Paint"));

index.saveObject(new JSONObject()
   .setObjectID("one")
   .setFirstname("Warren")
   .setLastname("Speach")
   .setFollowers(42)
   .setCompany("Norwalk Crmc"));
```

### Configure

You can customize settings to fine tune the search behavior. For example, you can add a custom ranking by number of followers to further enhance the built-in relevance:

  ```java
  index.setSettings(new IndexSettings().setCustomRanking(Collections.singletonList("desc(followers)")));

  // Async
  index.setSettingsAsync(new IndexSettings().setCustomRanking(Collections.singletonList("desc(followers)")));
  ```

You can also configure the list of attributes you want to index by order of importance (most important first).

**Note:** Algolia is designed to suggest results as you type, which means you'll generally search by prefix.
In this case, the order of attributes is crucial to decide which hit is the best.

  ```java
  index.setSettings(new IndexSettings().setSearchableAttributes(
    Arrays.asList("lastname", "firstname", "company")
  );

  // Asynchronous
  index.setSettingsAsync(new IndexSettings().setSearchableAttributes(
      Arrays.asList("lastname", "firstname", "company")
  );
  ```

### Search

You can now search for contacts by `firstname`, `lastname`, `company`, etc. (even with typos):

  ```java
  // Sync version

  // Search for a first name
  index.search(new Query("jimmie"));
  // Search for a first name with typo
  index.search(new Query("jimie"));
  // Search for a company
  index.search(new Query("california paint"));
  // Search for a first name and a company
  index.search(new Query("jimmie paint"));
  ```

  ```java
  // Async version

  // Search for a first name
  index.searchAsync(new Query("jimmie"));
  // Search for a first name with typo
  index.searchAsync(new Query("jimie"));
  // Search for a company
  index.searchAsync(new Query("california paint"));
  // Search for a first name and a company
  index.searchAsync(new Query("jimmie paint"));
  ```

## 📝 Examples

You can find code samples in the [Algolia's API Clients playground](https://github.com/algolia/api-clients-playground/tree/master/java/src/main/java).

## 📄 License
Algolia Java API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
