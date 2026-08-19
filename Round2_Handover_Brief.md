# Round 2 Handover Brief — Team Seek Deep
## "Living Ledger": Programmable Contract-Driven Value Reservation & Settlement Platform

**Status:** Selected for the next round of Accelerate 3.0. Round 1 delivered a fully working, demo-safe hackathon prototype (all 9 backend services + React frontend, driven end-to-end). Round 2 goal: evolve this into a **viable, production-credible application** that impresses judges on depth, robustness, and real-world readiness — not just a happy-path demo.

> **If you are an AI agent picking this up:** read this whole document first, then read `Hackathon_Plan_Value_Reservation.md` and `Agent_Build_Brief.md` (same folder) for the original design rationale. This document tells you what already exists (do not re-scaffold it), what's fake/stubbed (and needs real implementation), and what's simply missing. Ask before violating any "Non-Negotiable" below — they were deliberate architectural decisions, not oversights.

---

## 1. What We Built in Round 1 (all of this exists and works today)

### 1.1 Architecture
Single Maven multi-module repo. 9 independently runnable Spring Boot microservices (Java 17) + a shared `common` module + a React frontend + an `app` module (convenience aggregator — see 1.5). One shared Postgres schema `hackathon`, bootstrapped via plain `schema.sql`/`data.sql` (no Flyway, no migrations tooling at all).

