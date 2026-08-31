Status: Working

# Clarify Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Expose consequential ambiguity and keep product decisions with the human.

## Risk controlled

Manufacturing certainty, silently expanding scope, or treating implementation
details as authoritative product requirements.

## Minimum evidence

- Consequential knowns and unknowns.
- Clinic Stakeholder facts, preferences, and explicit uncertainty kept
  distinct.
- Assumptions, deferrals, narrowing decisions, or escalations with their
  consequences.
- Decisions that still require human authority before execution.

## Optional Copilot example

`/grilling` is one concrete, replaceable way to surface ambiguity; use another
method when it exposes the same decisions without answering for the human.

## Exit question

**What must a human decide before authority is granted?**

## Evidence

### Clinic Stakeholder facts

- The Clinic Assistant is staff-facing and read-only. It must never claim to
  change PetClinic data.
- Answers must come only from retrieved PetClinic records. The assistant must
  admit when records are absent or a request is unsupported.
- The assistant must not guess identity or provide veterinary diagnosis or
  treatment advice.
- The desired capability families are owner and pet lookup, Visit summaries,
  and veterinarian specialties.
- When multiple records match, the assistant must present candidates and ask a
  clarifying question.
- Staff need an accessible chat option with a concise, visible activity trace
  of tool calls and their outcomes.

Source: `docs/workshop/clinic-stakeholder-knowledge.md`, inspected on
2026-08-31.

### Clinic Stakeholder preferences

- Prefer the smallest evidence-producing vertical slice.
- Prefer comparable engineering evidence over identical implementations.

These are preferences, not permission to invent requirements or acceptance
criteria.

### Explicit unknowns

- The exact UI surface, navigation treatment, wording, visual design, and
  conversational tone are unresolved.
- The detailed behavior of owner and pet lookup beyond the fixed multiple-match
  rule is unresolved.
- The exact content and granularity of the visible activity trace are
  unresolved.
- Production authentication, authorization, privacy controls, auditing,
  prompt-injection hardening, production observability, scheduling, writes, and
  persistent conversations are outside the workshop slice and unresolved.

### Human-owned narrowing and deferrals

- **Narrowing:** prioritize owner and pet lookup as the first bounded capability
  family. Visit summaries and veterinarian-specialty questions remain desired
  capability families but are excluded from the first slice. This reduces the
  initial data and behavior surface; it does not reject those capabilities.
- **Deferral:** choose the exact staff-facing UI and navigation treatment during
  Shape. Consequence: implementation must not begin until the Work Contract
  names an observable public UI or API seam and corresponding acceptance
  evidence.
- **Residual-risk boundary:** retain production authentication, authorization,
  privacy, auditing, prompt-injection hardening, observability, scheduling,
  writes, and persistent conversations outside the workshop slice. Consequence:
  any resulting assistant is workshop evidence, not production-ready.

These decisions were made by the human attendee on 2026-08-31.

### Assumptions

- No additional product assumptions have been authorized at Clarify. In
  particular, this card does not choose lookup fields, response wording, session
  behavior, a model integration, or a UI implementation.

### Decisions still requiring human authority before execution

- Select the smallest owner/pet user journey and define how known, absent, and
  ambiguous records appear to staff.
- Select the staff-facing chat surface and visible activity-trace contract.
- Define the public application and test seams, agent authority, and
  risk-shaped acceptance evidence in the Shape Work Contract.
- Decide at the Commitment Gate whether the remaining ambiguity and explicit
  residual risks justify execution, further narrowing, or escalation.
