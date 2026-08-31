Status: Working

# Shape Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Define the smallest bounded move that can produce useful acceptance evidence.

## Risk controlled

Authorizing broad or opaque work whose scope, assumptions, agent authority,
public seams, and expected evidence cannot be inspected.

## Minimum evidence

- A Work Contract naming intent, scope, and constraints.
- Consequential assumptions and decisions reserved for the human.
- The Engineering Agent's authority and explicit boundaries.
- The highest useful public seam for testing or demonstration.
- Expected evidence for the Commitment and Acceptance Gates.

## Optional Copilot example

`/domain-modeling` is one concrete, replaceable way to sharpen terms and
boundaries; use another method when it makes the same contract legible.

## Exit question

**Is the next move safe and inspectable?**

## Evidence

## Work Contract

### Intent

Produce inspectable evidence that a staff user can ask for an owner by last name
and receive a read-only answer grounded only in retrieved PetClinic owner and pet
records.

### Authorized scope

- Add a dedicated `/assistant` page linked from the existing navigation.
- Accept one staff question per request. Conversation memory and reset are
  excluded.
- Support owner lookup by last name only.
- Trim the supplied last name and perform a case-insensitive prefix match.
- For one match, answer with the owner's full name and each pet's name and type.
- For multiple matches, present the matching candidates and ask the staff user
  to clarify. Candidate data remains limited to owner full name and pet name and
  type.
- For no match, state that no matching PetClinic record was found.
- Reject unsupported requests, including mutations and veterinary diagnosis or
  treatment advice, without claiming that work occurred.
- Show a concise activity trace containing only the read tool name and one
  outcome category: `matches found`, `no matches`, or `unsupported`.
- Use the preflight-proven Spring AI 2.0 integration with the Microsoft Foundry
  OpenAI-compatible endpoint. Tests use a deterministic mocked model; real
  Foundry is reserved for deployed smoke evidence.

### Explicitly out of scope

- Pet-name lookup, Visit summaries, and veterinarian-specialty questions.
- Owner address, city, telephone, and pet birth date.
- Writes, write tools, scheduling, RAG, Azure AI Search, another database,
  Foundry projects or Agent Service, and persistent transcript storage.
- Conversation memory, reset, identity guessing, and chain-of-thought display.
- Production authentication, authorization, privacy controls, auditing,
  prompt-injection hardening, production observability, and production
  readiness.

### Module interface and seam

Place a framework-agnostic read-only query seam between the assistant and
PetClinic persistence. Its interface has one owner-last-name lookup operation
and returns purpose-built immutable records containing only:

- owner full name;
- pet name; and
- pet type.

The module owns input normalization, case-insensitive prefix matching, empty
results, and mapping from JPA entities to purpose-built records. Spring AI,
controllers, prompts, and tests call this interface; they do not receive
repositories or JPA entities. This concentrates retrieval rules at one seam and
keeps the interface as the direct test surface.

The assistant-facing read tool adapts that interface for Spring AI. It has no
write operation and reports only retrieved records or an explicit absent result.

### Constraints and invariants

- Remain inside one Spring Boot process and the existing database.
- Answer only from records returned through the read-only query interface.
- Never infer an owner identity from an ambiguous match.
- Never claim mutation or provide veterinary diagnosis or treatment advice.
- Never expose fields beyond those authorized above.
- Keep all consequential product, risk, residual-risk, and acceptance decisions
  human-owned.
- Preserve the existing PetClinic routes and behavior.

### Engineering Agent authority

The Engineering Agent may:

- choose names and package placement consistent with repository conventions;
- add the minimum Spring AI dependencies and configuration for the selected
  integration;
- implement the read-only query module, Spring AI adapter, MVC page, navigation
  link, activity trace, and focused tests;
- make small internal design choices that do not alter this contract.

The Engineering Agent may not:

- add capabilities, fields, infrastructure, persistence, or production controls
  outside this contract;
- introduce any write path or expose repositories or JPA entities across the
  query seam;
- choose a different model integration or silently weaken absent, ambiguous,
  unsupported, mutation, or medical-advice behavior;
- decide that evidence is accepted or that the slice is complete.

### Public seams and expected evidence

- **Highest useful public application seam:** HTTP interaction with
  `/assistant`, including page rendering, question submission, grounded answer,
  and visible activity outcome.
