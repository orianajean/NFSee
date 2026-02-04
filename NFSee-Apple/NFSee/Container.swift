//
//  Container.swift
//  NFSee
//
//  A physical place (bin, drawer, shelf, closet, garage box, etc.)
//

import Foundation
import SwiftData

@Model
final class Container {
    var name: String
    var locationLabel: String?
    var createdAt: Date

    @Relationship(deleteRule: .cascade, inverse: \Item.container)
    var items: [Item] = []

    init(name: String, locationLabel: String? = nil, createdAt: Date = Date()) {
        self.name = name
        self.locationLabel = locationLabel
        self.createdAt = createdAt
    }
}
