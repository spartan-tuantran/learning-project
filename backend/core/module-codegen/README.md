## module-codegen

Utilizing Kotlin KSP (https://kotlinlang.org/docs/ksp-overview.html) to generate ExposedSQL
boilerplate CRUD operation methods.

### Idea

The idea is walk through the AST of the annotated entity class and generate the following extension methods:

- `fun insert(e: Entity): Entity`
- `fun byId(id: UUID): Entity?`
- `fun update(id: UUID, a: A?, b: B?....): Entity?`
- `fun convert(row: ResultRow): Entity`

For more details, see `RepositorySymbolProcessor.kt`

### Usage

To use the codegen processor, you will need to do the following:

- Include the KSP plugin in your `build.gradle` module `id("com.google.devtools.ksp")`
- Call to `ksp`, i.e `ksp(project(":core:module-codegen"))`

**module-postgresql**

```groovy
plugins {
  id("com.alext.plugin.flyway")
  id("com.google.devtools.ksp")
}

dependencies {
  implementation(project(":core:module-database"))
  implementation(project(":core:module-jackson"))
  implementation(project(":core:module-utility"))
  implementation(project(":core:module-codegen"))
  ksp(project(":core:module-codegen"))

  implementation(Libraries.Exposed.core)
  implementation(Libraries.Exposed.dao)
  implementation(Libraries.Exposed.jdbc)
  implementation(Libraries.Exposed.javaTime)

  implementation(Libraries.Micronaut.injectJava)

  implementation(Libraries.Utility.libPhoneNumber)

  implementation(Libraries.GeoSpatial.jts)

  implementation(Libraries.Jdbi.core)
  implementation(Libraries.Jackson.databind)
  implementation(Libraries.Jackson.core)

  implementation(Libraries.ApacheCommon.lang3)

  testImplementation(project(":core:module-io"))
  testImplementation(project(":core:module-crypto"))
  testImplementation(project(":core:module-rtree"))
}

flyway {
  sqlDir = file("./sql/")
}

kotlin {
  sourceSets.main {
    kotlin.srcDir("build/generated/ksp/main/kotlin")
  }
  sourceSets.test {
    kotlin.srcDir("build/generated/ksp/test/kotlin")
  }
}

```

### Example

Given the following entity:

```kt
package com.alext

import com.alext.codegen.Index
import com.alext.codegen.annotations.Repository
import com.alext.database.dao.Entity
import com.alext.database.exposed.extension.offsetDateTime
import java.time.OffsetDateTime
import java.util.UUID
import org.jetbrains.exposed.dao.id.UUIDTable

enum class Platform {
  ANDROID,
  IOS
}

object UserTable : UUIDTable("users") {
  val name = text("name")
  val age = integer("phone")
  val bio = text("bio").nullable()
  val platform = text("platform")
  val updatedAt = offsetDateTime("updated_at").nullable()
}

@Repository("com.alext.UserTable")
data class User(
  override val id: UUID,
  @Index(unique = true)
  val name: String = "hello",
  val age: Int = 100,
  val bio: String? = null,
  val platform: Platform,
  val updatedAt: OffsetDateTime? = null
) : Entity
```

The codegen processor will generate the following:

```kt
package com.alext

import com.alext.database.runtime.DatabaseContext
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.Int
import kotlin.String
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

public interface UserRepositoryExtension {
  public fun DatabaseContext.insert(entity: User): User {
    transaction(primary) {
      UserTable.insert {
        it[id] = entity.id
        it[name] = entity.name
        it[age] = entity.age
        it[bio] = entity.bio
        it[platform] = entity.platform.toString()
        it[updatedAt] = entity.updatedAt
      }
    }
    return entity
  }

  public fun convert(row: ResultRow): User = User(
    id = row[UserTable.id].value,
    name = row[UserTable.name],
    age = row[UserTable.age],
    bio = row[UserTable.bio],
    platform = com.alext.Platform.valueOf(row[UserTable.platform].uppercase()),
    updatedAt = row[UserTable.updatedAt]
  )

  public fun DatabaseContext.byId(id: UUID): User? = transaction(primary) {
    UserTable.select { UserTable.id eq id }
      .singleOrNull()
      ?.let { convert(it) }
  }

  public fun DatabaseContext.update(
    id: UUID,
    name: String? = null,
    age: Int? = null,
    bio: String? = null,
    platform: Platform? = null,
  ): User? = transaction(primary) {
    UserTable.update({ UserTable.id eq id }) { update ->
      name?.let { update[UserTable.name] = it }
      age?.let { update[UserTable.age] = it }
      bio?.let { update[UserTable.bio] = it }
      platform?.let { update[UserTable.platform] = it.toString() }
      update[updatedAt] = OffsetDateTime.now()
    }

    UserTable.select { UserTable.id eq id }
      .singleOrNull()
      ?.let { convert(it) }
  }
}
```

To use the generated code, you can simply extend the generated interface:

```kt
package com.alext

import com.alext.database.runtime.DatabaseContext
import java.util.UUID

interface UserRepository {
  fun byId(id: UUID): User?
  fun insert(entity: User): User
  fun update(
    id: UUID,
    name: String? = null,
    age: Int? = null,
    bio: String? = null,
    platform: Platform? = null,
  ): User?
}

class DefaultUserRepository(
  private val db: DatabaseContext
) : UserRepository, UserRepositoryExtension {

  override fun byId(id: UUID): User? {
    return db.byId(id)
  }

  override fun insert(entity: User): User {
    return db.insert(entity)
  }

  override fun update(id: UUID, name: String?, age: Int?, bio: String?, platform: Platform?): User? {
    return db.update(id, name, age, bio, platform)
  }
}
```
