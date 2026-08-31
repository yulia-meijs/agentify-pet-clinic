Status: Working

# Execute Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Advance through small evidence-producing moves rather than one broad
delegation.

## Risk controlled

Losing inspectability, hiding failures, or continuing from agent confidence
instead of fresh observations.

## Minimum evidence

- Each bounded move's purpose, authorized scope, and expected evidence.
- The fresh result of each move, including failures and inaccessible inputs.
- The resulting decision to continue, narrow, correct, or escalate.
- Any Work Contract assumption or acceptance claim changed by the evidence.

## Optional Copilot example

`/tdd` is one concrete, replaceable way to make behavior changes observable;
use another method when it produces equally focused, risk-shaped evidence.

## Exit question

**What did this evidence change?**

## Evidence

### Bounded move 1: read-only owner and pet query seam

- **Purpose:** establish the framework-agnostic data boundary that constrains the
  assistant to the approved PetClinic fields.
- **Authorized scope:** a single owner-last-name query operation, immutable
  purpose-built result records, the internal JPA adapter, and direct
  H2-backed tests. No Spring AI, prompt, tool, controller, template, navigation,
  deployment, or write path.
- **Expected evidence:** a focused test first fails because the seam is absent,
  then passes for trimmed case-insensitive prefix matching, one result,
  ambiguous results, absent results, and records containing only owner full
  name plus pet name and type.
- **Fresh result:** the focused test first failed at compilation because
  `PetClinicQuery`, its records, and its adapter did not exist. After the
  implementation, all three H2-backed query tests passed. The generated SQL
  showed an upper-cased prefix comparison; the public records expose only the
  contracted fields.
- **Decision:** continue. This evidence established the constrained retrieval
  seam without changing the Work Contract.

### Bounded move 2: Spring AI read tool and safety behavior

- **Purpose:** connect the bounded query seam to Spring AI while keeping answers
  deterministic and grounded in the tool result.
- **Authorized scope:** Spring AI 2.0 dependencies, Microsoft Foundry
  OpenAI-compatible endpoint configuration, passwordless Azure identity, one
  read tool, deterministic answer formatting, and direct mocked-model tests. No
  MVC, deployment, memory, write tool, or additional capability.
- **Expected evidence:** dependency resolution and compilation on Spring Boot
  4.1, plus a deterministic mocked model selecting the registered read tool for
  known, ambiguous, and absent data. Mutation and medical-advice requests must
  call neither model nor query and must report `unsupported`.
- **Fresh result:** Spring AI `2.0.0` resolved and compiled with the Spring Boot
  `4.1.0` baseline. The first test failed because the assistant types were
  absent. During green work, two additional failures exposed that the mock
  needed tool-capable options and a tool-call finish reason; after correcting
  the mock to exercise Spring AI's real tool loop, all three assistant tests
  passed. The application ignores model-authored answer text and formats only
  the captured tool result. Explicit mutation and medical-advice tests proved
  that neither the model nor query was called. Non-chat Spring AI model
  auto-configurations are explicitly disabled. The deployed App Service enables
  only chat and uses managed identity against
  `${AZURE_OPENAI_ENDPOINT}/openai/v1`.
- **Decision:** correct, then continue. The failures sharpened the mocked-model
  evidence and revealed an otherwise hidden startup risk; neither correction
  expanded scope.

### Bounded move 3: `/assistant` HTTP seam

- **Purpose:** expose the contracted single-turn interaction and concise
  activity trace at the highest useful application seam.
- **Authorized scope:** one GET/POST controller, one Thymeleaf page, navigation,
  required localization keys, and focused MVC plus full-context mocked-model
  HTTP tests. No JavaScript, transcript persistence, or additional route.
- **Expected evidence:** the page renders; one submitted question shows the
  grounded answer and activity; blank input is rejected; known, ambiguous,
  absent, mutation, and medical-advice paths work through HTTP without a real
  Foundry call.
- **Fresh result:** the controller test first failed at compilation because the
  controller was absent, then all three MVC tests passed. The first full-context
  HTTP run failed because conditional bean ordering selected the explicit
  unconfigured-model response despite the deterministic test model. Replacing
  that fragile ordering with an `ObjectProvider` selection corrected the seam.
  All three full-context tests then passed against real H2 data and the
  deterministic tool-calling model. The repository localization check exposed
  missing template keys; adding the synchronized keys and localizing the
  descriptive text made that check pass.
