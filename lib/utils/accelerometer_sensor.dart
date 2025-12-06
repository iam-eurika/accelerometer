import 'package:flutter/services.dart';
import 'package:accelerometer/models/sensor_data.dart';

class AccelerometerSensorUtil {
  static const String _channelName = "accelerometer_stream";

  static const _eventChannel = EventChannel(_channelName);
  Stream<SensorData> sensorStream = _eventChannel
      .receiveBroadcastStream()
      .map((dynamic event) => SensorData.fromMap(event));
}
