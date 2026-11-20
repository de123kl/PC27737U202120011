# PC2 — Guía rápida de backend (POST + GET)

**Consulta rápida de la guía completa de Catch-up.** `Item` representa el aggregate que indique el nuevo enunciado; `items` representa su bounded context o recurso según corresponda. Los nombres, reglas, versiones y tiempos definitivos los establece la PC2. El examen anterior prohibía generación de código con IA durante la evaluación (p. 3); consulta estos apuntes y reutiliza archivos durante la prueba solamente si sus reglas lo permiten.

## 0. Lee el enunciado (min 0–8)

- [ ] Proyecto, package raíz, Java, Spring Boot, puerto y esquema MySQL.
- [ ] Bounded contexts, aggregate, atributos, tipos, VO, enums, defaults, auditoría.
- [ ] Unicidad, campos obligatorios/opcionales y reglas entre campos.
- [ ] POST: ruta, nombres JSON de entrada y salida (no incluir ID autogenerado en request).
- [ ] GET: ruta, parámetros, filtro, ¿un objeto o lista?, qué hacer si no existe.
- [ ] Requisitos de i18n, Swagger, JavaDoc, naming y entrega.

## 1. Proyecto y MySQL (min 8–20, junto con Shared)

1. IntelliJ → New Project → Spring Boot → Maven, Java, Jar. Usa las versiones del enunciado y el SDK correcto.
2. Dependencias: Spring Web/MVC, Spring Data JPA, MySQL Driver, Validation; Lombok si lo usas. En `pom.xml`, consulta las dependencias adicionales del ZIP: `io.github.encryptorcode:pluralize` y `springdoc-openapi-starter-webmvc-ui` (versiones compatibles con tu Boot; no copiar todo el POM).
3. Workbench → conexión existente (3306) → crear el esquema solicitado (`CREATE DATABASE ...`). No crear las tablas a mano si las generará Hibernate.
4. `src/main/resources/application.properties`: configurar `spring.application.name`, `server.port`, `spring.datasource.url` (jdbc:mysql://127.0.0.1:3306/ESQUEMA), `spring.datasource.username`, `spring.datasource.password`, `spring.jpa.hibernate.ddl-auto=update`, `spring.jpa.open-in-view=false`, `spring.jpa.show-sql=true`.
5. Revisar `CatchUpPlatformApplication.java`: main bajo la raíz nueva. Eliminar exclusión temporal de DataSource si existe. Reload Maven y ejecutar.

**Referencia:** `pom.xml`, `application.properties`, `application-dev.properties`. No confundir 3306 (MySQL) con 8080/8181 (API). El ZIP usa Java 21 y Boot 3.5.7; la PC2 anterior usaba Java 26 y Boot 4.1.0. No trasladar versiones automáticamente.

## 2. Shared: reutilizar lo genérico si está permitido

**Rutas base de todo el índice:** `src/main/java/com/acme/catchup/platform/`. En el proyecto destino cambia `com.acme.catchup.platform` por el package raíz exigido. `news/` es el BC de referencia, no el nombre de tu nuevo BC.

| Archivo dentro de `shared/` | Acción |
|---|---|
| `application/result/Result.java` | Reutilizar si sigues Result |
| `domain/events/DomainEvent.java` | Reutilizar si usas la clase base/eventos |
| `domain/aggregates/AbstractDomainAggregateRoot.java` | Reutilizar con DomainEvent; no audita por sí sola |
| `infrastructure/persistence/jpa/strategy/SnakeCasePhysicalNamingStrategy.java` | Reutilizar, añadir pluralize y activar |
| `infrastructure/configuration/openapi/OpenApiConfig.java` | Adaptar título, descripción, autor |
| `interfaces/rest/GlobalExceptionHandler.java` | Adaptar errores e i18n |

- Naming: una vez creada la clase, configurar `spring.jpa.hibernate.naming.physical-strategy` con el nombre completo de la clase en TU package. Verificar tablas plurales y snake_case. En el ZIP no está activada.
- Recursos: adaptar `src/main/resources/messages.properties` (EN) y `messages_es.properties` (ES); cambiar claves `favorite.source...`. Configurar `spring.messages.basename=messages`, UTF-8 y `spring.messages.fallback-to-system-locale=false`.
- Revisar packages, imports, dependencias y arranque antes de continuar.

## 3. Domain (min 20–34)

Crea las clases de tu caso tomando como referencia estas rutas relativas a `news/`:

| Orden | Referencia Catch-up | Qué implementar |
|---|---|---|
| 1 | `domain/model/valueobjects/NewsApiKey.java`, `SourceId.java` | Records VO: campos tipados, constructor compacto, null, rangos, reglas y métodos de negocio |
| 2 | `domain/model/valueobjects/` | Enum del enunciado, si existe; constantes exactas |
| 3 | `domain/model/aggregates/FavoriteSource.java` | Aggregate Java puro, sin @Entity; ID Long, VO, enum, getters, auditoría si se pide |
| 4 | `domain/exceptions/DuplicateFavoriteSourceException.java` | Excepción específica si el enfoque la requiere |
| 5 | `domain/repositories/FavoriteSourceRepository.java` | Puerto de dominio, sin extender JpaRepository |

**Aggregate:** `create(...)` para instancias nuevas (id null, defaults, timestamps iniciales); `rehydrate(...)` para reconstruir con ID y fechas existentes. No usar create al leer. Si se necesita evento, consultar `domain/model/events/FavoriteSourceCreatedEvent.java`.

**Reglas:** datos intrínsecos → VO/aggregate; duplicados o consultas de otros registros → servicio + repositorio; validación de forma JSON → resource. UUID para identificadores externos si se pide, LocalDate para fechas, BigDecimal para dinero. Un VO de otro BC no implica crear una tabla o FK automáticamente.

**Puerto:** `save(Item) → Item`, `existsBy... → boolean`; agregar `findById`/`findAll`/`findAllBy...` según GET. Usa Optional para uno y List para muchos.

## 4. Infrastructure (min 34–49)

| Orden | Referencia dentro de `news/` | Qué adaptar |
|---|---|---|
| 1 | `infrastructure/persistence/jpa/entities/FavoriteSourceJpaEntity.java` | @Entity, @Table, ID autogenerado, columnas, constructor vacío y completo, getters |
| 2 | `infrastructure/persistence/jpa/FavoriteSourceRepository.java` | Interfaz Spring Data, extends JpaRepository de JpaEntity y métodos derivados |
| 3 | `infrastructure/persistence/jpa/mappers/FavoriteSourcePersistenceMapper.java` | `toEntity` (aplanar VO); `toDomain` (recrear VO y rehydrate) |
| 4 | `infrastructure/persistence/jpa/adapters/FavoriteSourceRepositoryAdapter.java` | @Repository, implements puerto, inyecta JPA repo y mapper |
| 5 | `infrastructure/config/TimeConfiguration.java` | Bean Clock UTC, si usas timestamps mediante Clock |

- Entity: IDENTITY, columnas obligatorias no nulas, clave única según regla, enum STRING, fechas de auditoría persistidas. Aplana VO de varios campos. No inventar límites de longitud o precisión.
- Repo JPA: método derivado usa el atributo **Java de JpaEntity**, no el nombre de columna. findById/findAll ya existen.
- Adapter: save transforma → guarda (saveAndFlush si necesitas detectar constraint) → transforma resultado. exists convierte VO a primitivo. GET Optional usa map; listas usan stream/map/toList.
- Los dos `FavoriteSourceRepository.java` están en paquetes diferentes. Puedes llamar `ItemJpaRepository` al de infraestructura si las convenciones lo permiten.
- Unicidad: precheck + constraint SQL. El adapter del ZIP no traduce por sí mismo el fallo de constraint duplicado. Distingue la violación de unicidad concreta de otros errores.
- Ejecutar y comprobar en Workbench que la tabla, esquema, columnas, PK y constraint son correctos.

## 5. POST: Application (min 49–62, junto con resources)

| Referencia dentro de `news/` | Qué hacer |
|---|---|
| `application/commands/CreateFavoriteSourceCommand.java` | Record con datos necesarios/VO, no ID generado |
| `application/commandservices/FavoriteSourceCommandService.java` | Interfaz handle(command) |
| `application/commandservices/FavoriteSourceCommandFailure.java` | Failure Duplicate, si usas Result |
| `application/internal/commandservices/FavoriteSourceCommandServiceImpl.java` | @Service, @Transactional, inyectar puerto de dominio y Clock si corresponde |

**Secuencia:** comprobar duplicado → crear aggregate → guardar → retornar el aggregate **guardado con ID**. No devolver el original sin ID.

Catch-up usa `Result<Aggregate, Failure>`: success/duplicate. Alternativa si está permitida: devolver aggregate y lanzar excepción específica para duplicado. No mezclar los enfoques sin necesidad. Ningún status HTTP en Application.

## 6. Interfaces REST para POST (min 62–72)

| Orden | Referencia dentro de `news/` | Qué hacer |
|---|---|---|
| 1 | `interfaces/rest/resources/CreateFavoriteSourceResource.java` | Request record: nombres JSON exactos, tipos, Jakarta Validation |
| 2 | `interfaces/rest/resources/FavoriteSourceResource.java` | Response record: ID + campos exigidos |
| 3 | `interfaces/rest/transform/CreateFavoriteSourceCommandMapper.java` | Resource → Command, construye VO |
| 4 | `interfaces/rest/transform/NewsValueObjectMapper.java` | Referencia opcional para conversiones repetidas de VO |
| 5 | `interfaces/rest/transform/FavoriteSourceResourceMapper.java` | Aggregate → Response resource (reutilizar en GET) |
| 6 | `interfaces/rest/transform/FavoriteSourceCommandHttpResponseMapper.java` | Result → 201 o 409 localizado |
| 7 | `interfaces/rest/FavoriteSourcesController.java` | @RestController, @RequestMapping, inyección de servicios, @PostMapping, @Valid @RequestBody |

**POST:** JSON → CreateResource → CommandMapper → CommandService → DomainRepository → Adapter → JPA → MySQL → aggregate con ID → Resource → 201.

Usa @NotBlank para String obligatorio; @NotNull para UUID, fecha, número, etc.; no exigir un dato cuya omisión activa un default. Respeta nombres JSON aunque sean distintos al dominio. Probar con Postman (Content-Type: application/json): válido 201, inválido 400, duplicado 409 (según contrato).

## 7. GET (min 72–84)

Primero identifica qué variante pide el enunciado; implementa solo esa.

| Variante | Controller | Retorno del puerto | Método JPA |
|---|---|---|---|
| Por ID | `@GetMapping("/{id}")`, @PathVariable | Optional<Item> | findById |
| Todos | @GetMapping | List<Item> | findAll |
| Filtro | @GetMapping, @RequestParam | List<Item> | findAllByCampo |
| Clave única | @GetMapping, @RequestParam | Optional<Item> | findByCampo |
| Filtros combinados | @RequestParam por filtro | List<Item> o Optional según caso | findAllByCampoAndOtro o findBy... |

1. Asegurar que puerto, repo JPA y adapter incluyen la operación de lectura. Si falta, agregarla en ese orden.
2. `application/queries/`: consultar `GetFavoriteSourceByIdQuery.java`, `GetAllFavoriteSourcesByNewsApiKeyQuery.java` o `GetFavoriteSourceByNewsApiKeyAndSourceIdQuery.java`. Crear solo la query requerida, con parámetros validados.
3. `application/queryservices/FavoriteSourceQueryService.java`: declarar handle(query) y retorno adecuado.
4. `application/internal/queryservices/FavoriteSourceQueryServiceImpl.java`: @Service, @Transactional(readOnly = true), delegar al puerto.
5. `interfaces/rest/transform/FavoriteSourceQueryHttpResponseMapper.java`: Optional presente → 200, ausente → 404; lista → 200 y [] si vacía, salvo contrato distinto.
6. Agregar GET a `interfaces/rest/FavoriteSourcesController.java` (el controller del caso nuevo). Reutilizar el response resource y su mapper; no devolver JpaEntity. Evitar mappings duplicados.

**GET:** URL → Query → QueryService → DomainRepository → Adapter → JPA → MySQL → aggregate(s) → Resource(s) → HTTP. No guardar ni cambiar auditoría durante lectura.

## 8. Errores, i18n y documentación (min 84–93)

- Resource usa claves de validación; VO lanza IllegalArgumentException con clave; GlobalExceptionHandler y HTTP mappers resuelven con MessageSource y locale (`Accept-Language`). Añadir claves EN/ES en ambos bundles. Probar en ambos idiomas.
- Revisar 400 por JSON mal formado, UUID/fechas/enum inválidos y parámetros GET; handler del ZIP solo cubre IllegalArgumentException y MethodArgumentNotValidException. No transformar todos los errores en 400.
- 201 POST exitoso; 200 GET; 400 entrada inválida; 404 recurso individual ausente; 409 duplicado (ajustar al contrato). Body de error con mensaje, por ejemplo ProblemDetail.
- Swagger UI: `/swagger-ui/index.html`, OpenAPI `/v3/api-docs` si se usan rutas predeterminadas. @Tag, @Operation, @ApiResponses, @Schema; textos en inglés.
- JavaDoc en inglés, propósito y @author, @param/@return donde corresponda.
- Auditoría: createdAt/updatedAt inicializados y persistidos; GET no cambia fechas. La clase base y @EnableJpaAuditing no completan por sí solos la auditoría del ZIP.

## 9. Pruebas y entrega (min 93–100; prueba cada bloque antes)

- [ ] App compila y arranca en puerto requerido.
- [ ] POST válido: 201, ID, JSON exacto; fila real en Workbench.
- [ ] POST repetido: 409, sin segunda fila; campos nulos/rangos/fechas inválidos: 400.
- [ ] GET con registros y sin resultados; filtro excluye registros no correspondientes.
- [ ] Mensajes en EN/ES, Swagger, auditoría y nombres correctos.
- [ ] README.md en raíz, en inglés: overview, stack, configuración, ejecución, endpoints, author.
- [ ] Buscar restos de `com.acme.catchup.platform`, `FavoriteSource`, `catch_up_os`, `favorite.source.` y puerto antiguo.
- [ ] ZIP con nombre exigido (anterior: `pc2<NRC>u<code>.zip`), con pom.xml, src, README y archivos de construcción necesarios; sin target/logs/secretos. Abrir ZIP y verificar antes de entregar.

## Si algo falla, busca primero aquí

| Síntoma | Revisar |
|---|---|
| No DataSource / Access denied / Unknown database | URL, credenciales, MySQL activo, esquema, perfil |
| Release not supported / Cannot resolve symbol | SDK de Maven/IDE, java.version, dependencias, Reload Maven |
| No qualifying bean | @Service/@Repository/@Component, Clock bean y packages bajo raíz |
| Not a managed type | JpaRepository debe apuntar a JpaEntity con @Entity |
| No property ... found | Nombre de atributo Java en entity y método derivado |
| No instancia naming strategy | Ruta completa de clase + pluralize |
| 404 / 405 / 415 | URL y puerto / método HTTP / Content-Type |
| POST devuelve id null | Devolver aggregate persistido; mapper toDomain |
| 500 al repetir POST | Constraint, traducción de excepción y transacción |
| GET vacío inesperado | Filtros, conversión VO, esquema/perfil |
| No traduce / Swagger no abre | Bundles, Accept-Language / compatibilidad springdoc-Boot |

**Prioridad:** que arranque, POST y GET funcionales con persistencia y reglas; después pulir documentación. En la rúbrica anterior, endpoints (6) + reglas (4) sumaban 10/20; no conocemos la rúbrica nueva. Los tiempos son una propuesta basada en los 100 minutos de aquella PC2.

**Fuentes y límites:** resumen de `Guia_PC2_Backend_DDD_CQRS.md` (guía anterior), ZIP Catch-up y PC2 Lemonade 202610. El PDF anterior solicitaba únicamente POST. El GET y la distribución temporal son preparación propuesta; el proyecto de referencia fue inspeccionado estáticamente, no ejecutado. Para detalles de implementación, volver a la guía extensa.