- **Decision:** correct, then stop local feature execution. The HTTP evidence
  now covers the authorized behavior; deployment remains a separate,
  human-authorized billable move.

### Regression and infrastructure evidence

- The complete Maven suite passed except
  `PetClinicConcurrencyTests.testDuplicatePetNameRaceConditionIsBlocked`.
  Rerunning that inherited test in isolation reproduced the failure: both
  concurrent requests were rejected (`0` successful, `2` failed) while its
  assertion expects exactly one successful request. The final pet count was
  still two, showing one database insertion despite the HTTP classification.
  This is contradictory pre-existing regression evidence outside the assistant
  slice; it is not hidden or treated as a pass. The remaining suite passed when
  this named test was excluded.
- Azure preflight fixture tests passed in WSL. The infrastructure contract
  passed under Windows Git Bash with an explicit Python executable shim; the
  shim was removed afterward. The contract now verifies the App Service setting
  that enables only the OpenAI chat model.
- No Azure resources were deployed and no real Foundry request was made.

### Current execution boundary

Local implementation is **Review ready, not accepted**. The Work Contract is
unchanged.

That statement records the revision-1 boundary. The human subsequently reopened
the Commitment Gate and authorized Work Contract revision 2 in Card 3.

### Bounded move 4: deployed smoke evidence

- **Purpose:** test the preflight-proven integration against the real Foundry
  model through the deployed `/assistant` page.
- **Human authorization:** on 2026-08-31, the attendee explicitly selected
  **Deploy now** after being told that App Service and Foundry resources are
  billable and remain running until cleanup.
- **Authorized scope:** run the repository Azure preflight with the committed
  topology, then exercise only the known, ambiguous, and unsupported HTTP
  paths. Do not add resources or capabilities.
- **Cleanup deadline:** `2026-09-01T08:00:00Z`, selected by the attendee. This
  is evidence metadata, not automatic cleanup.
- **Expected evidence:** preflight records successful provisioning, topology,
  managed identity, role assignment, model deployment, app settings, and
  application health; smoke observations show grounded known and ambiguous
  answers plus an unsupported refusal without exposing secrets.
- **Result:** Working; fresh deployment and smoke observations will be recorded
  below.

### Bounded move 5: browser-tab conversation and recorded visits

- **Purpose:** support useful follow-up questions about owners, pets, and
  recorded visits without introducing server-side or persistent conversation
  storage.
- **Human authorization:** on 2026-08-31, the attendee selected browser-tab-only
  conversation, owners/pets/recorded visits, refresh/reset clearing, and a
  six-user-turn cap, then passed the revised Commitment Gate.
- **Authorized scope:** immutable visit records at the existing read-only query
  seam, bounded conversation input, a stateless `/assistant/messages` endpoint,
  tab-local UI state and Reset, directly required localization, and focused
  tests. No writes, scheduling, medical advice, persistent transcript, new data
  source, or Azure topology change.
- **Expected evidence:** direct query evidence exposes only visit date and
  recorded description; a follow-up such as “What visits has Samantha had?”
  performs a fresh owner lookup and returns fixture-backed visit records; a
  seventh turn and malformed history are rejected; scheduling and medical
  requests remain unsupported; refresh/reset behavior has no persistence
  mechanism.
- **Fresh result:** the red test run failed at compilation because the assistant
  accepted no conversation history. After implementation, focused query,
  assistant, MVC, and full-context HTTP tests passed. The HTTP follow-up resolved
  Samantha from an earlier Coleman turn, freshly queried H2, and returned the
  recorded 2013-01-01 and 2013-01-04 visits. Boundary tests cover the six-turn
  cap, oversized and malformed input, rescheduling refusal, medical-advice
  refusal, and treatment-history wording as a supported record lookup.
- **Challenge and correction:** automated review found that “treatment history”
  was over-refused, several rescheduling verbs bypassed explicit refusal
  wording, and the non-JavaScript form lacked matching length validation. The
  filters and validation were narrowed, expanded, and pinned with regression
  tests. This automated challenge is subordinate to the required human and peer
  review.
