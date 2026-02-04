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

@Model
final class Item {
    var name: String
    var category: String?
    var statusRaw: String
    var createdAt: Date

    var container: Container?

    var status: ItemStatus {
        get { ItemStatus(rawValue: statusRaw) ?? .inContainer }
        set { statusRaw = newValue.rawValue }
    }

    init(
        name: String,
        category: String? = nil,
        status: ItemStatus = .inContainer,
        container: Container? = nil,
        createdAt: Date = Date()
    ) {
        self.name = name
        self.category = category
        self.statusRaw = status.rawValue
        self.container = container
        self.createdAt = createdAt
    }
}