| Module | Port | State |
|---|---|---|
| `common` | - | Shared enums (`PartyType`, `MovementReason`, `ConditionStatus`, `EventType`, `AggregateType`), `EventEnvelope` DTO, `AiProvider` interface, `EvidenceEvaluationResult`/`EvidenceEvaluationRequest`, `ApiResponse` wrapper. **Built, but `EventEnvelope` is a DTO shape only — never persisted or published (see Gap 3.3).** |
| `party-service` | 8081 | Full CRUD, `XcroPartyClient` interface + `MockXcroPartyClient` (active) + `RestXcroPartyClient` (stub, throws `UnsupportedOperationException`) |
| `contract-service` | 8082 | Full CRUD, JSON-blob obligations/conditions storage, same Mock/Rest client pattern, `ContractApproval` entity |
| `vault-service` | 8083 | Hierarchical wallet ledger mirroring real `51241-scroe-vault` schema (`scv_v1_tree_master_tbl`, `scv_v1_vault_master_tbl`, `scv_v1_vault_bal_tbl`, `scv_v1_vault_movements_tbl`). Strategy-pattern `WalletMovementExecutor` (Apportion/Release/Transfer/Clawback) via `MovementExecutorFactory`. RTA ingestion endpoint. Tree/wallet-by-tree endpoints for UI visualization. |
| `allocation-service` | 8084 | The novel flexibility layer. `AllocationStrategy` enum (`RESERVE_ON_FUNDS_AVAILABLE`, `RESERVE_IMMEDIATELY`, `OVER_COLLATERALIZED`, `NO_UPFRONT_RESERVATION`) + implicit partial-funding support (repeatable `/reserve` calls). Calls `vault-service` and `contract-service` via `RestTemplate`. |
| `evidence-service` | 8085 | Upload + evaluate. `AiProvider` interface, `MockAiAdapter` (deterministic keyword/field matching, active by default), `SpringAiAdapter` (real Spring AI `ChatClient` integration, opt-in via `ai.provider=spring-ai`, OpenAI or Azure OpenAI backends — **this is further along than the original PRD's "mock-only" plan**), `AiFactoryAdapter` stub. |
| `release-service` | 8086 | Rule-led readiness engine. Calls `evidence-service` + `allocation-service`. Final authority — never lets AI decide directly. |
| `split-pay-service` | 8087 | Waterfall split with residual payee (`WaterfallSplitCalculator`), `RateType` (FIXED/PERCENTAGE), hard-blocks via `ReleaseNotReadyException` if `release-service` hasn't reported `RELEASE_READY`. |
| `transaction-service` | 8088 | `Pain001Generator` producing real ISO 20022 `pain.001` XML from split results. Same Mock/Rest XCRO client pattern for payment submission. |
| `mock-rta-producer` | 8089 | Simulates core-banking RTA credit events into `vault-service`. |
| `app` | 8080 | **Not in the original plan** — a convenience module that component-scans all 9 services into one single Spring Boot process/port, so the whole demo can run without Docker. Doesn't replace the other run modes. |

### 1.2 Cross-cutting patterns actually implemented (consistent across every module)
- Every module: `XWebCorsConfig` (permissive CORS, added purely for the browser frontend, no other backend logic touched).
- Every module: a `Global Exception Handler` (`@ControllerAdvice`)  mapping domain exceptions (`WalletNotFoundException`, `ContractNotFoundException`, `ReleaseNotReadyException`, etc.) to HTTP responses.
- Inter-service calls: plain `RestTemplate` clients, one `XClient` + matching `XDto` per dependency, wrapped in `RemoteApiResponse`. No circuit breakers, no retries, no timeouts configured beyond RestTemplate defaults.
- IDs: human-readable prefixed strings (`W-CONTRACT-...`, `MVT-...`, `ALLOC-RULE-...`), generated via `UUID.randomUUID()` substrings — not sequential, not vendor-style `dealRefId`/`partyRefId` composites as originally sketched.

### 1.3 Frontend — "Living Ledger" (React, Vite/CRA, plain CSS Modules, no UI kit)
- Fully wired to all 9 services directly (no BFF/gateway).
- Screens: Landing (scenario picker, only "Marketplace Escrow" is `[LIVE]`), Setup, Flow (pipeline + `PersonaDemoScreen`/`SplitFlowScreen` variants — the "split view" business-flow + live-vault-tree layout described in `Video_Script_Demo.md`), Vault Tree, Evidence, Settlement.
- Global `TimelineContext` — **session-only, client-side, in-memory** event log (not backed by any backend `events` table — see Gap 3.3).
- `JsonDrawer`, `PipelineNode`, `WalletTreeNode`, `RadialGauge`, `WaterfallBar`, `StatusPill`, `ScenarioCard` components exist per the UI design doc.
- A client-side-only fallback mock evaluator (`computeLocalMockVerdict`) exists in `evidenceApi.js` as an offline decouple option.

### 1.4 Documentation & demo assets already produced
- `README.md` — architecture, run instructions (3 options), API flow, non-negotiables.
- `sample-data/Hackathon_Plan_Value_Reservation.md` — original team plan and reference-repo rationale.
- `sample-data/Agent_Build_Brief.md` — the original from-scratch build brief (useful for "why" context, module-by-module design).
- `sample-data/UI_Design_Living_Ledger.md` — full UI/UX spec ("Living Ledger" theme, screens, components).
- `sample-data/Video_Script_Demo.md` — 4-minute demo script, already reflects the *actual* built UI flow (persona/split view, `</>` raw-response modals), so it's a good source of truth for what the current frontend really looks like end-to-end.
- `sample-data/Submission_Writeup.md` — the Round 1 submission form answers (tech stack, challenges, what's next).
- `sample-data/Team-Seek-Deep.postman_collection.json` — full API collection covering the demo flow.
- `sample-data/full_schema.sql` / `full_data.sql` — consolidated reference copies of every module's schema/seed data.

### 1.5 What is genuinely solid and worth keeping as-is
- The **allocation-service flexibility layer** — this is the most original piece of IP in the platform and is architecturally sound (funding-order-agnostic reservation).
- The **vault schema fidelity** decision — real column/enum names, documented extension points directly in `schema.sql` — makes future real-vault integration additive, not a rewrite.
- The **pluggable client pattern** (Mock/Rest for XCRO integrations, Mock/Spring-AI for evidence) — clean seam for swapping fakes for real integrations later.
- The **non-negotiables enforced as runtime exceptions** (e.g. `ReleaseNotReadyException`) rather than just code review conventions — genuinely good design discipline.

---

## 2. Honest Gap Analysis — What Is Actually Fake / Missing / Weak

Be blunt about this with judges and in your own planning. Round 1 optimized for "demo works end-to-end," not "production-grade." Below is everything a production-minded judge or engineer would flag.

### 2.1 No tests, anywhere
Zero unit tests, zero integration tests, zero contract tests across all 10 Java modules and the frontend. `pom.xml`s presumably still carry default Spring Boot test starters, but nothing exercises them. **This is the single biggest credibility gap for a "production-ready" pitch.**

### 2.2 No security whatsoever
- No Spring Security dependency in any module (confirmed: no `SecurityConfig`/`@EnableWebSecurity` anywhere in the repo).
- No authentication, no authorization, no API keys, no rate limiting, no input sanitization beyond basic bean validation (if even that — needs checking per-DTO).
- CORS is wide open (`WebCorsConfig` in every module — permissive by design for the demo, dangerous for anything beyond it).
- No secrets management — DB credentials are plaintext in `application.properties`/`docker-compose.yml` (`hackathon`/`hackathon`).
- No audit trail of *who* performed an action (no concept of an authenticated user/service identity at all).

### 2.3 Eventing was planned but never actually built
The original plan (`Agent_Build_Brief.md` §3) called for: "Spring `ApplicationEventPublisher` in-JVM + a simple `events` table (`common_event`) so the frontend can poll/replay a timeline." **This was never implemented.** `EventEnvelope` exists as a DTO shape in `common` but:
- No `ApplicationEventPublisher` usage anywhere in the codebase.
- No `common_event`/`events` table in any schema.sql.
- No event-replay/polling endpoint.
- The frontend's Timeline drawer is a **client-side-only, in-memory, session-scoped log** — refresh the page and history is gone; two browser tabs see different timelines; there is no server-side source of truth for "what happened and when."

This is a real functional gap versus the original design, not just a "nice to have" — fix this early in Round 2, it also solves several other problems below (audit trail, cross-service consistency, retry visibility).

### 2.4 No async/resilience patterns
- All inter-service calls are synchronous `RestTemplate` calls with no configured timeouts, no retries, no circuit breakers (no Resilience4j/Hystrix), no idempotency keys. A slow/down service cascades failures immediately.
- No outbox pattern / no guaranteed delivery — if `allocation-service` calls `vault-service` and the JVM crashes mid-call, there's no compensating mechanism (partially mitigated by `Clawback` movement type existing, but nothing invokes it automatically).
- No message broker (Kafka/RabbitMQ) despite being called out as explicit future work in the README.

### 2.5 Vendor/XCRO integration is 100% stubbed
`RestXcroPartyClient` / `RestXcroContractClient` / `RestXcroTransactionClient` all deliberately throw `UnsupportedOperationException`. This was an intentional Round 1 scope cut (documented, not hidden) — but for Round 2 "production ready" messaging, judges will ask "so what happens with a real vendor?" Decide: either build a working reference REST implementation against a documented contract (even if pointed at a mock server), or be very explicit in the pitch that this is deliberately abstracted vendor integration surface, not vaporware.

### 2.6 AI evidence verification is inconsistent in ambition
- `MockAiAdapter` = simple deterministic keyword matching.
- `SpringAiAdapter` = real LLM integration (OpenAI/Azure OpenAI) exists in code, but:
  - No prompt versioning/testing harness.
  - No guardrails beyond "expects strict JSON back."
  - No retry/fallback if the model call fails or returns malformed JSON.
  - No human-in-the-loop UI workflow for `requiresHumanReview=true` cases (the field exists in the DTO, but there's no reviewer screen/approval endpoint that acts on it).
  - Not clear if this has ever been run against a live key/tested end-to-end — treat as unverified until proven.

### 2.7 Data layer immaturity
- No Flyway/Liquibase — schema managed by plain `schema.sql`/`data.sql`, which is fine for a hackathon but not for a production app with iterative schema changes across a team.
- No optimistic locking / concurrency control on wallet balance updates (`WalletBalance` read-modify-write in `VaultService` — a race condition under concurrent load is very possible; no `@Version` field, no `SELECT ... FOR UPDATE`).
- No connection pooling/tuning, no read replicas, no partitioning strategy — fine for a demo DB, not addressed at all for scale.
- No archival/retention strategy for `WalletMovement`/ledger tables (which grow unbounded).

### 2.8 Observability is absent
- No structured logging strategy (just default Spring Boot console logging, presumably).
- No metrics (Micrometer/Prometheus), no tracing (no correlation/trace IDs propagated across the 9 services' `RestTemplate` calls), no health/readiness probes beyond Spring Boot Actuator defaults (if even added — verify).
- No centralized log aggregation story.

### 2.9 Deployment/DevOps gaps
- Each service has a `Dockerfile` and there's a root `docker-compose.yml`, but:
  - No CI/CD pipeline (no `.github/workflows`, no Azure Pipelines yaml found).
  - No Kubernetes manifests/Helm charts, despite "Containerize to Kubernetes with CI/CD" being explicitly named in `Submission_Writeup.md`'s "What's Next."
  - No environment separation (dev/sit/prod config profiles) — everything is `application.properties` with one set of defaults.
  - No image versioning/tagging strategy, no registry push step.

### 2.10 Frontend gaps
- Only the "Marketplace Escrow" scenario is `[LIVE]`; Trade Finance / Construction Milestone / Insurance Claim are static preview cards only — no real backend generality has actually been *proven* through the UI yet, only asserted architecturally.
- No frontend automated tests (no Jest/RTL/Cypress/Playwright found).
- No accessibility pass (contrast, keyboard nav, ARIA) — likely fails basic a11y checks given the "dark trading terminal" theme.
- No responsive/mobile layout consideration mentioned or verified.
- Timeline data loss on refresh (ties back to Gap 3.3).

### 2.11 Business-rule completeness gaps
- `AllocationStrategy` enum comment literally says "Pattern C support is implicit" — there's no explicit rule/state machine enforcing partial-funding semantics (e.g., what happens if partial funding never completes — is there a timeout/expiry/cancellation flow? No.).
- No contract cancellation/expiry/dispute flow anywhere (PRD mentions milestone/multi-party approval contract types, but only `ContractApproval` entity exists — no visible workflow enforcing "N approvals required before X").
- No refund/dispute-driven `Clawback` trigger from the UI or any service — the movement type exists in vault-service but nothing calls it end-to-end.
- Currency handling: `currency` is a free string field throughout; no FX conversion, no multi-currency contract support validated.
- No idempotency handling if e.g. the same RTA is replayed twice, or `/reserve` is called twice with the same request (currently would double-reserve).

---

## 3. Non-Negotiables — Do Not Break These in Round 2

These were deliberate design decisions from Round 1 and are already enforced in code in most cases. Keep enforcing them, and if new features challenge one of these, flag it rather than silently violating it:

1. Contract Service is not the orchestrator — it stores obligations/conditions only, never decides fund timing.
2. Allocation/reservation logic lives only in `allocation-service`.
3. `vault-service` stays business-rule-agnostic — pure ledger, no contract/milestone knowledge.
4. Contract creation does not always imply a contract wallet (lazy-created on first allocation rule).
5. Funds don't always arrive after contract creation — both orderings must keep working.
6. Funds aren't always available upfront — partial funding must keep working.
7. No `pain.001` generation at contract submission time — only after release + split.
8. No Split Pay calculation before `release-service` reports `RELEASE_READY` (`ReleaseNotReadyException`).
9. AI is not a simple summarizer — it verifies evidence against obligations.
10. AI never directly releases funds — `release-service` is rule-led and has final authority.
11. No physical money movement is modeled — only logical wallet movement.
12. Design must generalize beyond e-commerce (trade finance, construction milestones, insurance claims) — don't hardcode marketplace-only assumptions into shared modules.
13. Zero dependency on live vendor/production services required to run the demo (`xcro.provider=mock`, `ai.provider=mock` must remain the safe defaults).

---

## 4. Recommended Round 2 Priorities (suggested order, adjust based on judging criteria emphasis)

If the judging criteria for Round 2 reward "production readiness" and "real-world viability" specifically (as opposed to raw feature count), prioritize roughly in this order:

### P0 — Credibility foundations (do these first, they're cheap and judges will check for them)
1. **Add a real test suite.** At minimum: unit tests for `WaterfallSplitCalculator`, `MovementExecutorFactory`/each `WalletMovementExecutor`, `AllocationService` (all 4 strategies), `MockAiAdapter` matching logic, and `Pain001Generator` XML output. Add `@SpringBootTest` + Testcontainers (Postgres) integration tests for at least the critical path (contract → reserve → evidence → release → split → pain.001) in one module (e.g. via the `app` aggregator, since it already runs everything in one process).
2. **Fix the concurrency bug in `WalletBalance` updates** — add `@Version` optimistic locking or `SELECT ... FOR UPDATE`/pessimistic locking on the balance row during movement execution. This is a real bug, not just a nice-to-have.
3. **Add idempotency keys** to `/vault/rta`, `/allocations/reserve`, `/release/evaluate`, `/split-pay/calculate`, `/transactions/payment-instructions` so retried/duplicate calls don't double-process.
4. **Build the `events` table + `ApplicationEventPublisher` wiring** as originally planned — gives you: real audit trail, fixes the Timeline drawer's data-loss problem, and is a natural place to hang future async/Kafka migration.

### P1 — Real-world hardening
5. **Basic security layer**: at minimum, API-key or JWT-based service-to-service auth (even a simple shared-secret header checked by a filter is better than nothing), plus a locked-down CORS policy per environment. Add rate limiting on public-facing endpoints if any UI-facing gateway is introduced.
6. **Resilience**: add Resilience4j (timeout + retry + circuit breaker) around every `RestTemplate` client call between services. Add correlation-ID propagation (a simple `X-Correlation-Id` header forwarded through every client call) for traceability — pairs well with the events table.
7. **Observability**: add Spring Boot Actuator health/info endpoints everywhere (verify they're not already there), Micrometer + a simple Prometheus/Grafana docker-compose addition, structured JSON logging.
8. **Flyway migration** for at least one module as a proof-of-concept, ideally rolled out to all — replaces plain `schema.sql` so schema evolution is trackable/versioned like a real production system.

### P2 — Feature depth for the demo story
9. **Pick one additional scenario (Trade Finance or Construction Milestone) and make it `[LIVE]`**, not just a preview card — this is the single most convincing way to prove the "generalizes beyond e-commerce" claim from the PRD, since right now it's an assertion, not a demonstration.
10. **Wire one real `RestXcroXClient`** (even against a self-hosted stub/mock server standing in for the vendor) to prove the Mock→Real swap is truly config-only and not just a comment claiming it is.
11. **Contract lifecycle completeness**: cancellation, expiry, multi-party approval enforcement (actually gate release readiness on N-of-M approvals collected), and a UI-driven Clawback/dispute flow end-to-end.
12. **Human-in-the-loop evidence review screen** — a real UI + endpoint that acts on `requiresHumanReview=true` (currently the field exists but nothing consumes it operationally).

### P3 — Polish / stretch (only if time remains after P0-P2)
13. Kubernetes manifests + a basic CI/CD pipeline (build/test/docker-push on push to main).
14. Multi-currency/FX support in split-pay and transaction generation.
15. Frontend accessibility pass + at least smoke-level E2E tests (Playwright/Cypress) for the golden-path flow.
16. AI Factory Adapter refinement (prompt versioning, guardrails, structured retries) if `SpringAiAdapter` is going to be showcased live.

---

## 5. Practical Notes for Whoever/Whatever Continues This

- **Run it first before changing anything.** Three ways to run (see root `README.md`): single `app` module on 8080 (simplest), `docker-compose up --build` (9 containers), or per-module Maven (focused dev). Postman collection at `sample-data/Team-Seek-Deep.postman_collection.json` covers the whole flow and defaults to port 8080.
- **The `app` module is a convenience shim, not a "real" service** — don't confuse it with a future API gateway; it just component-scans everything into one JVM for demo simplicity. If you build a real gateway/BFF later, it's a separate concern.
- **Package convention**: every module lives under `com.seekdeep.hackathon.<module>` with consistent sub-packages (`client`, `config`, `controller`, `dto`, `entity`, `enums`, `exception`, `repository`, `service`). Follow this exactly for any new module/feature so the codebase stays uniform.
- **Every existing module already has a `Global Exception Handler` + custom exceptions** — extend those rather than introducing a new error-handling convention.
- **Before adding a message broker or async pattern**, note the README explicitly calls out "Replace intra-platform RestTemplate calls with a proper event bus (Kafka-compatible)" as planned future work — this aligns with Gap 3.3/3.4 above and is a good candidate for a single unifying Round 2 epic (event table → event publishing → eventual Kafka swap).
- **Do not touch `vault-service`'s schema fidelity intentionally** — the mirroring of the real `51241-scroe-vault` schema is a deliberate, documented differentiator (see `vault-service/src/main/resources/schema.sql` for the "future extension points" already documented there: `scv_v1_vault_limit_tbl`, `scv_v1_vault_awaiting_txn_tbl`, `scv_v1_vault_transaction_tbl`, `scv_v1_vault_account_link_tbl`, `scv_v1_client_master_tbl`). Pulling any of these back in is additive per the existing FK/enum design — a good, low-risk Round 2 feature if you want to show off "real banking-grade ledger depth."
- **`sample-data/full_schema.sql` / `full_data.sql`** are reference copies only (not auto-executed) — remember to keep them in sync manually if you change any module's real `schema.sql`/`data.sql`, or better, replace this manual-sync habit with a small script/Maven step in Round 2.

---

## 6. One-Paragraph Elevator Summary (for context reuse elsewhere)

Team Seek Deep built "Living Ledger" for Accelerate 3.0 Round 1: a 9-microservice Java/Spring Boot + React platform that decouples contract obligations, fund custody (a real-vault-schema-fidelity wallet ledger), flexible fund reservation (5 funding-order patterns), AI-assisted evidence verification (mock + real Spring AI), rule-led release readiness, waterfall payout splitting, and ISO 20022 `pain.001` generation — fully demoable offline via Docker Compose or a single-process `app` module, with a dark "trading terminal" React frontend and a complete Postman collection. It got selected for Round 2. The prototype is architecturally sound and its non-negotiables (AI never releases funds, Split blocked pre-`RELEASE_READY`, Vault is business-rule-agnostic) are enforced as real runtime exceptions — but it has **zero tests, zero security, no real eventing/audit trail (despite being planned), no resilience/observability, and only one of four pitched scenarios is actually wired live**. Round 2's job is to close those production-readiness gaps (prioritized above) without breaking the existing non-negotiables or re-architecting what already works.

