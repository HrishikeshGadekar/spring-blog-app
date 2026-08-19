# Submission Assets

Lightweight, judge-facing assets for Team Seek Deep's Living Ledger submission.

## Files

- `architecture-diagram.svg` — the nine microservices and how they actually interact end-to-end (who calls whom, what each service must never do).
- `demo-presentation.html` — a short, narrative-driven HTML deck (5 slides) focused on the problem and the idea, for presenting or printing to PDF.

## Recommended use

1. Open `demo-presentation.html` in a browser and present full-screen for the pitch/story.
2. Use `architecture-diagram.svg` as the technical backup slide/attachment showing the real service interaction map.
3. Drop either file directly into the submission form, or import into PowerPoint/Figma/draw.io for final tweaks.

## What each asset emphasizes

**`architecture-diagram.svg`**
- All nine services (Party, Contract, Vault, Allocation, Evidence, Release, Split-Pay, Transaction, Mock RTA Producer) and the frontend.
- Numbered end-to-end call flow (1 → 9) plus explicit cross-service dependencies (e.g. Release checks both Evidence and Allocation/Vault).
- The non-negotiables enforced *between* services (Vault knows nothing about contracts, Split-Pay/Transaction can't run before `RELEASE_READY`, AI never approves release directly).

**`demo-presentation.html`**
- Why escrow today is a black box, and why we split agree/hold/reserve/prove/decide/distribute/settle into independent stages.
- Why AI assists but never has release authority.
- Why the same pattern generalizes across marketplaces, trade finance, construction, and insurance.

## Notes

Both files are static with no external dependencies, so they can be opened directly or attached to the submission as-is.

