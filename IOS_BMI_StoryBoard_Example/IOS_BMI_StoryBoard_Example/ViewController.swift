//
//  ViewController.swift
//  IOS_BMI_StoryBoard_Example
//
//  Created by macbook on 06/03/2024.
//

import UIKit
import Flutter
import FlutterPluginRegistrant

class ViewController: UIViewController {
    
    var flutterEngine: FlutterEngine!
    var methodChannel: FlutterMethodChannel!
    var calBMI:CalculatorBrain!
    
    @IBOutlet weak var weightLabel: UILabel!
    @IBOutlet weak var heightLabel: UILabel!
    @IBOutlet weak var heightSlider: UISlider!
    @IBOutlet weak var weightSlider: UISlider!
    
    required init(coder decoder: NSCoder) {
        super.init(coder: decoder)!
        flutterEngine = FlutterEngine(name: "flutter-engine")
        flutterEngine.run()
        // If you added a plugin to Flutter module, you also need to register plugin to flutter engine
        GeneratedPluginRegistrant.register(with: self.flutterEngine)
        // Create a FlutterViewController
        methodChannel = FlutterMethodChannel(name: "SpinIngWheelChannel", binaryMessenger: flutterEngine.binaryMessenger)
        calBMI = CalculatorBrain()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        weightLabel.text = "\(Int(exactly: weightSlider.value)!) kg"
        heightLabel.text = "\(Int(exactly: heightSlider.value)!) cm"
    }

    @IBAction func buttonPressed(_ sender: Any) {
        self.calBMI.calculateBMI(Float(weightSlider.value), Float(heightSlider.value/100))
        
        let bmiValue = calBMI.getBMIValue()
        let bmiAdvice = calBMI.getAdvice()
        let bmiColor = calBMI.getColor()
        
        print(bmiValue)
        print(bmiAdvice)
        print(bmiColor)

        
        let bmiDataChannel = FlutterMethodChannel(name: "com.example.bmi/data", binaryMessenger:flutterEngine.binaryMessenger)
            
        let jsonObject: NSMutableDictionary = NSMutableDictionary()
        jsonObject.setValue(bmiValue, forKey: "value")
        jsonObject.setValue(bmiAdvice, forKey: "advice")
        jsonObject.setValue(bmiColor, forKey: "color")
            
        bmiDataChannel.invokeMethod("fromHostToClient", arguments: jsonObject)
        
        // Get RootViewController from window scene
        guard
          let windowScene = UIApplication.shared.connectedScenes
            .first(where: { $0.activationState == .foregroundActive && $0 is UIWindowScene }) as? UIWindowScene,
          let window = windowScene.windows.first(where: \.isKeyWindow),
          let rootViewController = window.rootViewController
        else { return }

        // Create a FlutterViewController from pre-warm FlutterEngine
        let flutterViewController = FlutterViewController(
          engine: flutterEngine,
          nibName: nil,
          bundle: nil)
        flutterViewController.modalPresentationStyle = .overCurrentContext
        flutterViewController.isViewOpaque = false

        rootViewController.present(flutterViewController, animated: true)
    }
    
    @IBAction func onWeightChanged(_ sender: UISlider) {
        weightLabel.text = "\(Int(sender.value)) kg"
    }
    
    @IBAction func onHeightChanged(_ sender: UISlider) {
        heightLabel.text = "\(Int(sender.value)) cm"
    }
}

