# EduConnect - API Gateway

Point d'entrée unique de la plateforme : la Gateway reçoit les requêtes, choisit une instance de microservice disponible et lui transfère la requête.

Implémentation du TP **« Implémentation API Gateway »** (Parties 1, 2 et 3).

- **Nom** : `api-gateway`
- **Port** : `8080`
- **Stack** : Spring Boot `4.1.3`, Spring Cloud `2025.1.5`, Spring Cloud Gateway `5.0.x`, Java `21`

---

## 1. Rôle

```text
Client
  │
  ▼
API Gateway (8080)
  ├── /candidats/** ──► candidat-service (8081, ...)
  └── /jobs/**      ──► job-service      (8082, ...)
```

La Gateway permet :

- un **point d'entrée unique** (le client ne connaît que le port 8080) ;
- le **routage** par chemin (`Path=/candidats/**`) ;
- la **découverte de services** via Eureka (`lb://candidat-service`) ;
- le **load balancing** entre plusieurs instances ;
- la centralisation future de la sécurité, CORS, logs, rate limiting…

---

## 2. Structure

```text
Backend/api-gateway/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
└── src/main/
    ├── java/com/educonnect/gateway/
    │   ├── ApiGatewayApplication.java      # @EnableDiscoveryClient
    │   └── config/
    │       ├── StaticRouteConfig.java      # Partie 2, méthode 1 (classe)
    │       ├── DynamicRouteConfig.java     # Partie 3, méthode 1 (classe)
    │       ├── RandomLoadBalancerConfig.java # Load balancing Random
    │       └── LoadBalancerConfig.java     # Applique Random à candidat
    └── resources/
        ├── application.properties          # Commun + profil par défaut
        ├── application-dynamic.properties  # Partie 3, méthode 2
        └── application-static-props.properties # Partie 2, méthode 2
```

---

## 3. Dépendances principales

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

> **Important (Spring Cloud 2025.1 / Gateway 5.x)** : l'ancien starter `spring-cloud-starter-gateway` et l'ancien préfixe `spring.cloud.gateway.routes` ne fonctionnent plus. Il faut utiliser `spring-cloud-starter-gateway-server-webflux` et `spring.cloud.gateway.server.webflux.routes`.

---

## 4. Les 4 configurations du TP

Le projet implémente **les deux méthodes** pour **les deux approches**, via des profils Spring :

| Profil | Approche | Méthode | Où sont les routes | Eureka |
|---|---|---|---|---|
| `static` | Statique | Classe Java (`RouteLocator`) | `StaticRouteConfig` | Non |
| `static-props` | Statique | `application.properties` | `application-static-props.properties` | Non |
| `dynamic` *(défaut)* | Dynamique | `application.properties` | `application-dynamic.properties` | Oui (`lb://`) |
| `dynamic-class` | Dynamique | Classe Java (`RouteLocator`) | `DynamicRouteConfig` | Oui (`lb://`) |

### Routes configurées

| Chemin | Service | Statique | Dynamique |
|---|---|---|---|
| `/candidats/**` | candidat-service | `http://localhost:8081` | `lb://candidat-service` |
| `/jobs/**` | job-service | `http://localhost:8082` | `lb://job-service` |

---

## 5. Démarrage

### Prérequis

1. **Eureka** démarré sur `http://localhost:8761` :

```powershell
cd Backend\eureka-server
.\mvnw.cmd spring-boot:run
```

2. **Microservices** démarrés (au moins `candidat-service` et `job-service`) :

```powershell
cd Backend\microservices\candidat-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd Backend\microservices\job-service
.\mvnw.cmd spring-boot:run
```

3. **Gateway** :

```powershell
cd Backend\api-gateway
.\mvnw.cmd spring-boot:run
```

### Choisir une configuration

```powershell
# Dynamique via properties (défaut)
.\mvnw.cmd spring-boot:run

# Statique via classe Java
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=static"

# Statique via properties
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=static-props"

# Dynamique via classe Java
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dynamic-class"
```

Ou avec le JAR :

```powershell
java -jar target\api-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=static
```

---

## 6. Vérification

### Via la Gateway

```powershell
Invoke-RestMethod http://localhost:8080/candidats
Invoke-RestMethod http://localhost:8080/candidats/1
Invoke-RestMethod http://localhost:8080/jobs
Invoke-RestMethod http://localhost:8080/jobs/1
```

Réponse attendue :

```json
{
  "service": "CANDIDAT-SERVICE",
  "message": "Liste des candidats",
  "port": "8081"
}
```

> Deux contrôleurs de démonstration (`CandidatController`, `JobController`) ont été ajoutés aux microservices pour exposer `/candidats` et `/jobs` et permettre de vérifier le routage et le load balancing.

### Routes chargées

```powershell
Invoke-RestMethod http://localhost:8080/actuator/gateway/routes
```

### Santé de la Gateway

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

---

## 7. Partie 3 — Réponses du rendu

