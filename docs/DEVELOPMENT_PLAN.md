# NFSee Development Plan

This document defines how we build NFSee on **iOS (Swift/SwiftUI/SwiftData)** and **Android (Kotlin/Jetpack Compose)** in parallel, with local-first storage and minimal UI to ship quickly.

---

## 1. Sync Strategy: Local-First, No Cross-Platform Yet

**Decision: Store everything locally on each platform. No Firebase or shared backend for MVP.**

- **iOS:** SwiftData (already in place). CloudKit can be added later for optional sync.
- **Android:** Room for SQLite (add when we introduce real models). No Firebase in MVP.
- **Why:** Keeps scope small, no backend/auth, works offline, and both codebases stay independent. When you add “household sharing” (paid), you can introduce Firebase or CloudKit then and design the sync layer once the app shape is stable.

**Rule:** Do not add cross-platform sync, Firebase, or a shared API until after MVP is solid on both platforms.

---

## 2. Parallel Development Rule (iOS ↔ Android)

**Every feature or data-model change must exist on both platforms.**

When you (or the AI) change the **iOS** app:

1. **Data model:** If you add/change a SwiftData `@Model` (e.g. `Container`, `Item`, status), add the equivalent Room entity and DAO (or Compose state) on Android.
2. **Screens/flows:** If you add a screen or flow on iOS (e.g. container list, item detail, search), add the same screen/flow on Android with equivalent behavior.
3. **Business rules:** Status (In / Out / Removed), “items belong to exactly one container,” search behavior—implement the same logic on both.

When you change the **Android** app:

1. Mirror the same data shape and behavior on iOS so that a user would get the same outcome on either device (with data local to that device).

**Check before considering a feature “done”:** Can a user accomplish the same core action on both iOS and Android? If not, implement the missing side.

---

## 3. Shared “Source of Truth” (Conceptual Data Model)

Both platforms implement this model. Keep this doc updated when you add fields or entities.

### Entities

| Entity     | Purpose |
|-----------|---------|
| **Container** | A physical place (bin, drawer, shelf, closet, box). Has: name, optional photo, optional NFC id, optional location label (e.g. garage, attic). |
| **Item**      | Belongs to exactly one container. Has: name, optional category, optional photo, status (In / Out / Removed). Icon can be derived (e.g. from category or default). |

### Item Status (required)

- **In** – Item is in its container.
- **Out** – Temporarily removed; “usually lives in X, currently out.”
- **Removed** – No longer owned (soft delete).

### Optional for MVP

- NFC association (can be added later).
- Location metadata on containers (optional field from day one is fine).

---

## 4. Implementation Phases

### Phase 1: Align Data Models ✓

**Goal:** Replace template “Item with timestamp” with real **Container** and **Item** models and basic persistence.

- **iOS**
  - Add `Container` and update `Item` to match the spec (name, category, status, container relationship). Use SwiftData.
  - Keep UI minimal: list of containers, list of items per container, basic add/delete.
- **Android**
  - Add Room (or structured in-memory state for the very first step), then add `Container` and `Item` entities and DAOs mirroring iOS.
  - Same minimal UI: container list, item list per container, basic add/delete.

**Exit criteria:** Both apps store and display containers and items with the same conceptual fields and status.

### Phase 2: Search-First UX ✓

- **Both:** Add a single search entry point (e.g. search bar at top).
- Search by: item name (partial), category, container name, status (e.g. “show Out”).
- Results show: item name, container name, optional location, status.
- Same behavior and scope on iOS and Android.

### Phase 3: Item Status and “Out” ✓

- **Both:** Every item has status In / Out / Removed.
- **Both:** UI to mark an item as Out or back to In; optional “Removed” (or hide from main lists).
- **Both:** Filter or search by status so “show me what’s out” works the same way.

### Phase 4: Polish and Optional Features

- Optional container photo, optional item photo.
- Optional location label on containers.
- Basic settings (e.g. default list sort).
- NFC (optional): scan tag → open container; add later without changing core model.

### Later (Post-MVP)

- CloudKit (iOS) / Firebase or custom backend for sync and “household sharing.”
- Siri / voice (paid).
- Movement history, reminders (paid).

---

## 5. UI Approach: Basic First

- Use **native, simple** UI on each platform (SwiftUI list/detail, Compose list/detail).
- No custom design system or heavy theming for MVP.
- Reuse system components (NavigationStack / NavigationSplitView on iOS, Compose navigation on Android).
- Keep layouts flat and readable; add polish after core flows work on both platforms.

---

## 6. Where to Look in the Repo

| Platform | App entry      | Models / persistence | Main UI |
|----------|----------------|----------------------|---------|
| iOS      | `NFSee-Apple/NFSee/NFSeeApp.swift` | `NFSee-Apple/NFSee/*.swift` (SwiftData) | `ContentView.swift` + new views |
| Android  | `NFSee-Android/app/.../MainActivity.kt` | Add `data/` or `db/` (Room) + ViewModels | Compose screens in `ui/` |

---

## 7. Git Branch Workflow

- **`main`** – Stable releases only. Merge here when ready to ship.
- **`develop`** – Default branch. Active development. Feature branches merge here via PR.
- **`feature/*`** – One branch per feature (e.g. `feature/search-first-ux`). Create from `develop`, merge back via PR.

**Flow:** `feature/xyz` → PR → `develop` → when ready for release → PR → `main`.

---

## 8. Cursor Rule

A Cursor rule in `.cursor/rules/` enforces: **when changing iOS, mirror the change on Android (and vice versa for data and behavior).** Use it on every feature so parity is automatic.

---

## Summary

- **Local-first, no Firebase for MVP.** Add sync when you add paid sharing.
- **Every change on one platform is mirrored on the other** (data model, screens, behavior).
- **Single conceptual data model** (containers, items, status) implemented in SwiftData (iOS) and Room (Android).
- **Phases:** (1) Containers + Items, (2) Search, (3) Status/Out, (4) Optional photos/NFC and polish.
- **Basic UI** on both platforms until core flows are done; then iterate on design.

This keeps the plan simple, gets NFSee running on both platforms quickly, and ensures that “where is this thing?” is answered the same way on iOS and Android.
