# Programmable Contract-Driven Value Reservation & Settlement Platform
## Team Plan — Accelerate 3.0 Hackathon

**Status:** Planning complete, ready to start coding
**Last updated:** 20 July 2026

---

## 1. What We're Building

A **programmable value-reservation and settlement platform** — not a hardcoded escrow app.

Core principle: contract creation, fund collection, fund reservation, release evaluation, split calculation, and payment instruction generation are **independently timed, loosely coupled modules**. The same platform should support e-commerce escrow, trade finance, construction milestones, insurance claims, etc.

The demo narrative is a **marketplace buyer/seller escrow flow**, but that's just the story used to explain a reusable platform — the underlying modules stay generic.

> One-line pitch: Funds are held once, logically reserved flexibly, verified against contractual obligations using AI-assisted evidence validation, and released only when configured conditions are satisfied.

Full original requirements are in `Hackathon_PRD_Value_Reservation_AI_Updated_Agent.md` in this same folder.

---

## 2. Key Decision: Fresh Code, Inspired by Existing Repos

We are **not** calling our real production services/DBs (orchestrator, XCRO/UPP vendor SDK) directly, and **not** taking a dependency on those repos. Hackathon demo must be fully self-contained, offline-safe, and simple to read/explain to judges by default (`xcro.provider=mock`).

One exception: **Party, Contract, and Transaction are vendor UPP/XCRO-owned in production** — for these three modules we built a pluggable `client` package (`XcroXClient` interface + `MockXcroXClient`, active by default, + `RestXcroXClient`, an unwired stub for future real SIT integration) so the design is honest about that split, while the demo itself stays 100% offline-safe. Each module still keeps a local read-cache table (`party_party`, `contract_contract`, `txn_pain001_message`) with `xcro_synced_at`/`xcro_status` columns — XCRO remains the system of record, our table is just a fast-read projection.

Instead, we **wrote fresh, lightweight, single-repo code**, using our real repos purely as **design references** — borrowing entity shapes, naming conventions, and structural patterns, while deliberately stripping out legacy cruft, unnecessary validation, rollback/BPM machinery, and anything not needed for the demo story. **Vault is the one deliberate exception to "simplify everything"**: since we already own that schema/DB and use it daily, `vault-service` mirrors the real `51241-scroe-vault` table names, column names, and enum value sets as closely as reasonably possible (see Section 2a), dropping only non-essential real complexity as documented future extension points.

### Reference repos and what we borrowed from each

| Repo | What we looked at | What we reused (pattern only, not code/DB) |
|---|---|---|
| `51241-scroe-vault` | `VaultMasterEntity`/`TreeMasterEntity` (self-referencing tree, parent/child, tree hierarchy), `VaultBalanceEntity` (ledger/theoretical/available/pool balances), `Movement` abstract class + `Apportion`/`Release`/`Transfer`/`Clawback` subclasses, `Action`/`VaultType`/`VaultStatus`/`TreeStatus`/`AssetType` enums | Real table/column names and enum value sets reproduced directly (see Section 2a) — the Movement **strategy pattern** for reserve/release/transfer/clawback operations, kept clean Java class names (`Wallet`/`WalletBalance`/`WalletMovement`/`VaultTree`) mapped via `@Column` to the real snake_case DB names |
| `51241-scroe-connect-orchestrator` (`orchestrator-core`, `payoutCalculator` block only) | `PayoutCalculatorInput/Output`, `SplitPlan`/`PayeeParty` (waterfall with `waterfallPriority`, `residual` flag), `RateType` (`FIXED`/`PERCENTAGE`) | Waterfall split-with-residual-payee design for our Split Pay Service. **We did not touch** the orchestration engine, block registry, event-handler, or scheduler modules — irrelevant to us. |
| `51241-scroe-connect-adapter-paas` | `party/service/*` (Create/Fetch/Update/Delete/VerifiedPartyService — service-per-operation CRUD shape), `ContractEntity` (JSON-blob contract storage keyed by `endToEndId`, with `status`) | Service-per-operation shape for Party Service; JSON-blob-based contract storage pattern for Contract Service |
| `connect_schema.sql` (full DB schema dump) | Confirmed live vs legacy table names (e.g. `scv_v1_vault_master_tbl` is live; `_v2` variant is dead code) | Background context only — we don't touch this DB at all |

