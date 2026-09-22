# Pump Coaching Service --- AGENTS.md

## Purpose

This repository contains the Pump Coaching Service.

Act as a Principal/Staff Engineer, Software Architect, Backend Engineer,
and Engineering Mentor when working in this repository.

The goal is not only to make changes that work. Changes should preserve
the Coaching Service's domain boundaries, API contracts, data integrity,
authorization rules, security, reliability, consistency, and
maintainability.

Inspect the existing implementation before making assumptions or
introducing new patterns.

------------------------------------------------------------------------

# Repository Scope

This repository is responsible only for Pump coaching functionality and
coaching-owned data.

The Coaching Service owns:

-   Coach-client relationships
-   Client enrollment and coaching relationship state
-   Coaching profiles and client coaching data
-   Coaching-specific client information
-   Training blocks and training-plan data where implemented
-   Coaching directives and plan-related data where implemented
-   Coaching-specific progress data where implemented
-   Coaching business rules
-   Coaching-owned PostgreSQL persistence

The Coaching Service does not own:

-   User credentials
-   Authentication identity
-   JWT issuance
-   Authentication-related account verification
-   Auth-owned roles or authentication data
-   Social profiles
-   Posts
-   Likes
-   Comments
-   Replies
-   Follows
-   Other Auth or Social domain data

Do not move responsibilities into Coaching merely because coaching
functionality references a user.

A user identifier may be stored or referenced by Coaching without
transferring ownership of authentication identity or social-domain data
to the Coaching Service.

------------------------------------------------------------------------

# Technology Context

The Coaching Service currently uses:

-   Java 17
-   Spring Boot
-   Spring Data JPA
-   PostgreSQL
-   Maven
-   HTTP-based service-to-service communication where applicable
-   Spring `RestClient` where applicable

Use the actual repository configuration and implementation as the source
of truth for exact versions, libraries, profiles, and runtime behavior.

Do not introduce a new framework, library, persistence technology,
communication mechanism, or architectural pattern unless the requirement
justifies it.

------------------------------------------------------------------------

# Service Architecture

Preserve the existing Coaching Service architecture and package
conventions.

The high-level responsibility flow is conceptually:

``` text
Controller
    ↓
Service
    ↓
Repository / Data Access
    ↓
PostgreSQL
```

External service communication should remain behind the established
service/client boundary rather than leaking into unrelated layers.

Before adding or changing functionality:

1.  Inspect the nearest equivalent implementation.
2.  Trace the request through the relevant application layers.
3.  Identify existing abstractions that can be reused.
4.  Determine the minimum set of components that need to change.
5.  Preserve existing dependency direction and responsibilities.
6.  Avoid introducing new layers merely for architectural symmetry.

Do not assume a theoretically cleaner structure is automatically better
than the established implementation.

Existing code takes precedence where it remains appropriate.

------------------------------------------------------------------------

# Coaching Domain Boundary

The Coaching Service is the authoritative backend boundary for
coaching-owned data and coaching relationships.

A typical coaching request is conceptually:

``` text
Client
  ↓
Coaching API
  ↓
Authentication / Request Context
  ↓
Coaching Business Logic
  ↓
Coaching-owned PostgreSQL
  ↓
Coaching Response
  ↓
Client
```

When Coaching requires information owned by another Pump service, use
the established explicit service boundary.

Do not directly access another service's database.

Do not move authentication identity, credentials, social data, or social
business ownership into Coaching for implementation convenience.

------------------------------------------------------------------------

# Coach-Client Relationships

Coach-client relationships are Coaching-owned resources.

When modifying coach-client relationship behavior, inspect and consider:

-   Coach identity
-   Client identity
-   Relationship uniqueness
-   Relationship status
-   Enrollment behavior
-   Activation or deactivation behavior where implemented
-   Authorization
-   Duplicate requests
-   Existing database constraints
-   Effects on coaching-owned resources
-   API compatibility

Do not infer relationship state solely from client-side UI behavior.

Do not allow duplicate active relationships or other relationship states
unless the current domain model explicitly permits them.

Use the existing implementation and confirmed requirements as the source
of truth for exact relationship lifecycle and status semantics.

------------------------------------------------------------------------

# Client Coaching Profiles

Client coaching profiles belong to the Coaching domain.

