# Gestion des Joueurs, Équipes et Auteurs — Full Stack (Spring Boot 3 + Angular)

Projet Full Stack réalisé dans le dossier `C:\Users\Dell\JoueursEquipes`.
Les 12 ateliers initiaux (Spring Boot **01–03** et Angular **01, 02, 03, 04, 05, 07, 08, 09, 16**),
puis les 5 ateliers JWT/Sécurité (Spring **13, 14, 15** et Angular **16bis, 17**), ont été adaptés
au domaine demandé : **Joueur**, **Equipe** et **Auteur**.

> Ouvrez directement le projet dans IntelliJ / votre éditeur : le backend, le frontend,
> les données de démonstration, les tests et la documentation sont déjà en place.

---

## 1. Structure du projet

```
JoueursEquipes/
├── backend/     → Spring Boot 3.5.0 (Maven, Java 17)
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/nadhem/joueurs/
│       │   ├── JoueursEquipesApplication.java
│       │   ├── DatabaseSeeder.java          → données de démonstration
│       │   ├── UserDataInitializer.java     → comptes admin / joueur (Atelier 13)
│       │   ├── entities/                    → Joueur, Equipe, Auteur, User, Role + projections
│       │   ├── repos/                       → 5 repositories (dont User / Role)
│       │   ├── service/                     → interfaces + implémentations
│       │   ├── restcontrollers/             → contrôleurs REST + LoginController + /api/users/all
│       │   ├── security/                    → JWTAuthenticationFilter, JWTAuthorizationFilter,
│       │   │                                  SecurityConfig, MyUserDetailsService, SecParams
│       │   └── exceptions/                  → gestion 400 / 404 / 409
│       ├── main/resources/application.properties
│       └── test/java/...                    → 20 tests JUnit
├── frontend/    → Angular 22 (zoneless) + Bootstrap 5
│   └── src/
│       ├── environments/environment.ts
│       └── app/
│           ├── models/      services/      guards/      pipes/
│           │                 → services/token.interceptor.ts (Atelier 17)
│           ├── joueurs/     equipes/       auteurs/
│           ├── recherche/   login/         forbidden/
│           ├── app.routes.ts  app.config.ts  app.ts  app.html
├── README.md
├── COMPTE-RENDU-TP.md
└── JoueursEquipes.postman_collection.json
```

---

## 2. Modèle de données

| Entité | Champs |
|---|---|
| **Equipe** | `id`, `nom`, `ville`, `pays`, `dateCreation`, `joueurs` |
| **Joueur** | `id`, `nom`, `prenom`, `age`, `poste`, `numeroMaillot`, `equipe`, `auteur` |
| **Auteur** | `id`, `nom`, `prenom`, `nationalite`, `joueurs` |
| **User** (Atelier 13) | `userId`, `username`, `password` (BCrypt, jamais renvoyé en JSON), `enabled`, `roles` |
| **Role** (Atelier 13) | `id`, `role` (`ADMIN` ou `USER`) |

Relations :
- une **Equipe** a plusieurs **Joueurs** (`@OneToMany`) ; un **Joueur** appartient à une seule **Equipe** (`@ManyToOne`).
- un **Auteur** suit plusieurs **Joueurs** (`@OneToMany`) ; un **Joueur** a au plus un **Auteur** (`@ManyToOne`, colonne `auteur_id`).
- Un **User** a plusieurs **Roles** (`@ManyToMany`, tables `app_user`, `app_role`, `user_role`).
- Les listes `joueurs` sont annotées `@JsonIgnore` pour éviter les boucles infinies JSON.
- Supprimer une équipe ou un auteur encore utilisé renvoie une erreur **409 Conflict**.

---

## 3. Prérequis et lancement

| Outil | Version utilisée |
|---|---|
| JDK | 17 |
| Maven | wrapper `.\mvnw.cmd` |
| MySQL / MariaDB (XAMPP) | port 3306, `root`, sans mot de passe |
| Node.js / npm | installés |
| Angular CLI | 22 |
| Postman / IntelliJ | déjà installés sur la machine |

### Backend

```powershell
cd C:\Users\Dell\JoueursEquipes\backend
.\mvnw.cmd spring-boot:run
```

- Base créée automatiquement : `joueurs_DB`.
- Contexte : `/joueurs`.
- API classique : `http://localhost:8080/joueurs/api`.
- Spring Data REST : `http://localhost:8080/joueurs/restJoueurs`, `/restEquipes`, `/restAuteurs`.

### Frontend

```powershell
cd C:\Users\Dell\JoueursEquipes\frontend
npm.cmd start
```

Ouvrir <http://localhost:4200>.

### Comptes de connexion (Ateliers 16 + 13)