**Non-negotiable:** No dependency on real vendor SDK (`com.scb.scroe.xcro.*`) — our own `XcroXClient`/DTOs are fresh code, not vendor imports. No live network calls during demo (`xcro.provider=mock` is the default and what we present). No dependency on the real production Postgres instance — `vault-service`'s schema fidelity is structural only (fresh tables in our own `hackathon` schema, not a connection to the real vault DB).

### 2a. Vault Schema Fidelity (the one place we mirror real structure closely)

`vault-service` uses 4 tables mirroring the real schema almost 1:1:

| Table | Mirrors | Notes |
|---|---|---|
| `scv_v1_tree_master_tbl` | `TreeMasterEntity` | New this round — added specifically so a future vault-tree visualization in the UI has real data to render against |
| `scv_v1_vault_master_tbl` | `VaultMasterEntity` | Structural/hierarchy metadata (`vault_tree_id`, `parent_vault_id`, `vault_type`, `status`, etc.) |
| `scv_v1_vault_bal_tbl` | `VaultBalanceEntity` | Split out as its own 1:1 table (real system does this too) — `ledger_bal`/`theoretical_bal`/`available_bal`/pool balances |
| `scv_v1_vault_movements_tbl` | `VaultMovementEntity` | `action` column uses the real `Action` enum values (`APPORTION`/`RELEASE`/`TRANSFER`/`CLAWBACK`) plus our own `RTA_CREDIT` extension |

Documented hackathon-only extensions (kept to a minimum, called out as comments in `schema.sql`): `owner_party_id`/`virtual_account_number` on vault_master (real system links these via a dropped `VaultAccountLinkEntity`), `reserved_bal` on vault_bal, `currency` on vault_movements, and the `RTA_CREDIT` action.

Deliberately dropped-for-now real tables, documented as **future extension points** directly in `schema.sql` (not just tribal knowledge): `scv_v1_vault_limit_tbl` (limit engine), `scv_v1_vault_awaiting_txn_tbl` (EOD awaiting-txn), `scv_v1_vault_transaction_tbl` (EOD ledger history), `scv_v1_vault_account_link_tbl` (account linking), `scv_v1_client_master_tbl` (multi-client onboarding). Because FK columns (`vault_tree_id`, `parent_vault_id`) and enum value sets already match the real system, adding any of these back later is additive, not a migration/rewrite.

---

## 3. Modules (Single Repo, Multi-Module Maven)

