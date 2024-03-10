package com.example.android_bmi_calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android_bmi_calculator.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

private const val FLUTTER_ENGINE_ID = "flutter_engine"
private const val CHANNEL = "com.example.bmi/data"

class MainActivity : AppCompatActivity(),
    Slider.OnSliderTouchListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var methodChannel: MethodChannel
    private lateinit var calculatorBrain: CalculatorBrain
    private lateinit var flutterEngine: FlutterEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFlutterEngine()
        calculatorBrain = CalculatorBrain()
        binding.calculateBMI.setOnClickListener(this)
        binding.heightSlider.addOnSliderTouchListener(this)
        binding.weightSlider.addOnSliderTouchListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onStartTrackingTouch(slider: Slider) {
        val sliderVal = slider.value.toInt()
        when (slider.id) {
            (binding.heightSlider.id) -> {
                binding.userHeight.text = "$sliderVal cm"
            }

            (binding.weightSlider.id) -> {
                binding.userWeight.text = "$sliderVal kg"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStopTrackingTouch(slider: Slider) {
        val sliderVal = slider.value.toInt()
        when (slider.id) {
            (binding.heightSlider.id) -> {
                binding.userHeight.text = "$sliderVal cm"
            }

            (binding.weightSlider.id) -> {
                binding.userWeight.text = "$sliderVal kg"
            }
        }
    }

    private fun setupFlutterEngine() {
        createAndConfigureFlutterEngine()
        FlutterEngineCache
            .getInstance()
            .put(FLUTTER_ENGINE_ID, flutterEngine)
    }

    private fun createAndConfigureFlutterEngine() {
        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        )
    }

    private fun launchFlutterModule() {
        startActivity(getFlutterIntent())
    }

    private fun getFlutterIntent(): Intent {
        return FlutterActivity
            .withCachedEngine(FLUTTER_ENGINE_ID)
            .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
            .build(this)
    }

    override fun onClick(v: View?) {
        val height = binding.heightSlider.value / 100
        val weight = binding.weightSlider.value
        calculatorBrain.calculateBMI(weight, height)
        val bmiValue = calculatorBrain.getBMIValue()
        val bmiAdvice = calculatorBrain.getAdvice()
        val bmiColor = calculatorBrain.getColor()
        val json = mutableMapOf<String, Any>()
        json["color"] = bmiColor
        json["advice"] = bmiAdvice
        json["value"] = bmiValue
        methodChannel.invokeMethod("fromHostToClient", json)
        launchFlutterModule()
    }
}