# Compte rendu des 17 ateliers + TP « Inscription des Utilisateurs » — Spring Boot 3 + Angular 22

**Projet :** Gestion des Joueurs, Équipes et Auteurs  
**Dossier :** `C:\Users\Dell\JoueursEquipes`  
**Ateliers couverts :** Spring Boot **01, 02, 03, 13, 14, 15** et Angular **01, 02, 03, 04, 05, 07, 08, 09, 16, 16bis, 17** + TP 2025 **Inscription des Utilisateurs**  
**Adaptation demandée :** les entités `Produit` / `Categorie` / `Jouet` des énoncés sont remplacées par **Joueur**, **Equipe** et **Auteur** (l'Auteur étant lié au Joueur).

| Élément | Version |
|---|---|
| JDK | 17 |
| Spring Boot | 3.5.0 |
| MySQL / MariaDB | XAMPP, base `joueurs_DB` |
| Angular | 22 (zoneless) |
| Bootstrap | 5.3 |
| Package Java | `com.nadhem.joueurs` |

- Backend : `http://localhost:8080/joueurs`
- Frontend : `http://localhost:4200`

---

# Partie I — Spring Boot

## Atelier 01 — Les couches Modèle, Persistance et Service

### Objectifs réalisés
1. Initialiser le projet Spring Boot avec les dépendances web, JPA, Data REST, MySQL, Lombok et test.
2. Créer les entités et les repositories.
3. Configurer `application.properties`.
4. Tester les opérations CRUD.
5. Créer la couche Service.

### Fichiers concernés
| Élément de l'énoncé | Réalisation |
|---|---|
| Projet Maven | `backend/pom.xml` |
| Entités | `entities/Joueur.java`, `entities/Equipe.java`, `entities/Auteur.java` |
| Repositories | `repos/JoueurRepository.java`, `repos/EquipeRepository.java`, `repos/AuteurRepository.java` |
| Configuration | `src/main/resources/application.properties` |
| Tests CRUD | `src/test/java/com/nadhem/joueurs/JoueursEquipesApplicationTests.java` |
| Services | `service/JoueurService.java`, `EquipeService.java`, `AuteurService.java` + implémentations |

`application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/joueurs_DB?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
server.servlet.context-path=/joueurs
```

### Entités finales

- **Equipe** : `id`, `nom`, `ville`, `pays`, `dateCreation`, `joueurs`.
- **Joueur** : `id`, `nom`, `prenom`, `age`, `poste`, `numeroMaillot`, `equipe`, `auteur`.
- **Auteur** : `id`, `nom`, `prenom`, `nationalite`, `joueurs`.

### Tests
`testCreate*`, `testFind*`, `testUpdate*`, `testDelete*`, `testLister*` sont présents pour les trois entités.  
Résultat : **20 tests JUnit, 0 échec, 0 erreur**.

---

## Atelier 02 — Persister et interroger les données avec Spring Data JPA

### Équivalence des requêtes

| Énoncé (Produit / Categorie) | Réalisation (Joueur / Equipe / Auteur) |
|---|---|
| `Categorie @OneToMany`, `Produit @ManyToOne` | `Equipe.joueurs`, `Joueur.equipe`, `Auteur.joueurs`, `Joueur.auteur` |
| Lombok | `@Data @NoArgsConstructor @AllArgsConstructor` |
| `findByNomProduit`, `findByNomProduitContains` | `findByNom`, `findByNomContains` |
| `findByNomPrix` avec `@Query` + `@Param` | `findByNomPoste(nom, poste)` |
| `findByCategorie(entité)` | `findByEquipe(Equipe)`, `findByAuteur(Auteur)` |
| `findByCategorieIdCat` | `findByEquipeId(Long)`, `findByAuteurId(Long)` |
| `findByOrderByNomProduitAsc` | `findByOrderByNomAsc`, `findByOrderByNomAsc()` sur Auteur |
| `trierProduitsNomsPrix` | `trierJoueursNomsPoste` |
| Méthodes ajoutées au Service | toutes exposées dans `*Service` / `*ServiceImpl` |

Extrait de `JoueurRepository` :

```java
List<Joueur> findByNom(String nom);
List<Joueur> findByNomContains(String nom);

@Query("select j from Joueur j where j.nom like %:nom% and j.poste = :poste")
List<Joueur> findByNomPoste(@Param("nom") String nom, @Param("poste") String poste);

@Query("select j from Joueur j where j.equipe = ?1")
List<Joueur> findByEquipe(Equipe equipe);

@Query("select j from Joueur j where j.auteur = ?1")
List<Joueur> findByAuteur(Auteur auteur);

List<Joueur> findByEquipeId(Long id);
List<Joueur> findByAuteurId(Long id);
long countByEquipeId(Long idEquipe);
long countByAuteurId(Long idAuteur);
```

`countByEquipeId` et `countByAuteurId` servent à refuser la suppression d'une équipe ou d'un auteur encore utilisé (`409 Conflict`).

---

## Atelier 03 — Développer des Web services REST

### Contrôleurs REST classiques

Trois contrôleurs ont été créés sur le modèle de l'énoncé :

| Contrôleur | Base |
|---|---|
| `JoueurRESTController` | `/api/joueurs` |
| `EquipeRESTController` | `/api/equipes` |
| `AuteurRESTController` | `/api/auteurs` |

Chacun expose : `GET /`, `GET /{id}`, `POST /`, `PUT /`, `DELETE /{id}`.  
Le contrôleur Joueur ajoute :

```java
@GetMapping("/equipe/{idEquipe}")  // joueurs d'une équipe
@GetMapping("/auteur/{idAuteur}")  // joueurs d'un auteur
@GetMapping("/nom/{nom}")          // recherche par nom (atelier Angular 07)
```

Le contrôleur Auteur ajoute `@GetMapping("/nom/{nom}")`.

Les corps sont validés avec `@Valid` et les erreurs sont converties par `GlobalExceptionHandler` :

| Exception | Code HTTP |
|---|---|
| `MethodArgumentNotValidException` | 400 |
| `EntityNotFoundException` | 404 |
| `EquipeNonVideException` / `AuteurNonVideException` | 409 |

### Spring Data REST

```java
@RepositoryRestResource(path = "restJoueurs")  // /restJoueurs
@RepositoryRestResource(path = "restEquipes")  // /restEquipes
@RepositoryRestResource(path = "restAuteurs")  // /restAuteurs
```

`JoueursEquipesApplication` expose les identifiants :

```java
repositoryRestConfiguration.exposeIdsFor(Joueur.class, Equipe.class, Auteur.class);
```

Projections disponibles :

| Projection | Champs |
|---|---|
| `nomJoueur` | nom, prénom, poste |
| `nomEquipe` | nom, pays |
| `nomAuteur` | nom, prénom |

### Vérification Postman / HTTP

Toutes les requêtes suivantes ont répondu **200** :

```
GET /joueurs/api/joueurs
GET /joueurs/api/equipes
GET /joueurs/api/auteurs
GET /joueurs/api/joueurs/equipe/1
GET /joueurs/api/joueurs/auteur/1
GET /joueurs/api/joueurs/nom/Ben
GET /joueurs/api/auteurs/nom/kar
GET /joueurs/restJoueurs
GET /joueurs/restEquipes
GET /joueurs/restAuteurs
```

Une collection importable est fournie : `JoueursEquipes.postman_collection.json`.

---

# Partie II — Angular

## Atelier 01 — Initialisation de l'application Angular

- Projet généré dans `frontend/` avec Angular CLI 22.
- Bootstrap 5 ajouté dans `angular.json` (CSS + JS bundle pour les dropdowns).
- Configuration globale dans `app.config.ts` : `provideRouter`, `provideHttpClient`, `provideZonelessChangeDetection`.
- Adresses du backend centralisées dans `src/environments/environment.ts`.

```ts
export const environment = {
  apiURL: 'http://localhost:8080/joueurs/api',
  restJoueursURL: 'http://localhost:8080/joueurs/restJoueurs',
  restEquipesURL: 'http://localhost:8080/joueurs/restEquipes',
  restAuteursURL: 'http://localhost:8080/joueurs/restAuteurs',
};
```

---

## Atelier 02 — Composants et affichage des données

Composants standalone créés :

| Composant | Rôle |
|---|---|
| `JoueurList` | Tableau des joueurs (équipe + auteur), recherche, tri |
| `EquipeList` | Tableau des équipes, formulaire intégré via composant enfant |
| `AuteurList` | Tableau des auteurs avec pipe de filtre |
| `EquipeJoueurs` | Joueurs d'une équipe donnée |

Les boucles Angular utilisent la syntaxe `@for (... track ...)` et les conditions `@if / @else`.

---

## Atelier 03 — Routage et passage de paramètres

`app.routes.ts` contient les routes :

```
''                       → /joueurs
/joueurs                 → JoueurList
/joueurs/new             → JoueurForm  (guard ADMIN)
/joueurs/edit/:id        → JoueurForm  (guard ADMIN)
/equipes                 → EquipeList
/equipes/new             → EquipeForm  (guard ADMIN)
/equipes/edit/:id        → EquipeForm  (guard ADMIN)
/equipes/:id/joueurs     → EquipeJoueurs
/auteurs                 → AuteurList
/auteurs/new             → AuteurForm  (guard ADMIN)
/auteurs/edit/:id        → AuteurForm  (guard ADMIN)
/rechercheParCategorie   → RechercheParCategorie
/rechercheParNom         → RechercheParNom
/login                   → Login
/app-forbidden           → Forbidden
```

Dans les formulaires, l'identifiant est récupéré avec `ActivatedRoute.snapshot.params['id']` et le champ ID est affiché en lecture seule en mode édition.

---

## Atelier 04 — Formulaires et relation avec la catégorie

`JoueurForm` contient deux listes déroulantes chargées au `ngOnInit()` :

```ts
this.equipeService.getAllEquipes().subscribe(...);
this.auteurService.listeAuteurs().subscribe(...);
```

À la soumission, les objets complets sont reconstruits à partir des IDs sélectionnés :

```ts
this.joueur.equipe = this.equipes.find(e => e.id === this.equipeId) ?? null;
this.joueur.auteur = this.auteurs.find(a => a.id === this.auteurId) ?? null;
```

`EquipeForm` et `AuteurForm` assurent le CRUD complet de ces entités.

---

## Atelier 05 — Consommation d'un web service REST

`JoueurService`, `EquipeService` et `AuteurService` utilisent `HttpClient` avec un en-tête JSON :

```ts
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

listeProduit(): Observable<Joueur[]> {
  return this.http.get<Joueur[]>(this.baseUrl);
}
ajouterProduit(prod: Joueur): Observable<Joueur> {
  return this.http.post<Joueur>(this.baseUrl, prod, httpOptions);
}
```

Les noms de l'énoncé (`listeProduit`, `ajouterProduit`, `updateProduit`, `supprimerProduit`) ont été conservés et adaptés aux Joueurs.

---

## Atelier 07 — Recherche : API, keyup et pipe

Trois mécanismes sont implémentés comme demandé :

1. **API** : `GET /api/joueurs/nom/{nom}` via `rechercherParNom()` et `rechercherProds()`.
2. **Événement `keyup`** : `onKeyUp(searchTerm.value)` filtre la liste affichée.
3. **Pipe personnalisé** : `SearchFilterPipe` (`nom`, `prenom`, `poste`, équipe, auteur).

Pages dédiées : `/rechercheParNom` et `/rechercheParCategorie`.

---

## Atelier 08 — Transmettre des données entre composants

`UpdateEquipe` est un composant enfant réutilisable :

```ts
@Input() categorie!: Equipe;
@Input() ajout!: boolean;
@Output() categorieUpdated = new EventEmitter<Equipe>();

saveCategorie(): void {
  this.categorieUpdated.emit(this.categorie);
}
```

`EquipeList` lui passe les valeurs avec `[categorie]`, `[ajout]` et reçoit l'événement avec `(categorieUpdated)="categorieUpdated($event)"`.

---

## Atelier 09 — Consommer Spring Data REST côté Angular

Les réponses HAL sont typées :

```ts
export interface EquipeWrapper {
  _embedded: { equipes: Equipe[] };
}
export interface AuteurWrapper {
  _embedded: { auteurs: Auteur[] };
}
```

`EquipeList` charge les équipes depuis `/restEquipes` et utilise un repli sur `/api/equipes` si Data REST est indisponible. `RechercheParCategorie` charge également les équipes via `/restEquipes`.

---

## Atelier 16 — Authentification, droits d'accès et LocalStorage

- `User` (username, password, roles) et `AuthService` avec deux comptes : `admin/123` (ADMIN) et `joueur/123` (USER).
- `SignIn()`, `logout()`, `isAdmin()`, `setLoggedUserFromLocalStorage()`, `getUserRoles()`.
- `joueurGuard` protège les routes d'écriture.
- `Login` affiche une erreur si les identifiants sont invalides.
- `Forbidden` affiche la page d'accès refusé.
- La navbar contient le menu **Gestion**, le dropdown utilisateur et le lien **Logout**.
- Le `App` restaure la session depuis le LocalStorage au démarrage.

---

## Note technique — Angular 22 « zoneless »

Angular 22 ne déclenche plus automatiquement la détection de changements après un appel HTTP asynchrone.  
Chaque composant appelle donc `ChangeDetectorRef.markForCheck()` dans les callbacks `next` et `error`. La configuration est :

```ts
provideZonelessChangeDetection(),
provideRouter(routes),
provideHttpClient()
```

---

# Partie III — JWT / Sécurité (ateliers supplémentaires)

## Atelier 13 — Génération du token JWT

### Objectifs réalisés
1. Ajout de `spring-boot-starter-security` et `com.auth0:java-jwt:3.4.1` dans `backend/pom.xml`.
2. Création des entités `User` et `Role` et de leurs repositories (`app_user`, `app_role`, `user_role`).
3. Ajout de la couche service : `UserService` / `UserServiceImpl` avec mot de passe **BCrypt**.
4. Configuration de `MyUserDetailsService` (chargement de l'utilisateur et de ses rôles depuis la base).
5. Création de `JWTAuthenticationFilter` : sur `POST /login`, authentification puis génération du JWT
   (subject = username, claim `roles`, expiration 10 jours, signature HMAC256 avec `SecParams.SECRET`).
6. Initialisation des comptes `admin/123` (ADMIN) et `joueur/123` (USER) dans `UserDataInitializer`.

### Fichiers concernés
| Élément de l'énoncé | Réalisation |
|---|---|
| `User`, `Role`, repositories | `entities/User.java`, `entities/Role.java`, `repos/UserRepository.java`, `repos/RoleRepository.java` |
| Service | `service/UserService.java`, `service/UserServiceImpl.java` |
| `MyUserDetailsService` | `security/MyUserDetailsService.java` |
| Filtre de génération | `security/JWTAuthenticationFilter.java` |
| Endpoint de génération | `restcontrollers/LoginController.java` (fallback robuste sur `POST /login`) |
| Constantes | `security/SecParams.java` (`SECRET`, `EXP_TIME`, `PREFIX`, `LOGIN_URL`) |
| Comptes de test | `UserDataInitializer.java` |

Le token est renvoyé dans l'en-tête `Authorization` **et** dans le corps JSON :
`{"token":"...","username":"admin","roles":["ADMIN","USER"]}`.

---

## Atelier 14 — Vérification de l'authenticité du token JWT

- `JWTAuthorizationFilter` (étend `OncePerRequestFilter`) lit l'en-tête `Authorization`,
  vérifie le préfixe `Bearer `, valide la signature et l'expiration avec `JWTVerifier`, puis
  reconstruit l'`Authentication` avec les autorités issues du claim `roles`.
- Un token invalide / expiré répond **401** avec `{"message":"Token JWT invalide ou expire"}`.
- Une API protégée réservée aux ADMIN a été ajoutée : `GET /api/users/all` (`UserRestController`).

```java
JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build();
DecodedJWT decodedJWT = verifier.verify(jwt.substring(SecParams.PREFIX.length()));
String username = decodedJWT.getSubject();
List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
```

---

## Atelier 15 — Sécuriser les API REST par authentification JWT

`SecurityConfig` applique les règles de gestion de l'énoncé, adaptées à notre domaine :

| Règle | Résultat |
|---|---|
| `POST /login` | public |
| `GET /api/joueurs/**`, `/api/equipes/**`, `/api/auteurs/**` | `ADMIN` ou `USER` |
| `POST/PUT/DELETE` sur ces API | `ADMIN` uniquement |
| `GET/POST/PUT/PATCH/DELETE` sur `/restJoueurs`, `/restEquipes`, `/restAuteurs` | lecture `ADMIN`/`USER`, écriture `ADMIN` |
| `GET /api/users/all` | `ADMIN` uniquement |
| toute autre requête | authentifiée |

La session est **stateless** (`SessionCreationPolicy.STATELESS`), CSRF est désactivé (API REST),
et CORS autorise `http://localhost:4200` avec l'en-tête `Authorization` exposé.
Les erreurs sont JSON : **401** (non authentifié) et **403** (droits insuffisants).

---

## Atelier 16bis — Consommation avec Angular des API sécurisées

- Installation de `@auth0/angular-jwt` (`JwtHelperService`).
- `environment.ts` ajoute `authURL: 'http://localhost:8080/joueurs'`.
- `AuthService.login(user)` appelle `POST /login`, récupère l'en-tête `Authorization`,
  enregistre le token dans le LocalStorage (`jwt`) et décode `sub` / `roles` avec `JwtHelperService`.
- `isAdmin()`, `isTokenExpired()`, `logout()` et `setLoggedUserFromLocalStorage()` sont conservés.
- `Login.onLoggedin()` utilise désormais le vrai login backend ; en cas d'échec, `erreur = 1`.
- `App.ngOnInit()` recharge le token et redirige vers `/login` s'il est absent ou expiré.

---

## Atelier 17 — Créer un Token Interceptor

```ts
export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  if (req.url.includes('/login')) return next(req);
  const jwt = authService.getToken();
  if (!jwt) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${jwt}` } }));
};
```

Enregistré dans `app.config.ts` via `provideHttpClient(withInterceptors([tokenInterceptor]))`.
Tous les services (`JoueurService`, `EquipeService`, `AuteurService`, Data REST) profitent
automatiquement de l'en-tête sans modification de leurs appels.

---

# Vérification globale

| Vérification | Résultat |
|---|---|
| `.\mvnw.cmd test` | **25 tests, 0 failure, 0 error** |
| `.\mvnw.cmd -DskipTests package` | JAR généré : `target/joueurs-equipes-0.0.1-SNAPSHOT.jar` |
| `npm.cmd run build` | Build Angular de production OK |
| Endpoints REST classiques | HTTP 200 avec un token `ADMIN` |
| Endpoints Spring Data REST | HTTP 200 avec un token `ADMIN` |
| `POST /login` | HTTP 200 + token JWT |
| Appel sans token | HTTP 401 `Authentification requise` |
| Appel avec token invalide | HTTP 401 `Token JWT invalide ou expire` |
| Écriture avec un token `USER` | HTTP 403 `Acces refuse` |
| Frontend `http://localhost:4200` | HTTP 200 |
| Backend `http://localhost:8080/joueurs/...` | HTTP 200 (authentifié) |