- **Direct module seam:** tests call the read-only query interface using the H2
  fixture data and verify trimmed case-insensitive prefix matching, single,
  multiple, and absent results, and the restricted output fields.
- **Mock-model application evidence:** focused MVC or random-port tests verify
  one known lookup, ambiguous-match clarification, absent lookup, unsupported
  mutation, medical-advice refusal, tool registration, and activity outcomes
  without calling Foundry.
- **Regression evidence:** the inherited Maven test suite remains green.
- **Deployed evidence:** after Azure readiness is repaired, a real smoke test
  exercises the authorized known, ambiguous, and unsupported paths through the
  deployed `/assistant` page and records the URL and observed outputs without
  secrets or personal data.

### Assumptions and evidence gaps

- The selected Spring AI 2.0 artifacts are compatible with this Spring Boot
  baseline; dependency resolution and a focused startup test must prove this.
- Existing owner data is sufficient to exercise known, ambiguous, and absent
  prefix matches; tests must use controlled fixtures rather than assume the
  sample data remains unchanged.
- The mocked model can deterministically exercise tool selection and refusal
  behavior; this must be demonstrated rather than inferred.
- **Azure readiness repaired:** `azd` is installed and authenticated. On
  2026-08-31, the real readiness gate passed on the newly selected subscription
  with App Service in West Central US and the pinned `gpt-5.4-mini`
  Global Standard deployment in Sweden Central. The check verified providers,
  regional capacity, model availability and quota, and deployment authority.
  No deployment or Foundry runtime behavior is proven yet.

### Decisions reserved for the human

- At the Acceptance Gate, decide whether fresh local and deployed evidence
  supports acceptance, acceptance with a residual gap, or non-acceptance.
- Any expansion beyond this Work Contract requires a new human decision.

### Commitment Gate decision

**Proceed to Execute.** On 2026-08-31, the human attendee reopened the
Commitment Gate after the real split-region Azure readiness gate passed and
authorized execution under this Work Contract.

The earlier decision to escalate rather than accept missing Azure evidence is
resolved. It remains part of the evidence history; it did not authorize work
while readiness was blocked.

## Work Contract revision 2: bounded conversation and visit records

On 2026-08-31, the human attendee reopened the Commitment Gate and authorized
this revision. It supersedes the single-turn and no-visit limits above without
changing the read-only safety envelope.

### Revised intent and authorized scope

- Support a conversation of at most six staff questions in the current browser
  tab.
- Keep the transcript only in browser memory. Send the bounded prior turns with
  each request so the server remains stateless; refresh and Reset clear it.
- Use earlier turns only to resolve follow-up references. Every supported answer
  still requires a fresh call to the owner-last-name read tool.
- Expand the purpose-built `PetSummary` with immutable recorded-visit summaries
  containing only visit date and recorded description.
- Answer questions about owners, pets, and recorded visits when an owner last
  name can be resolved from the current or earlier turns.
- Preserve explicit ambiguous and absent outcomes, the concise activity trace,
  and refusal of writes, scheduling, diagnosis, and treatment advice.

### Revised seams, constraints, and authority

- Add a stateless JSON message seam at `/assistant/messages`; retain
  `/assistant` as the page and non-JavaScript single-question fallback.
- The framework-agnostic `PetClinicQuery` remains the only data boundary and
  still returns no repository or JPA entity.
- No server-side or persistent transcript, write tool, scheduling, independent
  pet-name search, owner contact fields, pet birth date, medical interpretation,
  or Azure topology change is authorized.
- The Engineering Agent may change only assistant records, query adapter,
  assistant/controller behavior, the assistant page, directly required
  localization, focused tests, and execution evidence.

### Assumptions and expected evidence

- Recorded visit descriptions are staff-visible record text, not medical advice.
- Six turns means six submitted user questions; a seventh requires Reset.
- Controlled query, assistant, MVC, and full-context HTTP tests must prove visit
  mapping, conversational reference resolution, fresh grounding, six-turn
  enforcement, malformed-input rejection, and continued scheduling/mutation and
  medical-advice refusals.
- Browser code inspection and syntax validation must show that transcript state
  is held only in the tab-local JavaScript closure and that refresh or Reset
  creates an empty conversation.

### Revised Commitment Gate decision

**Proceed with revision 2.** The human selected non-persistent browser-tab
conversation, owners/pets/recorded visits, a refresh-cleared lifecycle, and a
six-turn cap, then explicitly chose **Proceed with this Work Contract**.