| Utilisateur | Mot de passe | Rôle |
|---|---|---|
| `admin` | `123` | `ADMIN` (peut ajouter / modifier / supprimer) |
| `joueur` | `123` | `USER` (lecture seule) |

Les comptes sont désormais stockés en base (`app_user` / `app_role`) avec des mots de passe
**BCrypt**. Le login appelle `POST http://localhost:8080/joueurs/login` et renvoie un **JWT**
(dans l'en-tête `Authorization` et dans le corps JSON). Le token est conservé dans le
**LocalStorage** sous la clé `jwt`. Le menu contextuel (dropdown) et la page `/app-forbidden`
restent en place.

> **PowerShell** : utiliser `npm.cmd`, `ng.cmd` et `.\mvnw.cmd` (les scripts `.ps1` peuvent être bloqués).
>
> **Angular 22 est zoneless** : chaque `subscribe()` appelle `ChangeDetectorRef.markForCheck()`
> et `app.config.ts` utilise `provideZonelessChangeDetection()`.

---

## 4. Fonctionnalités de l'interface

- **Joueurs** : liste avec équipe et auteur, recherche instantanée (`keyup`), recherche par API, tri sur les colonnes, ajout / modification / suppression (formulaire avec listes déroulantes **Équipe** et **Auteur**).
- **Équipes** : liste issue de **Spring Data REST** (format HAL), composant enfant `@Input/@Output` pour ajouter / éditer, recherche, tri, formulaire complet, page « Joueurs de l'équipe ».
- **Auteurs** : CRUD complet, filtre via le pipe `searchFilter`, suppression protégée.
- **Recherche** :
  - `/rechercheParNom` : API `/api/joueurs/nom/{nom}`, `keyup` et pipe.
  - `/rechercheParCategorie` : équipes via Spring Data REST puis `/api/joueurs/equipe/{id}`.
- **Authentification** : login JWT réel, guard sur les routes d'écriture, rôles décodés du token,
  LocalStorage, intercepteur `Authorization: Bearer <jwt>`, dropdown utilisateur, logout.

---

## 5. Web services REST

Collection Postman prête à importer : [`JoueursEquipes.postman_collection.json`](./JoueursEquipes.postman_collection.json).

### Authentification JWT — base `http://localhost:8080/joueurs`

| Méthode | URL | Description | Accès |
|---|---|---|---|
| POST | `/login` | Génère le JWT (`Authorization` + corps `{token, username, roles}`) | public |
| GET | `/api/users/all` | Liste des utilisateurs (API protégée de l'atelier 14) | `ADMIN` |

Toutes les autres URL ci-dessous nécessitent l'en-tête `Authorization: Bearer <jwt>`.
Les lectures (`GET`) sont autorisées pour `ADMIN` et `USER` ; les écritures (`POST`, `PUT`,
`PATCH`, `DELETE`) sont réservées à `ADMIN`. Spring Data REST est protégé de la même manière.

### Contrôleurs REST — base `http://localhost:8080/joueurs/api`

| Méthode | URL | Description |
|---|---|---|
| GET | `/joueurs` | Tous les joueurs |
| GET | `/joueurs/{id}` | Un joueur |
| POST | `/joueurs` | Créer (avec `equipe` et `auteur` optionnel) |
| PUT | `/joueurs` | Modifier |
| DELETE | `/joueurs/{id}` | Supprimer |
| GET | `/joueurs/equipe/{idEquipe}` | Joueurs d'une équipe |
| GET | `/joueurs/auteur/{idAuteur}` | Joueurs d'un auteur |
| GET | `/joueurs/nom/{nom}` | Recherche par nom |
| GET | `/equipes`, `/equipes/{id}` | CRUD équipes |
| POST | `/equipes`, PUT `/equipes`, DELETE `/equipes/{id}` | CRUD équipes |
| GET | `/auteurs`, `/auteurs/{id}` | CRUD auteurs |
| POST | `/auteurs`, PUT `/auteurs`, DELETE `/auteurs/{id}` | CRUD auteurs |
| GET | `/auteurs/nom/{nom}` | Recherche d'auteur |

Exemple de création :

```json
POST http://localhost:8080/joueurs/api/joueurs
{
  "nom": "Ben Romdhane",
  "prenom": "Mohamed",
  "age": 26,
  "poste": "Milieu",
  "numeroMaillot": 8,
  "equipe": { "id": 1 },
  "auteur": { "id": 1 }
}
```

Exemple d'appel authentifié :

```powershell
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/joueurs/login" `
  -ContentType "application/json" -Body '{"username":"admin","password":"123"}'
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/joueurs/api/joueurs" -Headers $headers
```

### Spring Data REST — base `http://localhost:8080/joueurs`

| URL | Description |
|---|---|
| `/restJoueurs`, `/restEquipes`, `/restAuteurs` | Listes HAL (ids exposés) |
| `/restJoueurs?size=2&page=0` | Pagination |
| `/restJoueurs?sort=nom,desc` | Tri |
| `/restJoueurs?projection=nomJoueur` | Projection nom / prénom / poste |
| `/restEquipes?projection=nomEquipe` | Projection nom / pays |
| `/restAuteurs?projection=nomAuteur` | Projection nom / prénom |
| `/restJoueurs/search/findByNomContains?nom=Ben` | Recherche dérivée |

---

## 6. Correspondance avec les 17 ateliers

| Atelier | Sujet | Réalisation dans le projet |
|---|---|---|
| Spring **01** | Projet Spring Boot, entités, repository, service, CRUD, config | `entities/`, `repos/`, `service/`, `application.properties`, 20 tests JUnit |
| Spring **02** | JPA, associations, Lombok, requêtes dérivées / JPQL, tri | `Equipe.joueurs`, `Joueur.equipe`, `Auteur.joueurs`, repositories |
| Spring **03** | Web services REST, Data REST, IDs, projections | `restcontrollers/`, `@RepositoryRestResource`, `exposeIdsFor`, `*Projection` |
| Angular **01** | Initialisation projet Angular | `frontend/`, `angular.json`, Bootstrap, `app.config.ts` |
| Angular **02** | Composants, binding, listes | `joueur-list`, `equipe-list`, `auteur-list` |
| Angular **03** | Routing et paramètres d'URL | `app.routes.ts`, `ActivatedRoute` dans les formulaires |
| Angular **04** | Formulaires + relation catégorie | `joueur-form` (Équipe + Auteur), `equipe-form`, `auteur-form` |
| Angular **05** | Consommation REST avec HttpClient | services `*Service`, `environment.ts`, `httpOptions` |
| Angular **07** | Recherche (API, keyup, pipe) | `recherche/`, `SearchFilterPipe`, `onKeyUp()`, `rechercherParNom()` |
| Angular **08** | `@Input()` / `@Output()` entre composants | `equipes/update-equipe.ts` + `equipe-list` |
| Angular **09** | Spring Data REST côté client | `EquipeWrapper`, `AuteurWrapper`, `listeEquipesDataRest()` |
| Angular **16** | Auth, guard, LocalStorage, menu contextuel | `AuthService`, `joueurGuard`, `login/`, `forbidden/`, navbar |
| Spring **13** | Génération du JWT | `User`, `Role`, `UserService`, `MyUserDetailsService`, `JWTAuthenticationFilter`, `UserDataInitializer`, `POST /login` |
| Spring **14** | Vérification du JWT + rôles | `JWTAuthorizationFilter`, `SecParams`, `/api/users/all`, `403` sur droits insuffisants |
| Spring **15** | Sécurisation des API REST | `SecurityConfig` : stateless, CORS, `requestMatchers`, lecture `ADMIN`/`USER`, écriture `ADMIN` |
| Angular **16bis** | Consommation des API sécurisées | `AuthService.login()`, `JwtHelperService`, décodage des rôles, `Login` |
| Angular **17** | Token Interceptor | `token.interceptor.ts`, `provideHttpClient(withInterceptors([...]))` |

Le compte rendu détaillé est dans [`COMPTE-RENDU-TP.md`](./COMPTE-RENDU-TP.md).

---

## 7. Données de démonstration

Le `DatabaseSeeder` crée la base au premier démarrage si elle est vide :

- **5 équipes** : Espérance de Tunis, Juventus, Real Madrid, Manchester City, Al Ahly.
- **3 auteurs** : Karim Ben Ali, Marco Rossi, Lucia Garcia.
- **11 joueurs** répartis sur ces équipes, chacun rattaché à un auteur.

Pour repartir de zéro :

```powershell
# Arrêter le backend puis :
& "C:\xampp\mysql\bin\mysql.exe" -u root -e "DROP DATABASE IF EXISTS joueurs_DB;"
```

---

## 8. Vérifications effectuées

- `.\mvnw.cmd test` → **20 tests, 0 échec, 0 erreur**.
- `npm.cmd run build` → build Angular de production OK.
- `POST /login` → **200** avec token ; sans token les API répondent **401** ; avec un token invalide **401** ; un `USER` sur une écriture répond **403**.
- Avec un token `ADMIN`, tous les endpoints ci-dessus répondent **HTTP 200** (contrôleurs + Spring Data REST).
- Backend et frontend ont été laissés démarrés sur les ports **8080** et **4200**.