## Données de démonstration laissées dans la base

- **5 équipes** : Espérance de Tunis, Juventus, Real Madrid, Manchester City, Al Ahly.
- **3 auteurs** : Karim Ben Ali, Marco Rossi, Lucia Garcia.
- **11 joueurs** répartis sur ces équipes, chacun avec un auteur.

## Lancement rapide

```powershell
cd C:\Users\Dell\JoueursEquipes\backend
.\mvnw.cmd spring-boot:run

cd C:\Users\Dell\JoueursEquipes\frontend
npm.cmd start
```

Connexion JWT : `admin / 123` pour administrer, `joueur / 123` pour consulter.
Le token est automatiquement ajouté à chaque requête par le `tokenInterceptor`.

**Résultat : les 17 ateliers sont intégrés dans un seul projet cohérent avec les trois entités demandées.**

---

# Partie IV — TP 2025 : Inscription des Utilisateurs

## Objectifs réalisés

1. Formulaire Angular `/register` avec validation : nom obligatoire, email valide,
   mot de passe ≥ 6 caractères et confirmation identique.
2. API `POST /register` côté Spring Boot avec validation backend (`@Valid`).
3. Génération d’un code secret à **6 chiffres** (`SecureRandom`, unique en base).
4. Envoi du code par email via `spring-boot-starter-mail` (`JavaMailSender` + `MimeMessageHelper`).
5. Expiration du code après **15 minutes** (`VerificationToken`).
6. API `GET /verifyEmail/{code}` qui active le compte.
7. Rôle **USER** par défaut et compte désactivé (`enabled=false`) jusqu’à validation.
8. Gestion d’erreurs JSON (`ErrorDetails` + `errorCode`).
9. Blocage du login tant que le compte est désactivé (**403** `errorCause=disabled`).
10. CORS maintenu (`http://localhost:4200`, en-tête `Authorization` exposé).
11. Notifications **Toastr** + spinner pendant l’envoi / la validation.