This may include coaching-relevant information such as:

-   Gender where implemented
-   Birth date where implemented
-   Height
-   Current weight
-   Goal weight
-   Activity level
-   Fitness goal
-   Other coaching-specific profile information confirmed by the
    implementation

Do not treat coaching profile data as authentication identity.

The Auth Service remains authoritative for authentication identity and
credentials.

When modifying coaching profiles:

-   Validate fields server-side.
-   Preserve ownership and authorization.
-   Preserve existing nullability and optional-field behavior.
-   Consider database constraints.
-   Consider compatibility with existing consumers.
-   Do not invent profile fields or health/fitness semantics not
    established by the implementation or requirement.

Use the actual entity, DTO, validation, and persistence model as the
source of truth for exact fields and constraints.

------------------------------------------------------------------------

# Training Blocks and Plans

Training blocks, training plans, and related coaching-plan data belong
to Coaching where implemented.

When modifying training-plan behavior, inspect and consider:

-   Coach-client relationship
-   Ownership
-   Authorization
-   Plan lifecycle
-   Start/end or week structure where implemented
-   Training split
-   Exercise-related data where implemented
-   Nutrition/macronutrient targets where implemented
-   Step targets where implemented
-   Notes or directives where implemented
-   Current/active plan semantics
-   Historical plan behavior
-   Database constraints
-   API compatibility

Do not invent plan lifecycle rules, active-plan semantics, exercise
modeling, nutrition calculations, or progression logic that cannot be
confirmed from the implementation or requirement.

The backend remains authoritative for persisted coaching-plan state.

------------------------------------------------------------------------

# Coaching Progress Data

Progress data belongs to Coaching when it exists specifically to support
the coaching relationship or coaching plan.

When modifying progress behavior, inspect and consider:

-   Client ownership
-   Coach access
-   Date/time semantics
-   Measurement units
-   Historical records
-   Ordering
-   Duplicate entries
-   Update/delete behavior
-   Relationship to an active or historical training plan where
    applicable
-   Authorization
-   API compatibility

Do not infer progress calculations or analytics from UI mockups alone.

Do not introduce derived health, body-composition, calorie, or
performance calculations unless explicitly required and supported by a
confirmed backend contract.

Use persisted authoritative values rather than duplicating independently
mutable state where possible.

------------------------------------------------------------------------

# Authentication and Identity

The Coaching Service consumes authentication identity; it does not own
it.

For protected Coaching operations:

-   Authenticate requests using the established Pump mechanism.
-   Use trusted server-side identity rather than trusting arbitrary user
    IDs from the client.
-   Preserve the existing token-validation or Auth-validation boundary.
-   Do not issue authentication tokens.
-   Do not store user credentials.
-   Do not reproduce Auth credential logic.
-   Do not weaken authentication to make a Coaching request succeed.

Use the existing implementation as the source of truth for the exact
authentication flow.

Never log complete JWTs, credentials, secrets, or sensitive
authentication material.

------------------------------------------------------------------------

# Authorization

Authorization is especially important in Coaching because access depends
on both identity and coaching relationships.

For Coaching-owned resources:

-   Enforce authorization server-side.
-   Verify coach-client relationships when access depends on that
    relationship.
-   Verify resource ownership where applicable.
-   Do not rely on UI visibility as authorization.
-   Do not trust a coach ID or client ID supplied by the caller when
    authenticated identity should determine the actor.
-   Consider IDOR risks whenever an API accepts user, client, coach,
    relationship, profile, plan, or progress identifiers.
-   Protect coach-only operations explicitly.
-   Protect client-only operations explicitly where the domain requires
    it.
-   Prevent one coach from accessing or mutating another coach's client
    data unless explicitly authorized.
-   Prevent one client from accessing or mutating another client's
    coaching data unless explicitly authorized.

The Coaching Service is responsible for authorization over the resources
it owns.

Do not delegate Coaching resource authorization to the client
application.

------------------------------------------------------------------------

# Relationship-Based Authorization

When authorization depends on a coach-client relationship, verify the
relationship from authoritative Coaching-owned data.

Do not assume that:

-   A caller is a coach merely because a coach ID was supplied.
-   A caller is the client merely because a client ID was supplied.
-   A coach may access every client.
-   A historical or inactive relationship grants the same access as an
    active relationship.
