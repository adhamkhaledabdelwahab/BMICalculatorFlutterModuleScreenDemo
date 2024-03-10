//
//  ContentView.swift
//  IOS_BMI_Calculator
//
//  Created by macbook on 04/03/2024.
//

import SwiftUI
import Flutter

struct ContentView: View {
    @State private var height = 165.0
    @State private var weight = 65.0
    @State private var cal = CalculatorBrain()
    @EnvironmentObject var flutterDependencies: FlutterDependencies
    
    var body: some View {
        VStack {
            Text("CALCULATE BMI").frame(
                minWidth: 0,
                maxWidth: .infinity,
                minHeight: 0,
                maxHeight: .infinity,
                alignment: .center
            ).font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
            VStack{
                HStack{
                    Text("Height")
                    Spacer()
                    Text("\(Int(height)) cm")
                }
                Slider(
                    value: $height,
                    in: 100...250,
                    onEditingChanged: { editting in
                        
                    }
                )
            }
            Spacer()
                .frame(height: 30)
            VStack{
                HStack{
                    Text("Weight")
                    Spacer()
                    Text("\(Int(weight)) kg")
                }
                Slider(
                    value: $weight,
                    in: 30...200,
                    onEditingChanged: { editting in
                        
                    }
                )
            }
            Spacer()
                .frame(height: 30)
            Button {
                self.cal.calculateBMI(Float(weight), Float(height/100))
                
                let bmiValue = cal.getBMIValue()
                let bmiAdvice = cal.getAdvice()
                let bmiColor = cal.getColor()
                
                print(bmiValue)
                print(bmiAdvice)
                print(bmiColor)

                
                let bmiDataChannel = FlutterMethodChannel(name: "com.example.bmi/data", binaryMessenger:flutterDependencies.flutterEngine.binaryMessenger)
                    
                let jsonObject: NSMutableDictionary = NSMutableDictionary()
                jsonObject.setValue(bmiValue, forKey: "value")
                jsonObject.setValue(bmiAdvice, forKey: "advice")
                jsonObject.setValue(bmiColor, forKey: "color")
                    
                bmiDataChannel.invokeMethod("fromHostToClient", arguments: jsonObject)
                
                showFlutter()
                
            } label: {
                Text("CALCULATE").frame(maxWidth: .infinity)
            }.buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            Spacer()
                .frame(height: 30)
        }.padding()
    }
    
    func showFlutter() {
        // Get RootViewController from window scene
        guard
          let windowScene = UIApplication.shared.connectedScenes
            .first(where: { $0.activationState == .foregroundActive && $0 is UIWindowScene }) as? UIWindowScene,
          let window = windowScene.windows.first(where: \.isKeyWindow),
          let rootViewController = window.rootViewController
        else { return }

        // Create a FlutterViewController from pre-warm FlutterEngine
        let flutterViewController = FlutterViewController(
          engine: flutterDependencies.flutterEngine,
          nibName: nil,
          bundle: nil)
        flutterViewController.modalPresentationStyle = .overCurrentContext
        flutterViewController.isViewOpaque = false

        rootViewController.present(flutterViewController, animated: true)
      }
}

#Preview {
    ContentView()
}
