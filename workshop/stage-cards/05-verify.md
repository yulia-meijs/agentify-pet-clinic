Status: Review ready

# Verify Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Trace every acceptance claim to fresh evidence and make residual gaps honest.

## Risk controlled

Green-by-proxy, substituting local checks for deployed claims, or treating an
agent summary as proof of completion.

## Minimum evidence

- Each acceptance claim linked to a focused test, smoke check, or observable
  demonstration at the correct seam.
- Independent challenge considered at a named revision, or its absence
  recorded as an evidence gap.
- Contradictory evidence, failed checks, and residual risks.
- The human's Accepted, Accepted with residual gap, or Not yet accepted
  judgment.

## Optional Copilot example

`/code-review` is one concrete, replaceable source of additional challenge;
use another method when it remains non-authoritative and evidence-focused.

## Exit question

**What can I honestly accept?**

## Evidence

### Verification target

- **Fixed point:** `origin/main` at `162d84a`.
- **Named implementation revision challenged:** `244c6b6`.
- **Change set:** four commits and 51 changed files at the time of independent
  challenge.
- **Governing acceptance source:** Work Contract revision 2 in
  `03-shape.md`, while earlier clauses remain in force unless explicitly
  superseded.

### Acceptance claims traced to fresh evidence

| Claim | Correct seam and fresh evidence | Result |
|---|---|---|
| Retrieval is read-only and exposes only authorized owner, pet, and visit fields | `OwnerPetClinicQueryTests` against H2 plus immutable purpose-built records; adapter uses `@Transactional(readOnly = true)` | Supported |
| Known, ambiguous, and absent owner outcomes are explicit and grounded | `ClinicAssistantTests` and `AssistantHttpIntegrationTests`; deployed Coleman and Davis observations use `find_owners_by_last_name` | Supported |
| Follow-up questions use a fresh grounded lookup | Full-context HTTP test and real Foundry Coleman-to-Samantha smoke request | Supported |
| Conversation is limited to six questions and is not persisted | MVC validation rejects a seventh turn; template inspection shows closure-local `turns`, no HTTP session, cache, database, `localStorage`, or `sessionStorage`; Reset empties the array | Supported locally; browser lifecycle is code-inspection evidence, not automated end-to-end evidence |
| Recorded visits can be read without becoming medical advice | H2 visit fixtures, focused treatment-history tests, and deployed recorded-medication/date follow-ups returned Samantha's two recorded visits | Supported |
| Writes, scheduling, diagnosis, and treatment advice remain unsupported | Direct assistant tests, full-context HTTP tests, and deployed rescheduling and medicine observations | Supported |
| Confirmed Concept B is the deployed presentation | MVC structural assertions plus live HTML/CSS checks for `conversation-panel`, `evidence-rail`, and `.assistant-shell`; human-visible page opened in the browser | Supported technically; final visual judgment remains human-owned |
| Existing behavior remains stable | All 90 tests other than the named inherited concurrency test passed; two were skipped | Supported with the contradiction below |
| Split-region Azure integration works | Readiness evidence, successful web deployment, `/actuator/health`=`UP`, and real Foundry tool-backed responses | Supported |

### Independent challenge at `244c6b6`

The optional two-axis code review ran Standards and Spec review independently.
It is additional automated challenge, not a substitute for Reciprocal Evidence
Review.

- **Standards finding:** client-supplied assistant answer text could influence
  resolution of one owner from an ambiguous result. The correction now resolves
  owners only from staff-authored questions and rejects a model-selected lookup
  prefix that is absent from those questions before querying PetClinic.
- **Standards judgement calls:** the assistant class has several responsibilities;
  form and JSON validation are duplicated; and the six-turn policy is repeated
  across Java, JavaScript, localized copy, and tests. These are maintainability
  observations rather than demonstrated behavior failures.
- **Spec findings:** date-only follow-ups were classified as owner summaries, and
  recorded medication wording was over-refused. Failing tests reproduced both;
  the corrected tests and deployed observations now return recorded visits while
  preserving advice refusal.
- **Challenged scope claim:** the reviewer classified split-region Azure as
  revision-2 scope creep. The dated Stage Card sequence contradicts that reading:
  split-region readiness passed and the human authorized execution before
  revision 2 prohibited any *further* topology change. No topology change was
  made during revision 2 or this Verify correction.

### Contradictory evidence, failures, and residual risks

- The inherited
  `PetClinicConcurrencyTests.testDuplicatePetNameRaceConditionIsBlocked` still
  reports zero successful HTTP requests while one database insertion occurs.
  This is not treated as a pass. The remaining 90 tests pass when that named test
  is excluded.
- Peer Reciprocal Evidence Review at the post-correction revision is **Missing**.
  The automated review above is subordinate and does not replace the Auditor.
- Browser refresh/reset persistence behavior is supported by direct code
  inspection and MVC boundaries, but no automated browser test currently proves
  it.
- Authentication, authorization, privacy controls, auditing, prompt-injection
  hardening, production observability, and production readiness remain outside
  the workshop slice.
- Azure resources remain running and billable until explicit cleanup. The
  recorded deadline is `2026-09-01T08:00:00Z`; it is not automatic.

### Verify decision boundary

The corrected slice is **Review ready**. Tests, deployment, smoke observations,
and automated review provide evidence but do not decide acceptance. The human
owns the Accepted, Accepted with residual gap, or Not yet accepted judgment.