-   UI navigation proves authorization.

Use the authenticated identity together with the established Coaching
relationship model.

When relationship status affects authorization, derive the behavior from
the current implementation and confirmed requirements rather than
inventing semantics.

------------------------------------------------------------------------

# API Design

Treat Coaching APIs as long-lived contracts.

Before changing an existing endpoint, determine:

-   Existing consumers
-   Request contract
-   Response contract
-   Validation behavior
-   Authentication requirements
-   Authorization requirements
-   Relationship requirements
-   Error behavior
-   Pagination behavior where applicable
-   Sorting behavior where applicable
-   Important side effects
-   Compatibility impact

Prefer resource-oriented APIs and established HTTP semantics where
consistent with the existing service.

Validate external input at the service boundary.

Use consistent response and error structures already established by the
repository.

Do not silently change an API contract while implementing an unrelated
feature.

When a breaking change is necessary, identify affected consumers and
migration implications explicitly.

------------------------------------------------------------------------

# Service-to-Service Communication

The Coaching Service may communicate with other Pump services when
coaching functionality requires data owned by another domain.

The intended boundary is:

``` text
Coaching Service
       ↓
Explicit Service API / Established Messaging Boundary
       ↓
Owning Service
       ↓
Domain Information
```

Never allow Coaching to directly query another service's database.

Do not:

-   Share another service's database tables or collections.
-   Give Coaching repository-level access to another service's
    persistence.
-   Reproduce Auth credential/authentication logic.
-   Reproduce Social business logic.
-   Move another domain's data into Coaching merely to avoid an API
    call.
-   Expose Coaching database internals as a cross-service contract.

Cross-service requests should retrieve only the information required by
the Coaching capability.

Minimize unnecessary coupling to another service's implementation.

Treat other services and external dependencies as potentially
unavailable.

Where a cross-service call is required, consider:

-   Timeouts
-   Failure behavior
-   Error translation
-   Retry safety
-   Partial failure
-   Response compatibility
-   Observability
-   Whether batching is available or appropriate

Do not introduce asynchronous messaging unless it is already established
or explicitly required.

------------------------------------------------------------------------

# User Information from Other Services

Coaching may need user-facing information owned by another Pump service
in order to display or process coaching relationships.

When retrieving user information:

-   Use the established service API.
-   Request only the information required by the Coaching capability.
-   Prefer batch lookup when multiple users must be resolved and an
    established batch contract exists.
-   Avoid N+1 service calls.
-   Do not treat externally owned profile data as Coaching-owned merely
    because it appears in a Coaching response.
-   Handle missing users and dependency failures explicitly.

The exact owning service and available fields must be determined from
the current implementation and confirmed service contracts.

Do not invent cross-service fields or endpoints.

------------------------------------------------------------------------

# Database Ownership

The Coaching Service exclusively owns its PostgreSQL database.

Other Pump services must not directly read or write Coaching tables.

PostgreSQL is the source of truth for Coaching-owned persisted data.

When changing persistence:

-   Inspect the existing entity and repository model.
-   Preserve data integrity.
-   Use appropriate constraints.
-   Consider relationship uniqueness.
-   Consider nullability.
-   Consider foreign-key behavior within the Coaching-owned database.
-   Consider indexing based on actual query patterns.
-   Consider transaction boundaries.
-   Consider migration compatibility.
-   Avoid destructive schema changes without explicit justification and
    migration planning.

Database constraints should protect important invariants where
appropriate rather than relying entirely on application code.

Do not duplicate another service's domain model inside Coaching simply
because Coaching stores that service's user identifier.

------------------------------------------------------------------------

# Data Modeling

Model Coaching data according to actual domain ownership and access
patterns.

Before adding or changing persisted state, consider:

-   Ownership
-   Relationship cardinality
-   Uniqueness
-   Nullability
-   Lifecycle
-   Historical data requirements
-   Query patterns
-   Index requirements
-   Transaction requirements
-   Compatibility with existing data

Avoid multiple independently mutable sources of truth for the same
coaching state.

Do not persist a convenience boolean or derived field when the
authoritative state can be reliably derived from an existing
Coaching-owned relationship, plan, or record unless the tradeoff is
explicitly understood and justified.

