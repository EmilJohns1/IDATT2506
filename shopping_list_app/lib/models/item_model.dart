class Item {
  String name;
  bool isPurchased;

  Item({required this.name, this.isPurchased = false});

  Map<String, dynamic> toJson() => {'name': name, 'isPurchased': isPurchased};

  factory Item.fromJson(Map<String, dynamic> json) {
    return Item(
      name: json['name'],
      isPurchased: json['isPurchased'],
    );
  }
}