## Fichiers ajoutés / modifiés

| Côté | Fichier | Rôle |
|---|---|---|
| Backend | `entities/User.java` | ajout de `email` |
| Backend | `entities/VerificationToken.java` | code + `expirationTime` (15 min) |
| Backend | `repos/VerificationTokenRepository.java` | `findByToken`, `existsByToken` |
| Backend | `register/RegistrationRequest.java` | DTO avec `jakarta.validation` |
| Backend | `util/EmailSender.java`, `util/EmailService.java` | envoi SMTP / mode démo console |
| Backend | `restcontrollers/RegistrationController.java` | `/register` et `/verifyEmail/{code}` (+ alias `/api/users/...`) |
| Backend | `exceptions/ErrorDetails.java` + 5 exceptions | `USER_EMAIL_ALREADY_EXISTS`, `USER_USERNAME_ALREADY_EXISTS`, `INVALID_TOKEN`, `EXPIRED_TOKEN`, `EMAIL_SENDING_FAILED` |
| Backend | `exceptions/GlobalExceptionHandler.java` | réponses JSON 400 / 403 / 404 / 500 |
| Backend | `service/UserServiceImpl.java` | inscription, BCrypt, rôle USER, code, validation |
| Backend | `security/SecurityConfig.java` | endpoints publics + CORS `maxAge` |
| Backend | `security/JWTAuthenticationFilter.java` | message spécifique `DisabledException` |
| Backend | `application.properties` | configuration SMTP et 15 minutes |
| Backend | `src/test/.../RegistrationServiceTests.java` | 5 tests unitaires du flux |
| Frontend | `models/user.model.ts` | ajout de `email`, `enabled` |
| Frontend | `services/auth.service.ts` | `registerUser`, `validateEmail`, `regitredUser` |
| Frontend | `services/token.interceptor.ts` | exclusion de `/register` et `/verifyEmail` |
| Frontend | `register/register.ts/.html/.css` | formulaire réactif + Toastr + spinner |
| Frontend | `verifEmail/verif-email.ts/.html/.css` | saisie du code + auto-login |
| Frontend | `app.routes.ts`, `app.ts`, `app.html` | routes publiques et lien « S'inscrire » |
| Frontend | `app.config.ts`, `angular.json` | `provideAnimations()`, `provideToastr()`, `toastr.css` |

