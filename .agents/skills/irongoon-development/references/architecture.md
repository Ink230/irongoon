# Irongoon architecture

## Contents

- Leaf-based programming
- Runtime flow
- Package ownership
- Singleton object graph
- Data-source architecture
- Randomness contract
- Extension recipes
- Architectural boundaries

## Leaf-based programming

Irongoon uses a shallow, directed tree of responsibilities. Broad classes branch; narrow classes are leaves.

```text
Severed Chains event bus
  -> Irongoon event adapter
    -> Randomizer strategy coordinator
      -> domain randomizer leaf
        -> parser/data/config leaf
```

The parent knows which child operation to invoke. The child knows how to perform it. A leaf must not route back through its parent, know about unrelated sibling features, or mutate engine state beyond the object/value handed to it.

This is not a rule that every class must be tiny. It is a rule that every class has one architectural altitude and that domain decisions move toward terminal owners instead of accumulating in roots.

## Runtime flow

`Irongoon` is the mod entry point and composition root. Its constructor registers the instance with `GameEngine.EVENTS`. Its annotated handlers:

- Translate Severed Chains events into Irongoon inputs
- Refresh configuration and table state at lifecycle boundaries
- Call one or more coordinator operations
- Apply returned values to engine event/state objects
- Respect event priority for live-data ingestion

`Randomizer` owns configured dispatch. Its `do*` methods select the active enum mode with switch expressions, call the matching leaf method, and combine results when applying character or monster state.

Domain leaves implement calculations such as character HP, monster stats, shop contents, battle party, or battle stage selection. Parsers turn table rows into domain values. They depend on configuration, table access, and shared numeric leaves but not on event classes unless the event itself is the narrow input boundary.

## Package ownership

| Package | Ownership |
| --- | --- |
| `lod.irongoon` | Mod lifecycle, event integration, registry event forwarding, application of leaf results |
| `config` | YAML loading and the process-wide mutable configuration snapshot |
| `data` | Closed policy modes, serialized values, table column identifiers, external dataset identifiers |
| `models` | Mutable or immutable domain/table carriers |
| `parse.external` | File-format parsing independent of game semantics |
| `parse.game` | Dataset row interpretation into Irongoon domain values |
| `parse.schema` | Dataset-wide and row-level validation |
| `services` | Stateful application services and canonical table/addition state |
| `services.data` | Dataset source selection, source implementations, source metadata, live SC ingestion |
| `services.randomizer` | Strategy coordinator plus terminal randomization algorithms |
| `registries` | Static mod registry declarations and registration helpers |

Do not create a generic utility package for behavior that has a domain owner.

## Singleton object graph

Stateful collaborators use eager process-wide singletons:

```java
public final class ExampleService {
    private static final ExampleService INSTANCE = new ExampleService();

    public static ExampleService getInstance() {
        return INSTANCE;
    }

    private ExampleService() {}
}
```

Use singleton ownership for:

- Configuration
- Table/addition services
- Parsers
- Data-source implementations and resolver
- Live-data adapters
- Strategy coordinators and randomizer leaves

Use static-only classes with private constructors for declaration-only registries or schema catalogs. Use ordinary models, records, enums, and engine-owned event objects as values rather than singletons.

Singleton services acquire other singleton leaves directly in fields. Do not add public constructors, service locators, containers, factories, or runtime setter injection solely for validation convenience.

## Data-source architecture

The data pipeline is layered deliberately:

```text
ExternalData
  -> DataTableSourceResolver policy
    -> CSVDataTableSource or SeveredChainsDataTableSource
      -> DataTableSchemas validation
        -> DataTables defensive canonical copy
          -> game parser leaves
```

Source policy is per logical dataset:

- An available CSV is authoritative when `csvDataOverrides` is true
- Otherwise use a supported Severed Chains source
- Otherwise use an available CSV as a compatibility source
- Missing required data fails initialization
- A selected but invalid source fails loudly; it must not silently fall through to another source

`LoadedDataTable` carries provenance, selection reason, and live-update capability. Preserve these observability fields when adding a source.

`DataTables` owns initialized canonical tables and loaded-source metadata. It clones loaded tables, clones returned tables, clones rows around updates, validates replacements, and throws before use when uninitialized.

`SeveredChainsLiveDataAdapter` is the event-to-table update leaf. Only apply live changes when the selected source allows them. Use listener priority to ingest upstream mod changes before Irongoon randomizes the event.

## Randomness contract

Seed behavior is user-visible policy:

- Campaign/fixed modes must reproduce values from `IrongoonConfig.seed`
- Add stable modifiers such as character ID, monster ID, shop/registry hash, level, encounter ID, or submap ID to separate domains
- Centralize reusable bounded, distribution, limit, and percentage calculations in `StatsRandomizer`
- Construct unseeded `Random` only for modes explicitly documented as random on every use or battle
- Do not replace stable deterministic formulas casually; changing constants or modifier composition is a compatibility change for existing seeds

## Extension recipes

### Add a randomization mode

1. Add the option to the owning `data` enum with its serialized value
2. Add one focused method to the owning randomizer leaf
3. Route the enum case in the corresponding `Randomizer.do*` method
4. Apply the returned value in `Irongoon` only if engine state needs adaptation
5. Verify deterministic and boundary behavior through compilation and targeted runtime checks at the narrowest public seam

### Add a logical dataset

1. Add an `ExternalData` identifier
2. Add a schema and validation rules
3. Add support to the appropriate source implementations
4. Extend resolver policy only when the dataset needs different source capabilities
5. Add a game parser if consumers need domain conversion
6. Verify source selection, invalid data, and defensive state behavior through relevant runtime or manual checks

### Add an engine event integration

1. Register an `@EventListener` method in `Irongoon`
2. Extract stable identifiers and adapt engine types there
3. Delegate selection/calculation to the existing coordinator and leaf
4. Apply the result to the event/state object
5. Use explicit priority when ordering against other mods or live ingestion matters

## Architectural boundaries

- Do not move algorithms into `Irongoon` to avoid creating a leaf
- Do not let `Randomizer` parse tables or implement algorithm loops
- Do not let parsers choose sources
- Do not let source implementations choose global source policy
- Do not expose canonical mutable tables directly
- Do not add cross-feature knowledge to a domain randomizer
- Do not change seeded formulas without treating save/seed reproducibility as an affected surface
- Do not simulate Severed Chains progression when no side-effect-free authoritative source exists; retain compatibility data and document the limitation