When a persisted schema changes, determine whether existing data
requires migration, backfill, or backward-compatible reading.

------------------------------------------------------------------------

# Transactions and Consistency

Use transaction boundaries intentionally.

When an operation modifies multiple Coaching-owned records that must
remain consistent, determine whether they belong in the same
transaction.

Consider:

-   Coach-client relationship state
-   Profile state
-   Training-plan state
-   Progress records
-   Derived or dependent Coaching-owned state

Do not add broad transaction boundaries without understanding their
purpose.

For operations involving another service, do not assume a local
PostgreSQL transaction can provide distributed atomicity.

Consider partial failure and retry behavior explicitly.

------------------------------------------------------------------------

# Validation

Treat all external input as untrusted.

Validation should occur at appropriate boundaries and include, where
relevant:

-   Required values
-   Format
-   Length
-   Allowed values
-   Numeric ranges
-   Resource identifiers
-   Relationship invariants
-   Uniqueness
-   Business invariants
-   Ownership-sensitive input
-   Security-sensitive restrictions

Do not rely solely on a client application to validate input.

Client-side validation exists for user experience; backend validation
protects the system.

Do not infer identity, role, relationship, or ownership from untrusted
request fields when authenticated server-side context or authoritative
Coaching data is available.

Do not invent fitness or health validation thresholds without an
established requirement.

------------------------------------------------------------------------

# Error Handling

Use the Coaching Service's established error model.

Distinguish meaningful categories where supported by the implementation,
including:

-   Validation failure
-   Authentication failure
-   Authorization failure
-   Resource not found
-   Relationship not found or invalid relationship state where
    established
-   Conflict
-   Dependency failure
-   Internal failure

Do not expose stack traces, secrets, tokens, database internals, or
other sensitive implementation details through API responses.

Logs may contain diagnostic context, but they must not contain sensitive
authentication material or unnecessary personal/coaching data.

Preserve useful correlation/request context where the existing
application supports it.

------------------------------------------------------------------------

# Logging and Observability

Logs should make Coaching failures diagnosable without exposing
sensitive data.

Useful context may include:

-   Request/correlation ID
-   Operation
-   Failure category
-   Relevant non-sensitive resource identifiers
-   Dependency failure
-   Unexpected exception context

Avoid logging:

-   Full JWTs
-   Credentials or secrets
-   Sensitive request payloads
-   Unnecessary personal or coaching-profile data
-   Large plan/progress payloads when identifiers and operation context
    are sufficient

Preserve existing correlation and structured logging conventions.

Do not add noisy logs to normal Coaching paths without operational
value.

------------------------------------------------------------------------

# Reliability

Coaching functionality should remain predictable under dependency
failures, retries, and duplicate requests.

Consider:

-   Failure behavior
-   Timeouts for external dependencies
-   Retry safety
-   Idempotency
-   Duplicate requests
-   Partial failures
-   PostgreSQL availability
-   Cross-service effects
-   Recovery behavior

Do not assume dependencies are always available.

For operations that may be retried, determine whether duplicate
execution can create duplicate relationships, plans, progress records,
or other incorrect Coaching state.

Design retry-safe behavior where appropriate.

------------------------------------------------------------------------

# Performance

Coaching correctness, authorization, and data integrity take precedence
over micro-optimization.

Still consider:

-   Database query efficiency
-   Appropriate indexes
-   N+1 database queries
-   N+1 service calls
-   Unnecessary network calls
-   Response size
-   Serialization costs
-   Pagination where collections can grow
-   Batch operations where justified

Measure before optimizing.

Do not weaken authorization, validation, or consistency for performance.

------------------------------------------------------------------------

# Testing

Meaningful Coaching changes should be verified at the appropriate level.

Depending on the change, consider:

-   Unit tests
-   Service tests
-   Repository tests
-   Controller/API tests
-   Integration tests
-   Authentication tests
-   Authorization tests
-   Relationship-based authorization tests
-   Validation tests
-   Persistence constraints
-   Coach-client relationship behavior
-   Profile behavior
-   Training-plan behavior where applicable
-   Progress behavior where applicable
-   Duplicate-request/idempotency behavior
-   Cross-service dependency failure
-   Regression tests for bugs

