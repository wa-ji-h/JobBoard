# Eureka Discovery Server & Observability

Projet microservices basé sur **Spring Cloud Eureka** pour la découverte de services et **Spring Boot Actuator** pour l'observabilité.

## Structure du projet

```text
Eureka-Observability-Atelier-main/
├── Backend/
│   ├── eureka-server/            # Serveur Eureka (Spring Boot) - port 8761
│   └── microservices/
│       ├── candidat-service/     # Microservice Spring Boot - port 8081
│       ├── job-service/          # Microservice Spring Boot - port 8082
│       ├── meeting-service/      # Microservice Node.js / Express - port 8083 (+ 8084)
│       └── notification-service/ # Microservice Python / Flask - port 8085
├── README.md
├── .gitignore
└── *.png                         # Captures d'écran des résultats
```

Le serveur Eureka est dans **`Backend/eureka-server/`**. Les microservices métier sont regroupés dans **`Backend/microservices/`**.

## Microservices

| Service | Technologie | Port | Enregistrement Eureka |
|---|---|---|---|
| `eureka-server` | Spring Boot | 8761 | Serveur (registre) |
| `candidat-service` | Spring Boot | 8081 | Client |
| `job-service` | Spring Boot | 8082 | Client |
| `meeting-service` | Node.js / Express | 8083 (2ᵉ instance : 8084) | Client `eureka-js-client` |
| `notification-service` | Python / Flask | 8085 | Client `py_eureka_client` |

## Technologies

Spring Boot, Spring Cloud Eureka, Spring Boot Actuator, Node.js, Express, Python, Flask.

## Prérequis

- **Java 21** (pour `eureka-server`, `candidat-service`, `job-service`)
- **Node.js + npm** (pour `meeting-service`)
- **Python 3** avec `flask` et `py_eureka_client` (pour `notification-service`)

---

## Démarrage

> Ordre recommandé : démarrer **Eureka** en premier, puis les microservices.
> Chaque commande est à exécuter depuis la racine du projet.

### 1. Serveur Eureka

```powershell
cd Backend\eureka-server
.\mvnw.cmd spring-boot:run
```

Tableau de bord : <http://localhost:8761>

### 2. Candidat Service (Spring Boot)

```powershell
cd Backend\microservices\candidat-service
.\mvnw.cmd spring-boot:run
```

### 3. Job Service (Spring Boot)

```powershell
cd Backend\microservices\job-service
.\mvnw.cmd spring-boot:run
```

### 4. Meeting Service (Node.js)

```powershell
cd Backend\microservices\meeting-service
npm install
node server.js
```

Deuxième instance sur le port 8084 :

```powershell
$env:PORT=8084
node server.js
```

### 5. Notification Service (Python)

```powershell
cd Backend\microservices\notification-service
pip install flask py_eureka_client
python app.py
```

---

## Vérification

| URL | Description |
|---|---|
| <http://localhost:8761> | Tableau de bord Eureka (services enregistrés) |
| <http://localhost:8761/eureka/apps> | Registre au format JSON |
| <http://localhost:8081/actuator/health> | Santé de `candidat-service` |
| <http://localhost:8082/actuator/health> | Santé de `job-service` |
| <http://localhost:8083/> | `meeting-service` |
| <http://localhost:8085/health> | Santé de `notification-service` |

Dans le tableau de bord Eureka, les applications doivent apparaître en statut **UP** :

```text
CANDIDAT-SERVICE
JOB-SERVICE
MEETING-SERVICE
NOTIFICATION-SERVICE
```

---

## Résultats

Four services registered in Eureka.
Two instances of MEETING-SERVICE and NOTIFICATION-SERVICE.

## Team members

- Farah Shimi
- Mohamed saad
- Hyathem ben Messaoud
- Wajih soussou
