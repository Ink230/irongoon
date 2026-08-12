---
name: irongoon-development
description: Apply Irongoon's leaf-based Java architecture and repository conventions when reviewing, planning, implementing, refactoring, or testing code in the irongoon repository, especially event handlers, singleton services, randomizers, parsers, data sources, configuration, enum policies, and Severed Chains integrations.
---

# Irongoon Development

Preserve Irongoon's established dependency direction: engine events enter at the root, coordinators select behavior, and leaf services perform one bounded operation. Prefer a new or extended leaf over adding domain logic to an event handler or coordinator.

## Required workflow

1. Read [references/architecture.md](references/architecture.md) before changing production structure or behavior
2. Read [references/code-conventions.md](references/code-conventions.md) before writing or reviewing Java, tests, configuration, or data-source code
3. Inspect the nearest analogous implementation and its callers; repository evidence overrides generic Java fashion
4. Identify the layer that owns the behavior and keep dependencies pointing downward toward leaves
5. Make the smallest coherent change and preserve unrelated worktree changes
6. Add or update focused JUnit tests for changed policy, routing, validation, or state behavior
7. Run `gradlew.bat test` on Windows or `./gradlew test` elsewhere; run the relevant build when integration or packaging changes

## Non-negotiable rules

- Keep `Irongoon` an engine adapter and event boundary, not a domain-logic container
- Keep `Randomizer` a strategy router and state-application coordinator, not the implementation of randomization algorithms
- Put each terminal algorithm in the narrowest `*Randomizer`, parser, schema, source, or registry leaf that owns it
- Do not let a leaf call upward into `Randomizer` or `Irongoon`, and do not create circular dependencies between sibling leaves
- Use eager `getInstance()` singletons for stateful services, parsers, data-source services, and randomizers unless an external framework must construct the type
- Obtain singleton collaborators with `Type.getInstance()`; do not call their constructors and do not add dependency-injection infrastructure
- Keep singleton constructors private and the singleton reference `private static final ... INSTANCE`
- Keep configuration in `IrongoonConfig`; do not duplicate configuration state in leaves
- Preserve deterministic seeded behavior by incorporating `config.seed` and stable domain-specific modifiers; use unseeded randomness only for explicitly per-use modes
- Represent closed option sets and configuration policies as enums; implement `Data<T>` when the option exposes a serialized value
- Validate external tables at their source boundary and fail with contextual `IllegalStateException` messages; do not silently fall back from an invalid explicit override
- Return or store defensive copies at mutable table boundaries so callers cannot mutate canonical state accidentally
- Use Severed Chains events as the integration seam and honor listener priority when ingestion must occur before transformation
- Avoid speculative abstractions, broad refactors, new frameworks, or style-only cleanup outside the requested behavior

## Decision test for leaf placement

Place code in the first matching owner:

- Engine event registration or engine object adaptation: `Irongoon`
- Configured mode selection or applying several leaf results: `Randomizer`
- One randomization calculation: the corresponding `services/randomizer/*Randomizer`
- Shared numeric/random primitives: `StatsRandomizer`
- Table lifecycle and canonical in-memory table state: `DataTables`
- Source choice: `DataTableSourceResolver`
- Source-specific loading: a `DataTableSource` implementation
- Table shape and row validation: `DataTableSchemas`
- Table-to-domain conversion: `parse/game/*Parser`
- External file syntax parsing: `parse/external/*Parser`
- Closed settings or column identifiers: `data` enums
- Mod registry declarations: `registries`

If no owner fits, add the smallest new leaf in the matching package and expose only the operation its parent coordinator needs.

## Legacy inconsistencies

Do not copy an isolated inconsistency merely because it exists. Normalize new code to the rules above, including `INSTANCE` casing, `final` parameters, `this.` for instance state, conventional lower-camel local names, and no debug `System.out.println`. Do not perform unrelated cleanup without explicit scope.
