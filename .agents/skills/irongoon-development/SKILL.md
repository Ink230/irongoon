---
name: irongoon-development
description: Identify and work across the Irongoon project space, including the Irongoon Java mod repository, its GitHub Wiki, and the DragoonMods/LodTools website, while applying Irongoon's leaf-based Java architecture and repository conventions. Use for Irongoon code, configuration, documentation, config-reference, config-template, config-builder, tooltip, how-to, target-version, and Severed Chains integration work.
---

# Irongoon Development

Preserve Irongoon's established dependency direction: engine events enter at the root, coordinators select behavior, and leaf services perform one bounded operation. Prefer a new or extended leaf over adding domain logic to an event handler or coordinator.

## Project identity and authority

- **Irongoon**: The base Java mod loaded by Severed Chains (`sc`). Its hosted repository is `https://github.com/Ink230/irongoon`, and its usual local checkout is `D:\java\irongoon`. Treat this repository as authoritative for implemented mod behavior, supported configuration, defaults, versioning, and packaging
- **Irongoon Wiki**: The GitHub Wiki attached to the Irongoon repository at `https://github.com/Ink230/irongoon/wiki`. It contains the config reference, config templates, and other pages explaining the project. Treat it as the user-facing documentation surface and keep it synchronized with the implemented Irongoon configuration
- **DragoonMods / LodTools / Website**: The `https://dragoonmods.com/` website is implemented by the LodTools repository at `https://github.com/Ink230/LoD-Tools`, usually checked out at `C:\webprojects\lodtools\web`. Its Irongoon page provides the config builder, field tooltips, copy-paste configuration output, how-to guidance, and the Irongoon target version. The website may host other mods, so scope Irongoon work to its Irongoon-specific page and services unless the request explicitly includes shared or other-mod behavior

Route a request to the surface that owns it: mod/runtime and schema changes belong in Irongoon; config reference, templates, and explanatory pages belong in the Irongoon Wiki; builder UX, tooltips, generated copy-paste config, how-to content, and displayed target version belong in LodTools. When a change spans surfaces, derive documentation and website behavior from the current Irongoon implementation and validate that names, values, defaults, output order, and version claims remain aligned. Use the Severed Chains repository as authority for engine APIs and game behavior rather than copying assumptions from Irongoon or LodTools.

## Required workflow

1. Read [references/architecture.md](references/architecture.md) before changing production structure or behavior
2. Read [references/code-conventions.md](references/code-conventions.md) before writing or reviewing Java, configuration, or data-source code
3. Inspect the nearest analogous implementation and its callers; repository evidence overrides generic Java fashion
4. Identify the layer that owns the behavior and keep dependencies pointing downward toward leaves
5. Make the smallest coherent change and preserve unrelated worktree changes
6. Run `gradlew.bat compileJava` on Windows or `./gradlew compileJava` elsewhere for production changes
7. Run `gradlew.bat assemble` on Windows or `./gradlew assemble` elsewhere when integration or packaging changes
8. Use targeted manual Severed Chains runtime checks for behavior compilation cannot verify, then inspect the final diff for preserved behavior and unrelated changes

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
- Automated tests are not required; do not add JUnit coverage or run the Gradle test task unless the user explicitly requests it
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
