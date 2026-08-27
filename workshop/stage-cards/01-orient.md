Status: Working

# Orient Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Build a truthful snapshot of the Inherited System before choosing what to
change.

## Risk controlled

Acting on assumptions about unfamiliar code, tests, local operation, Azure
topology, repository constraints, or product decisions.

## Minimum evidence

- The local run and test paths you observed.
- One concrete application seam and its highest useful public test seam.
- The relevant Azure topology and deployment path.
- Repository constraints that bound the work.
- Observed facts separated from assumptions and unresolved product decisions.

## Optional Copilot example

`/codebase-design` is one concrete, replaceable way to inspect module seams;
use another method when it controls the same orientation risk and leaves
equivalent evidence.

## Exit question

**Do I understand enough to choose the next decision?**

## Evidence

### Observed facts

- **Baseline inspected:** commit `162d84a` on 2026-08-26. The application is a
  Spring Boot 4.1.0 MVC/JPA application. The Maven build requires Java 17 or
  newer; this machine ran Java 17.0.20 and Maven Wrapper 3.9.16.
- **Local run path:** `.\mvnw.cmd spring-boot:run
  "-Dspring-boot.run.arguments=--server.port=18080"` started the application
  with the default in-memory H2 database. Fresh probes returned `UP` from
  `/actuator/health`, HTTP 200 from `/` and `/owners/1`, and six records from
  `/vets`.
- **Test path:** `.\mvnw.cmd test` completed successfully in 1 minute 17
  seconds: 71 tests run, 0 failures, 0 errors, and 2 skipped. The suite includes
  MVC slice tests with `@WebMvcTest` and `MockMvc`, JPA tests with
  `@DataJpaTest`, and full HTTP tests using `@SpringBootTest` on a random port.
- **Concrete application seam:** owner lookup currently enters through
  `GET /owners?lastName=...` or `GET /owners/{ownerId}`, then passes through
  `OwnerController` to `OwnerRepository`. The highest useful existing public
  test seam is `PetClinicIntegrationTests`, which makes HTTP requests to a real
  random-port application. `OwnerControllerTests` is the narrower seam for MVC
  behavior with a mocked repository.
- **Public application surface:** the inspected MVC routes expose the welcome
  page, owner search/details and owner/pet/visit forms, veterinarian HTML and
  JSON views, and `/oups`; Actuator endpoints are exposed for development and
  testing. No Clinic Assistant, Spring AI dependency, or chat endpoint exists
  in the inspected baseline.
- **Documented Azure topology:** `azure.yaml` packages the executable JAR with
  `./mvnw -q -DskipTests package` and deploys it through `azd` and Bicep. The
  template provisions one resource group containing a Linux Basic B1 App
  Service running Java 21 and a projectless Microsoft Foundry `AIServices`
  account with a pinned `gpt-5.4-mini` deployment. The web app uses a
  system-assigned managed identity with the Foundry User role; local
  authentication is disabled. Preflight verifies `/actuator/health`, topology,
  model settings, identity, role assignment, and app settings.
- **Repository constraints:** the Clinic Assistant must remain staff-facing,
  read-only, and inside the existing Spring Boot process. A
  framework-agnostic read-only query boundary must return purpose-built records
  rather than repositories or JPA entities. It must answer only from retrieved
  PetClinic data, admit absent or unsupported requests, and avoid identity
  guesses, claimed mutations, and veterinary advice. Write tools, RAG, Azure AI
  Search, Foundry projects or Agent Service, another database, and persistent
  transcripts are outside the workshop slice. Consequential scope, risk,
  acceptance, and residual-risk decisions remain human-owned.

### Inferences and assumptions

- A new assistant endpoint can likely use the same Spring MVC process and the
  existing owner, pet, visit, and veterinarian data, but it should sit above a
  new purpose-built read-only query boundary rather than expose the existing
  repositories or entities directly.
- The documented Azure template is treated as the intended deployment topology,
  not proof that this attendee environment is currently deployed or healthy.

### Missing or fragile evidence

- **Missing Azure runtime evidence:** `bash scripts/azure-readiness.sh` stopped
  immediately with `ERROR: required command not found: azd`. PowerShell also
  reported `azd` unavailable, although `az` and `bash` are installed. No current
  Azure login, selected environment, deployment, model availability, quota,
  deployed URL, or remote health claim was verified.
- **Fragile Java-version evidence:** local tests and runtime passed on Java 17,
  while the documented Azure preflight/package path requires JDK 21 and the App
  Service runtime is Java 21. That packaging path was not exercised here.

### Unresolved human decisions

- Which smallest capability family to implement first: owner/pet lookup,
  visit-summary lookup, or veterinarian/specialty lookup.
- Which staff-facing UI/API surface and wording to expose.
- Whether the missing Azure readiness evidence must be repaired before shaping
  the first local vertical slice, or may remain an explicit residual gap until
  deployed verification.
