//
//  ContentView.swift
//  NFSee
//
//  Main view: container list with items per container.
//

import SwiftUI
import SwiftData

struct ContentView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Container.name) private var containers: [Container]

    var body: some View {
        NavigationSplitView {
            List {
                ForEach(containers) { container in
                    NavigationLink {
                        ContainerDetailView(container: container)
                    } label: {
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
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
                ToolbarItem {
                    Button(action: addContainer) {
                        Label("Add Container", systemImage: "plus")
                    }
                }
            }
        } detail: {
            Text("Select a container")
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
                    VStack(alignment: .leading, spacing: 2) {
                        Text(item.name)
                        if let category = item.category, !category.isEmpty {
                            Text(category)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        if item.status == .out {
                            Text("Out")
                                .font(.caption2)
                                .foregroundStyle(.orange)
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

#Preview {
    ContentView()
        .modelContainer(for: [Container.self, Item.self], inMemory: true)
}
