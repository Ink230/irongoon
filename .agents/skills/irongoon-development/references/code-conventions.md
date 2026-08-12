# Irongoon code conventions

## Contents

- Scope and evidence
- Java structure
- Naming and types
- Control flow
- State and mutation
- Errors and logging
- Build and repository handling
- Review checklist

## Scope and evidence

Apply these conventions to new and touched code. Preserve nearby formatting when it does not conflict with a strict rule. Treat isolated legacy patterns as historical inconsistencies, not precedents.

## Java structure

- Use package root `lod.irongoon`
- Keep one top-level production type per file and match file/type names
- Order declarations as constants/singleton, collaborator fields, mutable state, constructor, public methods, private helpers
- Use four spaces and opening braces on the declaration line
- Use braces for multiline conditional and loop bodies
- Allow brace-free single-line guards such as `if (characterId < 0) return;`
- Keep methods narrow and extract named private helpers when a calculation has distinct phases
- Add comments only for non-obvious engine constraints, unsupported integration seams, reproducibility constants, or intentional compatibility behavior
- Do not add generated boilerplate, Lombok, a dependency-injection framework, or an abstraction with only speculative future use

## Naming and types

- Use PascalCase for types, UPPER_SNAKE_CASE for constants and enum members, and lowerCamelCase for fields, parameters, methods, and locals
- Name singleton fields `INSTANCE`
- Name strategy operations for behavior: `maintainStock`, `randomizeWithBounds`, `randomizeCampaign`, `randomizeBattle`
- Name coordinator entry points `do<Domain>` when extending the established `Randomizer` API
- Use `final` on parameters and locals whose reference/value is not reassigned; use `var` when the initializer makes the concrete type obvious
- Use `this.` for instance fields and instance helper calls when it clarifies ownership; never use it for static state
- Prefer primitive-specialized FastUtil collections when interacting with engine primitive collections
- Use records for small immutable transport metadata and enums for closed policy/identifier sets
- Implement `Data<T>` for enum options that expose a serialized scalar
- Preserve engine terminology and field names at the integration boundary even when they are unconventional

## Control flow

- Use switch expressions for exhaustive enum strategy selection
- Keep stock/passthrough behavior explicit through `maintainStock` or a `STOCK`/`MAINTAIN_STOCK` case
- Validate preconditions and return/throw early before the main path
- Split event flow into extract, delegate, and apply phases
- Keep source precedence explicit and ordered; do not hide it in reflection or generic scoring machinery
- Prefer streams for direct collection projection/filtering and loops for indexed mutation or multi-step calculations
- Do not catch exceptions unless adding domain context, translating a checked boundary, or providing an intentional recovery path

## State and mutation

- Treat `IrongoonConfig` as the shared current configuration snapshot
- Treat `DataTables` as the owner of canonical loaded table state
- Mutate Severed Chains event/game objects only at the event adapter boundary or in a leaf explicitly designed for that event
- Clone mutable arrays/lists when crossing canonical state boundaries
- Clear and rebuild service state during `initialize()` so reloads cannot retain stale entries
- Never add hidden global state outside an owning singleton or static declaration catalog

## Errors and logging

- Throw `IllegalStateException` when initialized runtime data is missing, invalid, inconsistent, or used before initialization
- Include the dataset/domain, source, row/index, actual value, and expected constraint when available
- Reject invalid explicit overrides instead of silently selecting a fallback
- Use the class logger from `LogManager.getFormatterLogger(Type.class)` for lifecycle, source-selection, warning, and diagnostic output
- Use formatter-style placeholders consistently with the formatter logger
- Do not add `System.out.println`, swallowed exceptions, or bare generic messages
- Log selected data source, selection reason, and live-update state when source behavior changes

## Build and repository handling

- Target the Java version and dependencies declared in `build.gradle`; do not downgrade language/API usage based on generic assumptions
- Use the Gradle wrapper, not a globally installed Gradle
- Automated tests are not required; do not add JUnit coverage or run the Gradle test task unless the user explicitly requests it
- Run `gradlew.bat compileJava` on Windows or `./gradlew compileJava` elsewhere for production changes
- Run `gradlew.bat assemble` on Windows or `./gradlew assemble` elsewhere when dependencies, registries, resources, patches, runtime integration, or packaging change
- Use targeted manual Severed Chains runtime checks for behavior compilation cannot verify
- Run `git diff --check` and inspect the final diff for preserved behavior, scope, and unrelated changes
- Preserve bundled CSV compatibility data and patch assets unless the task explicitly changes them
- Keep changes minimal and do not reformat unrelated legacy code
- Never commit crash logs, local saves, generated build output, IDE state, or the local Severed Chains snapshot JAR

## Review checklist

- Does the change live at the narrowest owning leaf?
- Does dependency flow remain root -> coordinator -> leaf -> data/config?
- Is every stateful collaborator obtained through `getInstance()`?
- Is seeded behavior deterministic and separated by a stable modifier?
- Are external inputs validated at their boundary with contextual errors?
- Are mutable canonical values copied across boundaries?
- Are enum modes routed exhaustively with explicit stock behavior?
- Are event priorities and other-mod interactions preserved?
- Were relevant manual runtime checks identified or completed for behavior compilation cannot verify?
- Was the final diff inspected for preserved behavior and unrelated changes?
- Are unrelated files and existing user changes untouched?
