class SensorData {
  final double x;
  final double y;
  final double z;

  SensorData.fromMap(Map<dynamic, dynamic> map)
      : x = (map['x'] as num?)?.toDouble() ?? 0.0,
        y = (map['y'] as num?)?.toDouble() ?? 0.0,
        z = (map['z'] as num?)?.toDouble() ?? 0.0;
}
