//
//  ContentView.swift
//  NFSee
//
//  Main view: container list with search-first UX.
//

import SwiftUI
import SwiftData

struct ContentView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Container.name) private var containers: [Container]
    @State private var searchText = ""
    @State private var hasCheckedForSampleData = false

    @State private var statusFilter: ItemStatusFilter = .all
    @State private var selectedContainerId: PersistentIdentifier?

    private var selectedContainer: Container? {
        containers.first { $0.id == selectedContainerId }
    }

    var body: some View {
        NavigationSplitView {
            List(selection: $selectedContainerId) {
                ForEach(containers) { container in
                    NavigationLink(value: container.id) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(container.name)
                            if let location = container.locationLabel, !location.isEmpty {
                                Text(location)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                }
                .onDelete(perform: deleteContainers)
            }
            .navigationTitle("Containers")
            .searchable(text: $searchText, prompt: "Search items, categories, containers")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
                ToolbarItem {
                    Button(action: addContainer) {
                        Label("Add Container", systemImage: "plus")
                    }
                }
                ToolbarItem(placement: .primaryAction) {
                    Menu {
                        Picker("Status", selection: $statusFilter) {
                            ForEach(ItemStatusFilter.allCases, id: \.self) { filter in
                                Text(filter.rawValue).tag(filter)
                            }
                        }
                    } label: {
                        Label("Filter", systemImage: "line.3.horizontal.decrease.circle")
                    }
                }
            }
        } detail: {
            Group {
                if !searchText.trimmingCharacters(in: .whitespaces).isEmpty || statusFilter != .all {
                    SearchResultsView(
                        searchText: $searchText,
                        statusFilter: $statusFilter,
                        onSelectContainer: { container in
                            selectedContainerId = container.id
                        }
                    )
                } else if let container = selectedContainer {
                    ContainerDetailView(container: container)
                } else {
                    ContentUnavailableView(
                        "Select a container",
                        systemImage: "tray",
                        description: Text("Choose a container from the list or search for items.")
                    )
                }
            }
        }
        .task {
            guard !hasCheckedForSampleData else { return }
            hasCheckedForSampleData = true
            if containers.isEmpty {
                SampleData.seed(context: modelContext)
            }
        }
    }

    private func addContainer() {
        withAnimation {
            let newContainer = Container(name: "New Container")
            modelContext.insert(newContainer)
        }
    }

    private func deleteContainers(offsets: IndexSet) {
        withAnimation {
            for index in offsets {
                modelContext.delete(containers[index])
            }
        }
    }
}

struct ContainerDetailView: View {
    @Bindable var container: Container
    @Environment(\.modelContext) private var modelContext

    var body: some View {
        List {
            ForEach(container.items.filter { $0.status != .removed }) { item in
                HStack {
                    StatusDot(indicator: item.statusIndicator)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(item.name)
                        if let category = item.category, !category.isEmpty {
                            Text(category)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .contextMenu {
                    if item.status == .inContainer {
                        Button("Mark as Out") {
                            item.status = .out
                            item.markedOutAt = Date()
                        }
                    } else {
                        Button("Mark as In") {
                            item.status = .inContainer
                            item.markedOutAt = nil
                        }
                    }
                }
            }
            .onDelete(perform: deleteItems)
        }
        .navigationTitle(container.name)
        .toolbar {
            ToolbarItem {
                Button(action: addItem) {
                    Label("Add Item", systemImage: "plus")
                }
            }
        }
    }

    private func addItem() {
        withAnimation {
            let newItem = Item(name: "New Item", container: container)
            container.items.append(newItem)
            modelContext.insert(newItem)
        }
    }

    private func deleteItems(offsets: IndexSet) {
        withAnimation {
            let visibleItems = container.items.filter { $0.status != .removed }
            for index in offsets {
                let item = visibleItems[index]
                modelContext.delete(item)
            }
        }
    }
}

/// Colored dot indicating item status: green (in), yellow (out < 14 days), red (out > 14 days).
struct StatusDot: View {
    let indicator: ItemStatusIndicator

    var body: some View {
        Circle()
            .fill(color)
            .frame(width: 10, height: 10)
    }

    private var color: Color {
        switch indicator {
        case .green: return .green
        case .yellow: return .yellow
        case .red: return .red
        }
    }
}

#Preview {
    ContentView()
        .modelContainer(for: [Container.self, Item.self], inMemory: true)
}
