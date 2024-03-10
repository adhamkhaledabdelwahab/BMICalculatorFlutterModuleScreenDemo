import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BMI Calculator Module',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Map? jData1;
  Color? color;

  static const platform = MethodChannel('com.example.bmi/data');

  _MyHomePageState() {
    platform.setMethodCallHandler(_receiveFromHost);
    debugPrint("init");
  }

  Future<void> _receiveFromHost(MethodCall call) async {
    late Map<String, dynamic> jData;

    try {
      debugPrint("method: ${call.method}");

      if (call.method == "fromHostToClient") {
        final data = call.arguments;
        debugPrint("arguments: ${call.arguments}");
        jData = Map<String, dynamic>.from(data);
      }
    } catch (error) {
      debugPrint("error: $error");
    }

    setState(() {
      jData1 = jData;
      if (jData['color'] == 'blue') {
        color = Colors.blue;
      } else if (jData['color'] == 'green') {
        color = Colors.green;
      } else {
        color = Colors.pink;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: color ?? Colors.white,
        child: Center(
          child: jData1 == null
              ? const CircularProgressIndicator()
              : Column(
                  mainAxisSize: MainAxisSize.max,
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: <Widget>[
                    const Text(
                      'YOUR BMI',
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 40,
                          fontWeight: FontWeight.bold),
                    ),
                    Text(
                      jData1!['value'].toString(),
                      style: const TextStyle(
                          color: Colors.white,
                          fontSize: 70,
                          fontWeight: FontWeight.bold),
                    ),
                    Text(
                      jData1!['advice'],
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                      ),
                    ),
                  ],
                ),
        ),
      ),
    );
  }
}
