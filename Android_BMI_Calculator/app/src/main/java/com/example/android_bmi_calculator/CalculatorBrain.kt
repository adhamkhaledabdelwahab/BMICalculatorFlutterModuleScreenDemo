package com.example.android_bmi_calculator

class CalculatorBrain {
    private var bmi: BMI? = null

    fun getBMIValue(): String {
        return String.format("%.1f", bmi?.value ?: 0.0)
    }

    fun getAdvice(): String {
        return bmi?.advice ?: "No advice"
    }

    fun getColor(): String {
        return bmi?.color ?: "white"
    }

    fun calculateBMI(weight: Float, height: Float) {
        val bmiValue = weight / (height * height)
        bmi = when {
            bmiValue < 18.5 -> BMI(bmiValue, "Eat more pies!", "blue")
            bmiValue < 24.9 -> BMI(bmiValue, "Fit as a fiddle!", "green")
            else -> BMI(bmiValue, "Eat less pies!", "pink")
        }
    }
}