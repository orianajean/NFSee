//
//  SearchResultsView.swift
//  NFSee
//
//  Search results: items matching query, with container name, location, status.
//

import SwiftUI
import SwiftData

struct SearchResultsView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Item.name) private var allItems: [Item]
    @Binding var searchText: String
    @Binding var statusFilter: ItemStatusFilter
    var onSelectContainer: (Container) -> Void

    private var filteredItems: [Item] {
        let nonRemoved = allItems.filter { $0.status != .removed }
        let statusFiltered: [Item]
        switch statusFilter {
        case .all:
            statusFiltered = nonRemoved
        case .in_:
            statusFiltered = nonRemoved.filter { $0.status == .inContainer }
        case .out:
            statusFiltered = nonRemoved.filter { $0.status == .out }
        }
        guard !searchText.trimmingCharacters(in: .whitespaces).isEmpty else {
            return statusFiltered
        }
        let lower = searchText.lowercased()
        return statusFiltered.filter { item in
            item.name.lowercased().contains(lower) ||
            (item.category?.lowercased().contains(lower) ?? false) ||
            (item.container?.name.lowercased().contains(lower) ?? false)
        }
    }

    var body: some View {
        Group {
            if filteredItems.isEmpty {
                ContentUnavailableView.search(text: searchText)
            } else {
                List {
                    ForEach(filteredItems) { item in
                        Button {
                            if let container = item.container {
                                onSelectContainer(container)
                            }
                        } label: {
                            SearchResultRow(item: item)
                        }
                    }
                }
            }
        }
        .navigationTitle("Search")
    }
}

struct SearchResultRow: View {
    let item: Item

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(item.name)
                    .font(.headline)
                if item.status == .out {
                    Text("Out")
                        .font(.caption2)
                        .foregroundStyle(.orange)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 2)
                        .background(Color.orange.opacity(0.2), in: Capsule())
                }
            }
            if let container = item.container {
                Text(container.name)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                if let location = container.locationLabel, !location.isEmpty {
                    Text(location)
                        .font(.caption)
                        .foregroundStyle(.tertiary)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

enum ItemStatusFilter: String, CaseIterable {
    case all = "All"
    case in_ = "In"
    case out = "Out"
}

#Preview {
    SearchResultsView(
        searchText: .constant(""),
        statusFilter: .constant(.all),
        onSelectContainer: { _ in }
    )
    .modelContainer(for: [Container.self, Item.self], inMemory: true)
}