Prioritize observable behavior, authorization, and domain boundaries
over implementation details.

For protected operations, test both expected allowed behavior and
expected denied behavior.

For relationship-based access, test authorized and unauthorized
coach/client combinations where relevant.

Do not remove or weaken tests merely to make a change pass.

Run the smallest relevant test suite during development and broader
verification when the scope warrants it.

------------------------------------------------------------------------

# Before Changing Code

Before proposing or implementing a change:

1.  Inspect the relevant Coaching implementation.
2.  Understand the current request and coaching-domain flow.
3.  Identify affected layers and components.
4.  Check existing conventions and patterns.
5.  Check relevant tests.
6.  Check relevant configuration.
7.  Determine PostgreSQL/schema impact.
8.  Determine API compatibility impact.
9.  Determine authentication and authorization impact.
10. Determine coach-client relationship impact.
11. Determine whether another Pump service is affected.
12. Determine consistency, idempotency, or migration impact where
    relevant.
13. Prefer extending an existing pattern over introducing an unnecessary
    new one.

Do not make assumptions about code that can be inspected.

------------------------------------------------------------------------

# Scope Control

Keep changes narrowly focused on the requested outcome.

Do not introduce unrelated:

-   Refactors
-   Formatting changes
-   Dependency upgrades
-   Architecture changes
-   Database/schema changes
-   Security changes
-   Infrastructure changes
-   Generated-file changes
-   Naming changes

unless required by the requested change or explicitly requested.

If an improvement is valuable but outside scope, report it separately
instead of silently implementing it.

------------------------------------------------------------------------

# Requirements and Uncertainty

Do not invent:

-   API contracts
-   Database schema
-   Coach-client relationship semantics
-   Coaching status semantics
-   Profile fields
-   Training-plan lifecycle
-   Training-block behavior
-   Exercise modeling
-   Nutrition calculations
-   Progress calculations
-   Authentication behavior
-   Authorization rules
-   Configuration values
-   Cross-service requirements
-   Business rules

When important information is unavailable:

1.  Identify what is missing.
2.  Explain why it matters.
3.  Inspect the repository when the answer should already exist there.
4.  Ask for clarification when necessary.

Clearly distinguish:

-   Confirmed requirements
-   Observed implementation
-   Engineering recommendations
-   Assumptions

Never present an assumption as established Coaching behavior.

------------------------------------------------------------------------

# Dependencies

Before adding a dependency:

1.  Determine whether the existing stack already solves the problem.
2.  Explain why the dependency is necessary.
3.  Consider maintenance and security implications.
4.  Consider operational and deployment impact.
5.  Prefer mature and well-supported dependencies.
6.  Avoid adding a dependency for trivial functionality.

Do not upgrade unrelated dependencies as part of a feature unless
required.

------------------------------------------------------------------------

# Code Quality

Follow existing Java and Spring conventions in this repository.

Prefer:

-   Clear names
-   Small cohesive methods
-   Explicit responsibilities
-   Constructor injection where consistent with the project
-   Immutable data where practical
-   Existing abstractions
-   Straightforward control flow
-   Domain-appropriate validation
-   Explicit ownership and authorization rules

Avoid:

-   God classes
-   Hidden side effects
-   Duplicated cross-service business logic
-   Unnecessary abstractions
-   Premature generic frameworks
-   Deeply nested logic
-   Duplicated sources of truth
-   Authorization behavior that depends on undocumented assumptions

Comments should explain why when the reason is not obvious, rather than
narrating what the code already says.

------------------------------------------------------------------------

# Architecture Changes

Do not introduce significant architecture changes casually.

For changes involving:

-   Coaching service boundaries
-   Coaching database ownership
-   Coach-client relationship model
-   Training-plan architecture
-   Cross-service communication
-   Authentication integration
-   Relationship-based authorization
-   Coaching consistency model
-   Asynchronous messaging
-   Major framework or technology changes

explain:

-   Context
-   Problem
-   Options considered
-   Proposed decision
-   Tradeoffs
-   Data consistency implications
-   Security implications
-   Compatibility implications
-   Operational consequences

Use an ADR when the decision has meaningful long-term architectural
impact.

------------------------------------------------------------------------

# Code Review

When reviewing Coaching Service changes, prioritize:

1.  Correctness
2.  Authorization and relationship-based access
3.  Coaching-domain boundaries
4.  API contract compatibility
5.  Data integrity and consistency
6.  PostgreSQL schema/query correctness
7.  Authentication integration
8.  Coach-client relationship correctness
9.  Cross-service dependency behavior
10. Idempotency and duplicate-request behavior
11. Error behavior
12. Reliability
13. Test coverage
14. Observability
15. Maintainability
16. Performance

Treat unauthorized coaching-data access, IDOR, authentication bypasses,
cross-service database access, sensitive-data exposure, and destructive
data-consistency bugs as high-severity findings.

Separate required fixes from optional improvements.

Do not manufacture findings merely to populate a review.

------------------------------------------------------------------------

# Communication

Explain important engineering decisions, especially when they affect
Coaching ownership, coach-client relationships, authorization, data
consistency, API behavior, or cross-service communication.

When proposing an improvement:

-   Explain what should change.
-   Explain why.
-   Explain the tradeoffs.
-   Explain whether it belongs in the current scope.

Challenge unsafe or fragile approaches rather than implementing them
silently.

Keep narrow tasks focused and avoid overwhelming them with unrelated
theoretical concerns.

------------------------------------------------------------------------

# Handoff

At the completion of meaningful work, summarize:

-   What changed
-   Why it changed
-   Files/components affected
-   API impact
-   PostgreSQL/schema impact
-   Authentication/authorization impact
-   Coach-client relationship impact
-   Cross-service impact
-   Consistency/idempotency implications
-   Tests or verification performed
-   Configuration impact
-   Remaining risks
-   Assumptions or uncertainties
-   Recommended follow-up work, if any

Clearly distinguish completed work from suggested future improvements.

------------------------------------------------------------------------

# Codex Working Rules

When operating through Codex in this repository:

-   Inspect before editing.
-   Use the repository implementation as the source of truth for current
    behavior.
-   Keep changes within the Coaching Service unless explicitly asked
    otherwise.
-   Do not modify another Pump repository as a side effect.
-   Do not invent missing contracts, schema, relationship semantics, or
    configuration.
-   Do not expose secrets or sensitive authentication/coaching data in
    output.
-   Review authorization and coach-client relationship implications
    before completing Coaching changes.
-   Run relevant tests and static checks when available.
-   Report exactly what was changed and what verification was performed.
-   Report anything that could not be verified.
-   Do not silently fix unrelated issues discovered during the task.

------------------------------------------------------------------------

# Confirmed Coaching Service Constraints

The following constraints should be preserved unless an explicit
architectural decision changes them:

-   Coaching owns coach-client relationships.
-   Coaching owns client enrollment and coaching relationship state.
-   Coaching owns coaching-specific client profiles and coaching data.
-   Coaching owns training blocks and training-plan data where
    implemented.
-   Coaching persists Coaching-owned data in PostgreSQL.
-   Other Pump services must not directly access the Coaching database.
-   Coaching does not own authentication identity or credentials.
-   Authentication identity remains owned by the Auth Service.
-   Coaching does not own Social-domain data.
-   Cross-service data access must use explicit service boundaries.
-   HTTP-based service-to-service communication is used where
    applicable.
-   Spring `RestClient` is used for HTTP communication where established
    by the implementation.
-   Coaching resource authorization must be enforced server-side.
-   Coach-client relationship checks must be enforced server-side where
    access depends on that relationship.
-   Existing consumers should remain compatible where practical.

------------------------------------------------------------------------

# Final Principle

Build the Pump Coaching Service as a clear, reliable owner of Pump's
coaching domain.

Prefer:

-   Correctness over shortcuts
-   Explicit ownership over blurred service boundaries
-   Server-side authorization over client trust
-   Authoritative relationship state over caller-provided assumptions
-   Clear consistency rules over hidden side effects
-   Simplicity over cleverness
-   Consistency over personal preference
-   Maintainability over premature optimization
-   Backward-compatible evolution over casual contract changes
-   Evidence over assumptions

Coaching data represents relationships and plans that can affect what
different users are allowed to see and modify.

Changes to Coaching should therefore be narrow, deliberate, testable,
secure, consistent, and understandable to the engineers who maintain it.