- **Regression evidence:** the full suite initially reported the already-known
  inherited concurrency contradiction plus two new hardcoded assistant labels.
  After localizing those labels, all 90 tests other than the named concurrency
  test passed, with two skipped. The unfiltered concurrency test still reports
  zero successful HTTP requests while one insertion occurs; this remains
  contradictory evidence outside this move.
- **Persistence observation:** the transcript is a JavaScript closure-local
  array. The request carries prior turns to a stateless controller; no HTTP
  session, cache, database, local storage, or session storage was added. Reset
  empties the array, and refresh constructs a new empty array. The extracted
  browser script passed `node --check`.
- **Decision boundary:** implementation is **Review ready, not accepted**. Fresh
  real-Foundry behavior for multi-turn visit queries has not been observed, and
  only the human may decide the Acceptance Gate.

### Acceptance Gate decision for Work Contract revision 2

On 2026-08-31, the human attendee selected **Accepted with residual gaps:
Foundry smoke + inherited concurrency**. This accepts the local workshop slice
while explicitly retaining two gaps: no real Foundry multi-turn visit smoke
observation has been recorded, and the inherited concurrency test still
produces contradictory HTTP-versus-database evidence.

### Deployed evidence for Work Contract revision 2

- **Human authorization:** on 2026-08-31, the attendee explicitly instructed
  **deploy**. The agent deployed only the `web` service to the existing billable
  Azure environment; it did not provision or alter topology.
- **Deployment result:** `azd deploy web --no-prompt` packaged and published the
  accepted local revision successfully. The deployed `/actuator/health` reported
  `UP`, and `/assistant` returned HTTP 200.
- **Real Foundry conversation:** the first deployed turn, “Find owner Coleman,”
  returned Jean Coleman with Max and Samantha and reported
  `find_owners_by_last_name - matches found`. The second turn, “What recorded
  visits has Samantha had?”, returned the two fixture-backed visits:
  `2013-01-01 - rabies shot` and `2013-01-04 - spayed`, again with the grounded
  tool activity.
- **Deployed safety observations:** a rescheduling request returned the explicit
  read-only/no-change refusal, and a medicine request returned the veterinary
  diagnosis/treatment-advice refusal.
- **Evidence change:** the real-Foundry smoke gap named at the Acceptance Gate is
  now closed by a fresh deployed observation. The inherited concurrency
  contradiction remains. This evidence update does not independently reopen or
  decide the human-owned Acceptance Gate.

### Concept B presentation correction

- **Reported gap:** after deployment, the attendee observed that the accepted
  Concept B styling was missing from `/assistant`.
- **Reproduction:** a live HTML check failed because the deployed page contained
  neither `conversation-panel` nor `evidence-rail`. The same check failed against
  the local production template while the prototype contained both.
- **Root cause:** Concept B had remained a throwaway prototype; only its behavior,
  not its structure or styling, had been ported to the production Thymeleaf page
  and PetClinic SCSS pipeline. Azure was serving the current functional package,
  so stale deployment was ruled out.
- **Correction:** the production page now uses the confirmed conversation panel,
  pet-friendly header, message bubbles, composer, six-turn count, examples, and
  supportive evidence rail. Responsive PetClinic-native styles use the existing
  brown, green, and warm-neutral design language. The prototype now demonstrates
  a two-turn Coleman-to-Samantha visit lookup and the six-turn browser-tab
  contract.
- **Regression evidence:** the MVC page test now requires both Concept B
  structural markers. Focused MVC and localization tests passed, the generated
  CSS contains `.assistant-shell`, prototype JavaScript syntax passed, and all 90
  tests excluding the documented inherited concurrency test passed.
- **Deployment observations:** the first retry was blocked because the temporary
  local visual-check process held the JAR open; stopping that specific process
  removed the Windows file lock. A later Azure client wait timed out before
  creating a deployment record and did not update the live page. Retrying with
  the documented extended timeout completed successfully.
- **Live result:** the original deployed-HTML reproduction now reports PASS for
  both Concept B structure and served CSS. A fresh real-Foundry follow-up still
  returned Samantha's two recorded visits with the read-tool activity. The
  deployed page and updated prototype were opened for human visual review.
