//
//  IOS_BMI_CalculatorApp.swift
//  IOS_BMI_Calculator
//
//  Created by macbook on 04/03/2024.
//

import SwiftUI
import Flutter
import FlutterPluginRegistrant

class FlutterDependencies: ObservableObject {
    let flutterEngine = FlutterEngine(name: "flutter-engine")
    init() {
        flutterEngine.run()
        // If you added a plugin to Flutter module, you also need to register plugin to flutter engine
        GeneratedPluginRegistrant.register(with: self.flutterEngine)
    }
}

@main
struct IOS_BMI_CalculatorApp: App {
    @StateObject var flutterDependencies = FlutterDependencies()
    
    var body: some Scene {
        WindowGroup {
            ContentView().environmentObject(flutterDependencies)
        }
    }
}
