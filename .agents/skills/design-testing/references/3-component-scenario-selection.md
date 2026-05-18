# Component Scenario Selection

This reference defines minimal scenario sets for integration and unit tests.

## Integration Scenario Set

Select by observable behavior and keep set minimal.

### Simple Screen Component

Start from this list and keep only relevant cases:

1. initial success state;
2. primary user action;
3. failure or recovery path (`retry`/`refresh` when relevant);
4. parameter-change behavior (`query/filter/id/type/tab`);
5. side effect (`Output`, message, dialog, navigation effect).

Add intermediate-state scenario only when temporary state is user-significant.

### Router Component

Start from this list:

1. initial child screen;
2. forward navigation transition;
3. back/close transition when supported;
4. bubble-up output to parent;
5. branch transition for different child outputs.

### Integration Size Heuristics

- Simple screen: 3-5 tests.
- Rich screen: 4-7 tests.
- Router: 2-5 tests.

If draft is much larger, merge overlapping scenarios.

## Unit Scenario Set

For isolated local logic, minimum useful set:

1. happy path;
2. boundary behavior;
3. invalid input or error path (when applicable);
4. determinism/idempotence check (when meaningful).

### Unit Size Heuristics

- Pure calculator/mapper: 2-4 tests.
- Logic with validation matrix: 3-6 tests.

## When Not to Add a Separate Test

Avoid separate test when:

- it duplicates already covered transition with different constants;
- it asserts implementation detail, not behavior;
- intermediate state has no product significance.

## Intermediate-State Test Gate

Write rare intermediate-state integration tests only if at least one is true:

- user can notice temporary state;
- temporary state protects previous content from disappearing;
- temporary state prevents duplicate actions;
- area has known regression history.
