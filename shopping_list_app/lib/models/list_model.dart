import 'item_model.dart';

class ShoppingList {
  String name;
  List<Item> items;

  ShoppingList({required this.name, this.items = const []});

  Map<String, dynamic> toJson() => {
        'name': name,
        'items': items.map((item) => item.toJson()).toList(),
      };

  factory ShoppingList.fromJson(Map<String, dynamic> json) {
    return ShoppingList(
      name: json['name'],
      items: (json['items'] as List<dynamic>)
          .map((item) => Item.fromJson(item))
          .toList(growable: true),
    );
  }
}
