Status: Review ready

# Learn Stage Card

Use the [Evidence Lenses](../../docs/workshop/evidence-lenses.md) while working.
When this card is Review ready, follow
[Reciprocal Evidence Review](../../docs/workshop/reciprocal-evidence-review.md).

## Purpose

Retain only learning that can improve future work beyond this solution.

## Risk controlled

Keeping generic slogans, transient session detail, or untested conclusions
whose maintenance cost exceeds their future value.

## Minimum evidence

- One Agentic Engineering Principle that improved the work.
- One risk, failure mode, or weak assumption exposed by the evidence.
- One adaptation that could transfer to another project, tool, or workflow.
- The human's Learning Gate decision about what is durable enough to retain.

## Optional Copilot example

`/domain-modeling` is one concrete, replaceable way to preserve durable shared
language; use another method when it captures the same transferable learning.

## Exit question

**What transfers beyond this solution?**

## Evidence

### Candidate transferable learning

#### Principle that improved the work

**Match every claim to evidence at the seam where the claim must be true.**
Focused Java tests established query and refusal behavior, but they could not
prove the accepted Concept B presentation was deployed. A direct live HTML/CSS
check exposed that the design still existed only in the prototype. The same
principle required a real Foundry request rather than treating a mocked model as
deployed integration evidence.

#### Risk and weak assumption exposed

**Success at a nearby seam can hide both product and trust failures.** The first
deployment had working multi-turn behavior but not the human-selected
presentation. Separately, a stateless transcript treated client-supplied
assistant answer text as identity-resolution evidence; deterministic output
formatting alone did not make that input trustworthy. Independent challenge at
`244c6b6` exposed the identity path.

#### Adaptation that can transfer

For a stateless tool-using conversation:

1. keep display history untrusted and separate from user-authored identity
   anchors;
2. validate model-selected tool arguments against those anchors before querying;
3. format the answer only from fresh tool results; and
4. verify behavior, presentation, and external integration with distinct checks
   at their real seams.

This adaptation applies beyond PetClinic to any browser-carried conversation
that resolves entities before invoking a read tool.

### Learning decision boundary

These are candidates supported by this workshop evidence. The human Learning
Gate decides whether all, some, or none are durable enough to retain.

### Learning Gate decision

On 2026-08-31, the human selected **Retain all three together**:

- correct-seam verification;
- explicit trust validation for model-selected tool arguments; and
- explicit Work Contract revision when scope changes.

They are retained as one transferable pattern: revise authority deliberately,
keep conversational evidence and trusted identity anchors distinct, and prove
each claim at the seam where it must hold.