## API et réponses

| Méthode | URL | Succès | Erreurs possibles |
|---|---|---|---|
| POST | `/register` | `200` + User (`enabled:false`, rôle `USER`) | `400` email/username existant, `400` validation |
| GET | `/verifyEmail/{code}` | `200` + User (`enabled:true`) | `404 INVALID_TOKEN`, `400 EXPIRED_TOKEN` |
| POST | `/login` (compte non activé) | — | `403 {"errorCause":"disabled"}` |
| POST | `/login` (compte activé) | `200` + JWT | `401 bad_credentials` |

## Configuration email

Sans variables d’environnement, `EmailService` affiche le code dans la console du backend
afin de garder le flux testable. Pour un envoi réel :

```powershell
$env:MAIL_USERNAME = "votre-adresse@gmail.com"
$env:MAIL_PASSWORD = "mot-de-passe-d-application"
$env:MAIL_FROM = "votre-adresse@gmail.com"
```

## Vérification

`.\mvnw.cmd test` → **25 tests, 0 failure, 0 error**. `npm.cmd run build` → OK.
Test HTTP complet effectué : register `200`, login avant validation `403`, code extrait du log,
verifyEmail `200`, login après validation `200` + JWT, `GET /api/joueurs` avec le token `USER`
`200`, `/api/users/all` `403`, doublon `400`, code invalide `404`, champs invalides `400`.
