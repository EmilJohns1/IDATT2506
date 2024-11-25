import 'dart:convert';
import 'dart:io';
import 'package:path_provider/path_provider.dart';
import '../models/list_model.dart';

class FileService {
  Future<String> _getDirectoryPath() async {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }

  Future<void> saveList(ShoppingList list) async {
    final path = await _getDirectoryPath();
    final file = File('$path/${list.name}.json');
    final json = jsonEncode(list.toJson());
    await file.writeAsString(json);
  }

  Future<List<ShoppingList>> loadLists() async {
    final path = await _getDirectoryPath();
    final directory = Directory(path);

    if (!await directory.exists()) {
      await directory.create();
      return [];
    }

    final files = directory.listSync().whereType<File>();

    return files.map((file) {
      try {
        final content = file.readAsStringSync();

        if (content.trim().isEmpty) return null;

        final json = jsonDecode(content);
        return ShoppingList.fromJson(json);
      } catch (e) {
        return null;
      }
    }).whereType<ShoppingList>().toList();
  }

  Future<void> deleteList(String name) async {
    final path = await _getDirectoryPath();
    final file = File('$path/$name.json');
    if (await file.exists()) {
      await file.delete();
    }
  }
}
