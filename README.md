<p>
  <img src="public/logo.svg" alt="Viola ORM Logo" width="120"/>
</p>

# Viola ORM: A High-Performance Developer-Friendly Micro-ORM for Java

**Viola ORM** 🎻 is a lightweight yet powerful Java Object-Relational Mapping (ORM) framework that prioritizes **performance** and **type-safety**. By leveraging compile-time code generation, it eliminates the need for runtime reflection and provides an expressive, **Fluent API** for composing complex SQL queries in a completely object-oriented way.

## 🚀 Key Features

| Feature | Description | Advantage |
| :--- | :--- | :--- |
| **Compile-Time Code Generation** | Mapping code is generated during compilation via an Annotation Processor, eliminating the need for runtime reflection. | **Zero Reflection Overhead.** Leads to significantly faster startup and execution times compared to traditional runtime ORMs. |
| **Fluent API Query Builder** | A complete object-oriented interface for constructing SQL queries, including complex conditions, arithmetic, and SQL functions. | **Type Safety.** All queries are checked by the compiler, minimizing runtime SQL errors and ensuring correct syntax. |
| **Manual Entity Definition** | Allows developers to explicitly define tables and columns using core classes (`ColumnInfo.defineColumn()`) without relying on the annotation processor. | **Superior Flexibility.** A key advantage over ORMs like jOOQ, enabling dynamic query building or working with tables outside of defined entities. |
| **Complex Primary Key Handling** | Supports composite keys by annotating multiple fields in an entity with `@Id`. The Annotation Processor (or manual definition) handles the creation of the equivalent complex ID column class. | **Simplicity.** Handles complex mapping boilerplate automatically. |
| **Multi-Type Fetch Methods** | Flexible methods to retrieve query results as fully mapped **Entity Objects**, generic **Maps**, or raw **Object arrays**. | **Versatility.** Easily integrates with various data consumption requirements. |

## 🏗️ Architecture and Project Structure

Viola ORM is logically separated into two modules:

### 1. `orm-core` (The Logical Core)
This module contains all the main classes for defining tables and columns, managing connections, and building queries.

* **OOP Definitions:** Classes and interfaces to define tables and columns in a purely OOP manner (`ColumnInfo<E>`).
* **Fluent API:** The core logic for the query builder interface, enabling method chaining for SQL composition.
* **Infrastructure:** Contains the `ConnectionManager`, `SQL Dialects`, `Naming Strategies`, and utilities for managing transactions (`Transaction` and `TransactionWorker`).

### 2. `annotation-processor` (The Code Generator)
This is a standard Java Annotation Processor that runs during the compilation phase.

* **Entity Analysis:** It scans for classes marked with the `@Entity` annotation.
* **Code Generation:** For each entity (e.g., `User`), it automatically creates a concrete, type-safe `EntityModel` class (e.g., `UserTable`) containing pre-defined, typed column objects (`ColumnInfo` instances) for fields annotated with `@Column`, `@Id`, or `@Lob`.

## 📐 Software Engineering Concepts & Design Patterns

The ORM is designed for high maintainability, extensibility, and clarity by applying several well-known software engineering concepts and patterns:

### Design Patterns

| Pattern | Application in Viola ORM |
| :--- | :--- | 
| **Strategy Pattern** | Used for **SQL Dialects** (e.g., `MySQLDialect`, `H2Dialect`) to adapt to database-specific syntax (like `LIMIT`/`OFFSET`). Also used for pluggable **Naming Strategies** (e.g., converting Java CamelCase to SQL snake\_case). | 
| **Fluent Interface/API** | The `ColumnInfo` class hierarchy is the foundation of the query building, allowing for readable method chaining (e.g., `user.id.equal(5).and(user.name.like("Elias%"))`). |
| **Decorator Pattern** | Specialized column types (`NumericColumn<N>`, `DateColumn<D>`, `TextColumn`) extend the base `ColumnInfo`, adding domain-specific query methods (`add()`, `before()`, `like()`) to the base functionality. |
| **Command Pattern** | The `Query` functional interface encapsulates a piece of executable SQL logic (the "command"). This allows for the composition of complex conditional expressions and nested queries within the Fluent API. |
| **Template Method Pattern** | The `Transaction` class defines the high-level workflow for a database transaction (`start()`, setting `setAutoCommit(false)`, calling user logic, `commit()/rollback()`, `close()`), allowing specific logic to be plugged in via the `TransactionWorker` interface. |

### Core Concepts

* **Annotation Processing:** Fundamental to the project's performance, enabling **meta-programming** to generate essential boilerplate code at compile-time, thereby removing the need for runtime reflection.
* **Abstraction and Inheritance:** Extensive use of abstract classes (`ColumnInfo`) and interfaces (`Dialect`, `OrderColumn`) ensures a modular and highly extensible codebase.
* **Functional Programming:** Leverages functional interfaces like `Query` and custom utility streams (`LogicalStream`) to handle complex conditional logic and SQL string construction in a clean, declarative manner.
-----

## 💡 Usage with Gradle (Local Jars)

To integrate Viola ORM into your project, you must include both the `orm-core` and `annotation-processor` JARs from the local `/release` directory.

### Step 1: Setup Local Repository

1.  Create a folder named `lib` in your project's root directory.
2.  Download the latest JARs for `orm-core` and `annotation-processor` from the **`/release`** section of this repository and place them in the `lib` folder.

### Step 2: Configure `build.gradle`

Modify your Gradle build file to recognize the local directory and declare the dependencies correctly:

```gradle
repositories {
    mavenCentral()
    flatDir dirs: "${rootDir}/lib"
}

dependencies {
    // Core logic implementation
    implementation files('lib/orm-core.jar')

    // Annotation Processor (MUST use the annotationProcessor configuration)
    annotationProcessor files('lib/annotation-processor.jar')
}
```

> **Important:** Ensure you add the 2 jars (orm-core, annotation-processor) to the `lib` folder.

-----

## 👋 Contribute to Viola ORM

Viola ORM is an open-source project built by and for developers. Your help is invaluable\! We welcome contributions of any kind:

  * **Bug Fixes:** Help us squash any existing bugs.
  * **New Features:** Propose and implement new features, such as support for more complex SQL functions or additional database dialects.
  * **Documentation:** Improve existing documentation or add usage examples.

Please open an **Issue** to discuss your idea or submit a **Pull Request** with your changes.

-----

## 📞 Contact & Support

If you have any questions, need clarification, or want to discuss features, please reach out using the channels below:

|  |  |  |
| :---: | :--- | :--- |
| 🌐 | **LinkedIn** | [Your LinkedIn Profile Link] |
| 📧 | **Gmail** | [Your Gmail Address] |
| 📱 | **WhatsApp** | [Your WhatsApp Number/Link] |
| 🐙 | **GitHub** | [Your GitHub Profile Link] |

I look forward to connecting 👋🏻👨🏻‍💻\!