package com.example.androidbmicompositeexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_bmi_calculator.CalculatorBrain
import com.example.androidbmicompositeexample.ui.theme.AndroidBMICompositeExampleTheme
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import java.math.BigDecimal

private const val FLUTTER_ENGINE_ID = "flutter_engine"
private const val CHANNEL = "com.example.bmi/data"

class MainActivity : ComponentActivity() {
    private lateinit var methodChannel: MethodChannel
    private lateinit var calculatorBrain: CalculatorBrain
    private lateinit var flutterEngine: FlutterEngine
    private var userHeight: MutableState<Int> = mutableIntStateOf(165)
    private var userWeight: MutableState<Int> = mutableIntStateOf(65)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFlutterEngine()
        calculatorBrain = CalculatorBrain()
        setContent {
            AndroidBMICompositeExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }

    @Composable
    fun Greeting() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1.0f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "CALCULATE BMI",
                    fontSize = 30.sp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Height"
                )
                HeightText()
            }
            HeightSlider()
            Spacer(modifier = Modifier.requiredHeight(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Weight"
                )
                WeightText()
            }
            WightSlider()
            Spacer(modifier = Modifier.requiredHeight(30.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val height = userHeight.value.toFloat() / 100
                    val weight = userWeight.value.toFloat()
                    Log.i("Shit", "height: $height")
                    Log.i("Shit", "weight: $weight")
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
            ) {
                Text(
                    text = "Calculate",
                    fontSize = 20.sp,
                )
            }
            Spacer(modifier = Modifier.requiredHeight(30.dp))
        }
    }

    @Composable
    fun HeightText() {
        val myHeight by userHeight

        Text(
            text = "$myHeight cm",
            modifier = Modifier,
        )
    }

    @Composable
    fun HeightSlider() {
        val myHeight by userHeight

        Slider(
            value = myHeight.toFloat(),
            onValueChange = {
                userHeight.value = it.toInt()
            },
            valueRange = 100.toFloat()..250.toFloat()
        )
    }

    @Composable
    fun WeightText() {
        val myWeight by userWeight

        Text(
            text = "$myWeight kg",
            modifier = Modifier,
        )
    }

    @Composable
    fun WightSlider() {
        val myWeight by userWeight

        Slider(
            value = myWeight.toFloat(),
            onValueChange = {
                userWeight.value = it.toInt()
            },
            valueRange = 30.toFloat()..200.toFloat()
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AndroidBMICompositeExampleTheme {
            Greeting()
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
}

