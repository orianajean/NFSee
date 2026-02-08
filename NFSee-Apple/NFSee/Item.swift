//
//  Item.swift
//  NFSee
//
//  An item that belongs to exactly one container.
//

import Foundation
import SwiftData

/// Status of an item relative to its container.
enum ItemStatus: String, Codable {
    case inContainer = "in"
    case out = "out"
    case removed = "removed"
}

/// Visual indicator for item status (green / yellow / red dot).
enum ItemStatusIndicator {
    case green   // In container
    case yellow  // Out, within last 14 days
    case red     // Out, over 14 days
}

@Model
final class Item {
    var name: String
    var category: String?
    var statusRaw: String
    var createdAt: Date
    var markedOutAt: Date?

    var container: Container?

    var status: ItemStatus {
        get { ItemStatus(rawValue: statusRaw) ?? .inContainer }
        set { statusRaw = newValue.rawValue }
    }

    var statusIndicator: ItemStatusIndicator {
        guard status == .out else { return .green }
        guard let outAt = markedOutAt else { return .yellow }
        let days = Calendar.current.dateComponents([.day], from: outAt, to: Date()).day ?? 0
        return days <= 14 ? .yellow : .red
    }

    init(
        name: String,
        category: String? = nil,
        status: ItemStatus = .inContainer,
        container: Container? = nil,
        createdAt: Date = Date(),
        markedOutAt: Date? = nil
    ) {
        self.name = name
        self.category = category
        self.statusRaw = status.rawValue
        self.container = container
        self.createdAt = createdAt
        self.markedOutAt = status == .out ? (markedOutAt ?? createdAt) : nil
    }
}