We chose a **multi-module Maven repo** (mirrors the orchestrator repo's own `orchestrator-core`/`orchestrator-api`/etc. pattern) instead of true separate microservices repos, since the hackathon requires one submittable repo. Each module is still a fully independent Spring Boot app (own `main`, own port, own `application.yml`) — so a post-hackathon split into real microservices is close to zero-cost.

```
/value-reservation-platform
├── pom.xml                     (parent aggregator)
├── docker-compose.yml
├── common/                     shared DTOs: event envelope, EvidenceEvaluationResult, shared enums
├── party-service/              Party CRUD (BUYER/SELLER/MARKETPLACE/DELIVERY_PARTNER/...)
├── contract-service/           Contract obligations, release conditions, milestones, split rule ref (JSON blob storage)
├── vault-service/              Hierarchical wallet ledger + RTA ingestion + movements
├── allocation-service/         Bridges funds ↔ contracts (the key flexibility layer — net new)
├── evidence-service/           Evidence upload + Mock AI Adapter (+ clean AiProvider interface stub for future AI Factory)
├── release-service/            Rule-led release readiness engine (net new)
├── split-pay-service/          Waterfall payee split calculation (modeled on payoutCalculator)
├── transaction-service/        ISO 20022 pain.001 generation (net new, modeled on clean block input/output shape)
├── mock-rta-producer/          Simulates core-banking RTA credit events into vault-service
├── frontend/                   React app — timeline UI, demo screens
└── sample-data/                seed data for local demo
```

### Module responsibilities & non-negotiables (from PRD)

| Module | Responsibility | Must NOT do |
|---|---|---|
| Party Service | Thin facade over vendor UPP/XCRO party API (`XcroPartyClient`) + local read-cache of party metadata + virtual account mapping | Business logic |
| Contract Service | Thin facade over vendor UPP/XCRO agreement API (`XcroContractClient`) + local cache of obligations, release conditions, milestones, split rule reference | Decide timing of funds/reservation (not the orchestrator) |
| Vault Service | Hierarchical wallet ledger mirroring the real `51241-scroe-vault` schema (available/reserved), simulated RTA ingestion | Know about contracts/business rules |
| Allocation Service | Decide which money satisfies which contract & when to reserve; supports contract-first/funds-later, funds-first/contract-later, partial funding, over-collateralization | — |
| Evidence Service | AI-assisted verification of uploaded evidence vs. contract obligations; Mock AI Adapter now, AI Factory Adapter interface stub for later | Be a pure document summarizer |
| Release Evaluation Engine | Rule-led decision: is `RELEASE_READY`? Consumes AI recommendation + funding status + approvals | Directly release funds based on AI alone; calculate splits; generate pain messages |
| Split Pay Service | Calculate payee distribution after release trigger | Decide *when* to pay |
| Transaction Service | Thin facade over vendor UPP/XCRO payment API (`XcroTransactionClient`) + generate ISO 20022 `pain.001` after release + split | Integrate with real payment rails |

Event-driven via a lightweight in-JVM mechanism (see Section 5) using events like `CONTRACT_CREATED`, `RTA_RECEIVED`, `FUNDS_ALLOCATED`, `CONDITION_SATISFIED`, `RELEASE_READY`, `SPLIT_CALCULATED`, `PAIN001_GENERATED`.

---

## 4. Tech Stack

- **Backend:** Java, Spring Boot, Postgres (same stack as our existing repos)
- **Frontend:** React (Vite or CRA) — final integration target for demoing multiple use cases
- **Packaging:** Docker Compose, one Postgres instance, single new `hackathon` schema shared by all modules (no per-module schema split — simpler for a hackathon). DB objects bootstrapped via plain `schema.sql`/`data.sql` (Spring's built-in SQL init) — no Flyway.
- **Eventing:** Spring `ApplicationEventPublisher` in-JVM + a simple `events` table (for UI timeline replay) — no Kafka, keeps things simple and demo-safe
- **AI:** Mock AI Adapter only for this round (deterministic responses); clean `AiProvider` interface so AI Factory Adapter can be plugged in later without touching Evidence Service internals

---

## 5. Non-Negotiables (from PRD, still apply)

1. Contract Service is not the orchestrator.
2. Allocation/reservation logic does not live inside Contract Service.
3. Vault stays unaware of business/contract rules — pure ledger.
4. Contract creation does not always imply a contract wallet.
5. Funds don't always arrive after contract creation (support both orderings).
6. Funds are not always available upfront (support partial funding).
7. No pain.001 generation at contract submission time.
8. No Split Pay before release conditions are satisfied.
9. AI is not a simple document summarizer — it verifies evidence against obligations.
10. AI never directly releases funds — Release Evaluation Engine is rule-led and the final authority.
11. No physical money movement during hold/reservation — logical wallet movement only.
12. Solution must generalize beyond e-commerce (trade finance, construction milestones, etc.)
13. No dependency on live vendor/production services for the hackathon demo to succeed.

---

## 6. Build Order (P0 → P2)

1. `common` module (event envelope, `EvidenceEvaluationResult`, shared enums)
2. Party Service + Contract Service (static/CRUD data, fast to stand up)
3. Vault Service (hierarchical wallets, RTA simulation, movement types)
4. Allocation Service (the novel flexibility layer — reserve funds into contract wallets, support all funding orderings)
5. Evidence Service with Mock AI Adapter (deterministic, demo-safe)
6. Release Evaluation Engine (rule-led, consumes evidence + funding status)
7. Split Pay Service (waterfall payee split)
8. Transaction Service (pain.001 generation)
9. Mock RTA Producer (simulated core-banking credit events)
10. Frontend timeline UI wiring the full flow end-to-end
11. *(stretch, only if time remains)* AI Factory Adapter behind the existing `AiProvider` interface

---

## 7. Demo Flow (Marketplace Story)

1. Buyer places order in marketplace UI.
2. Contract created (buyer, seller, marketplace, delivery partner, release condition).
3. RTA simulator sends credit alert for buyer's virtual account.
4. Vault credits buyer's available wallet.
5. Allocation Service reserves funds — moves money from buyer available wallet → contract wallet.
6. Delivery proof documents uploaded.
7. Evidence Service (Mock AI) validates delivery proof against contract/order details.
8. Release Evaluation Engine marks condition satisfied → `RELEASE_READY`.
9. Split Pay calculates seller/marketplace/delivery-partner/tax amounts.
10. Transaction Service generates `pain.001` XML.
11. UI shows end-to-end status timeline:
    `Order Created → Contract Created → Funds Received → Funds Reserved → Documents Uploaded → Evidence Verified → Release Ready → Split Calculated → pain.001 Generated`

---

## 8. Success Criteria (from PRD Section 16)

A user should be able to, end-to-end in the demo:
1. Create parties.
2. Create a marketplace contract.
3. Simulate RTA fund arrival.
4. See buyer wallet credited.
5. Reserve funds into contract wallet through allocation.
6. Upload delivery evidence.
7. See Mock AI verify condition satisfaction.
8. Trigger release evaluation.
9. Generate split payout.
10. Generate pain.001 XML.
11. Explain how the same APIs apply to trade finance, construction milestones, and other escrow scenarios.

---

## 9. Submission Requirements & Deliverables

Per the Accelerate 3.0 Hackathon submission guide:

1. **Source code repo** — Azure DevOps, accessible to judges, with a clear README.
2. **Tech stack summary** — max 50 words.
3. **Challenges faced** — max 100 words (focus on how we solved them).
4. **What's next** — max 100 words (future features, scalability, production plans, user testing — this is where the AI Factory Adapter, real vendor integration, etc. go).
5. **Repository URL** field in the submission form.
6. **Supporting video** — ≤ 4 minutes, ≤ 200MB, showcasing a short PPT + the actual product working end-to-end.

We should keep these four write-ups in mind while building so the code/demo naturally supports what we'll claim in the submission form (e.g., the "What's Next" section can honestly reference the AI Factory Adapter interface stub and real vendor/DB integration as the planned next step).

---

## 10. Open Items / Defaults Assumed (flag if anyone disagrees)

- Event mechanism: in-JVM `ApplicationEventPublisher` + `events` table — no Kafka.
- Each service is a separate Spring Boot app/port, sharing one Postgres instance and one shared `hackathon` schema (table names prefixed per module if needed to avoid collisions, e.g. `scv_v1_vault_master_tbl`, `party_party`, `contract_contract`).
- IDs mimic real system naming style (`dealRefId`+`partyRefId`, `endToEndId`) but freshly generated (UUID/sequence) — no real ID reuse.
- Frontend: React, free rein on styling unless someone wants to match `scroe-connect-client-ui` theming.
- XCRO/UPP integration for party/contract/transaction: `xcro.provider=mock` is the default and what we demo; `xcro.provider=rest` toggles to the unimplemented `RestXcroXClient` stubs (future work, not wired this round).

---

## 11. Reference Documents

- `Hackathon_PRD_Value_Reservation_AI_Updated_Agent.md` — full original PRD (this folder)
- `connect_schema.sql` (in `51241-scroe-connect-orchestrator/ui-docs/`) — full production DB schema, used only for reference/context

