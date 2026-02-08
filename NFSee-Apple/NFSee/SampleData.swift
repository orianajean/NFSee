//
//  SampleData.swift
//  NFSee
//
//  Mock data for testing containers, items, search, and status.
//

import Foundation
import SwiftData

enum SampleData {
    /// Seeds sample containers and items when the database is empty.
    static func seed(context: ModelContext) {
        let garage = Container(name: "Garage – Blue Bin #1", locationLabel: "Garage")
        let kitchen = Container(name: "Kitchen Drawer", locationLabel: "Kitchen")
        let closet = Container(name: "Closet Shelf", locationLabel: "Bedroom")

        context.insert(garage)
        context.insert(kitchen)
        context.insert(closet)

        let twoWeeksAgo = Calendar.current.date(byAdding: .day, value: -15, to: Date())!
        let threeDaysAgo = Calendar.current.date(byAdding: .day, value: -3, to: Date())!

        let garageItems: [(String, String?, ItemStatus, Date?)] = [
            ("Power drill", "Tools", .inContainer, nil),
            ("Extension cord", "Tools", .out, threeDaysAgo),   // Yellow (out < 14 days)
            ("Paint brushes", "Supplies", .inContainer, nil),
        ]
        let kitchenItems: [(String, String?, ItemStatus, Date?)] = [
            ("Measuring tape", "Tools", .inContainer, nil),
            ("Screwdriver set", "Tools", .inContainer, nil),
        ]
        let closetItems: [(String, String?, ItemStatus, Date?)] = [
            ("Holiday lights", "Decor", .out, twoWeeksAgo),   // Red (out > 14 days)
            ("Winter gloves", "Clothing", .inContainer, nil),
            ("Photo albums", "Memorabilia", .inContainer, nil),
        ]

        for (name, category, status, outAt) in garageItems {
            let item = Item(name: name, category: category, status: status, container: garage, markedOutAt: outAt)
            garage.items.append(item)
            context.insert(item)
        }
        for (name, category, status, outAt) in kitchenItems {
            let item = Item(name: name, category: category, status: status, container: kitchen, markedOutAt: outAt)
            kitchen.items.append(item)
            context.insert(item)
        }
        for (name, category, status, outAt) in closetItems {
            let item = Item(name: name, category: category, status: status, container: closet, markedOutAt: outAt)
            closet.items.append(item)
            context.insert(item)
        }
    }
}