### 2.b. Quel algorithme de load balancing est utilisé par défaut ?

L'algorithme par défaut est **RoundRobin** (`RoundRobinLoadBalancer` de Spring Cloud LoadBalancer).

- Les instances sont sélectionnées **cycliquement**, dans l'ordre.
- Exemple avec deux instances de `job-service` (8082 et 8087) : `8082 → 8087 → 8082 → 8087…`
- Il est visible dans les logs : `LoadBalancerClientFilter` / `RoundRobinLoadBalancer` indique l'instance choisie.

### 3. Autres algorithmes et configuration du Random pour candidat

Autres stratégies disponibles :

| Algorithme | Classe / mécanisme |
|---|---|
| **RoundRobin** (défaut) | `RoundRobinLoadBalancer` |
| **Random** | `RandomLoadBalancer` |
| **Zone preference** | `ZonePreferenceServiceInstanceListSupplier` |
| **Personnalisé** | Implémentation de `ReactorLoadBalancer` / `ServiceInstanceListSupplier` (ex. weighted, health-based) |

Configuration mise en place : **Random uniquement pour `candidat-service`**, `job-service` restant en RoundRobin.

```java
public class RandomLoadBalancerConfig {
    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
}
```

```java
@Configuration
@Profile("!round-robin")
@LoadBalancerClient(name = "candidat-service", configuration = RandomLoadBalancerConfig.class)
public class LoadBalancerConfig {
}
```

Pour comparer avec le RoundRobin sur candidat :

```powershell
java -jar target\api-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=dynamic,round-robin
```

Avec deux instances de candidat (8081 et 8086), envoyer plusieurs requêtes :

```powershell
1..10 | ForEach-Object { (Invoke-RestMethod http://localhost:8080/candidats).port }
```

- Sans le profil `round-robin` : ports **aléatoires** (Random).
- Avec `round-robin` : ports **alternés** (RoundRobin).

### 2.a. Activer les logs de routage

Déjà configuré dans `application.properties` :

```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.reactor.netty.http.client=DEBUG
```

À chaque requête, la console affiche la route utilisée et l'instance sélectionnée.

---

## 7 bis. Résultats de vérification (mesurés)

Environnement : Eureka `8761`, deux instances de `candidat-service` (`8081`, `8086`), deux instances de `job-service` (`8082`, `8087`), Gateway `8080`.

| Profil | Routes chargées (`/actuator/gateway/routes`) | Ports observés |
|---|---|---|
| `static` | `candidat-static → http://localhost:8081`, `job-static → http://localhost:8082` | candidat : `8081, 8081, 8081, 8081` — job : `8082, 8082, 8082, 8082` |
| `static-props` | `candidat-static → http://localhost:8081`, `job-static → http://localhost:8082` | candidat : `8081, 8081, 8081` — job : `8082, 8082, 8082` |
| `dynamic` (défaut) | `candidat-service → lb://candidat-service`, `job-service → lb://job-service` | candidat : `8086, 8086, 8081, 8081, 8081, 8081, 8086, 8086, 8081, 8086, 8081, 8086` (**Random**) — job : `8082, 8087, 8082, 8087, …` (**RoundRobin**) |
| `dynamic-class` | `candidat-dynamic → lb://candidat-service`, `job-dynamic → lb://job-service` | candidat : `8081, 8086, 8081, 8081, 8081, 8086` (**Random**) — job : `8087, 8082, 8087, 8082` (**RoundRobin**) |
| `dynamic,round-robin` | idem `dynamic` | candidat : `8086, 8081, 8086, 8081, 8086, 8081, 8086, 8081` (**RoundRobin**) |

Conclusion : le routage statique ne touche que l'instance ciblée, le routage dynamique résout bien les instances via Eureka, et le load balancing est **Random sur candidat** / **RoundRobin sur job** par défaut, avec retour au RoundRobin sur candidat via le profil `round-robin`.

---

## 8. Ajouter un autre microservice

Ajouter la route dans le fichier du profil concerné, par exemple :

```properties
spring.cloud.gateway.server.webflux.routes[2].id=meeting-service
spring.cloud.gateway.server.webflux.routes[2].uri=lb://MEETING-SERVICE
spring.cloud.gateway.server.webflux.routes[2].predicates[0]=Path=/meetings/**
```

---

## 9. Dépannage

| Symptôme | Cause probable | Solution |
|---|---|---|
| `503 Service Unavailable` | Aucune instance dans Eureka | Vérifier `http://localhost:8761` et le nom du service |
| `404` via la Gateway | Route non chargée / mauvais profil | Vérifier `/actuator/gateway/routes` et le profil actif |
| Routes ignorées | Ancien préfixe `spring.cloud.gateway.routes` | Utiliser `spring.cloud.gateway.server.webflux.routes` |
| `Spring MVC found on classpath` | Starter MVC ajouté | Retirer `spring-boot-starter-webmvc`, la Gateway est réactive |
| Port 8080 occupé | Autre application | `--server.port=8090` |
