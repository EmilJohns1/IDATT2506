import 'package:flutter/material.dart';
import '../models/list_model.dart';
import '../services/file_service.dart';
import '../widgets/list_tab.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final FileService _fileService = FileService();
  List<ShoppingList> _lists = [];
  int _currentIndex = 0;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadLists();
  }

  Future<void> _loadLists() async {
    final lists = await _fileService.loadLists();
    setState(() {
      _lists = lists;
    });
  }

  void _addList(String name) {
    if (_lists.any((list) => list.name == name)) {
      setState(() {
        _errorMessage = 'A list with this name already exists. Please choose a different name.';
      });
    } else {
      final newList = ShoppingList(name: name);
      setState(() {
        _lists.add(newList);
        _errorMessage = null;
      });
      _fileService.saveList(newList);
      Navigator.pop(context);
    }
  }

  void _deleteList(int index) {
    final listName = _lists[index].name;
    setState(() {
      _lists.removeAt(index);
    });
    _fileService.deleteList(listName);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Shopping Lists')),
      body: Stack(
        children: [
          Column(
            children: [
              _buildTabs(),
              if (_lists.isNotEmpty)
                Expanded(
                  child: ListTab(
                    list: _lists[_currentIndex],
                    onSave: _fileService.saveList,
                    onDelete: () => _showDeleteConfirmationDialog(_currentIndex),
                  ),
                ),
            ],
          ),
          if (_lists.isEmpty)
            Positioned(
              bottom: 100,
              right: 30, 
              child: Column(
                children: [
                  Text(
                    'Create Shopping List',
                    style: TextStyle(
                      fontSize: 32,
                      color: Colors.black,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Transform.rotate(
                    angle: -45 * 3.141592653589793 / 180,
                    child: Icon(
                      Icons.arrow_downward,
                      size: 48,
                      color: Colors.black,
                    ),
                  ),
                ],
              ),
            ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showAddListDialog(),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildTabs() {
    return Row(
      children: _lists.asMap().entries.map((entry) {
        final index = entry.key;
        final list = entry.value;
        return Padding(
          padding: const EdgeInsets.only(left: 8.0),
          child: GestureDetector(
            onTap: () => setState(() => _currentIndex = index),
            child: Container(
              padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: _currentIndex == index ? Colors.green : Colors.black,
                  width: 2,
                ),
              ),
              child: Text(
                list.name,
                style: TextStyle(
                  fontWeight: _currentIndex == index ? FontWeight.bold : FontWeight.normal,
                  color: _currentIndex == index ? Colors.green : Colors.black,
                ),
              ),
            ),
          ),
        );
      }).toList(),
    );
  }

  void _showAddListDialog() {
    final controller = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            title: const Text('Add New List'),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: controller,
                  onChanged: (text) {
                    setState(() {
                      _errorMessage = null;
                    });
                  },
                ),
                if (_errorMessage != null)
                  Padding(
                    padding: const EdgeInsets.only(top: 8.0),
                    child: Text(
                      _errorMessage!,
                      style: TextStyle(color: Colors.red),
                    ),
                  ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () {
                  final name = controller.text.trim();
                  if (name.isNotEmpty) {
                    _addList(name);
                    setState(() {});
                  }
                },
                child: const Text('Add'),
              ),
            ],
          );
        },
      ),
    );
  }

  void _showDeleteConfirmationDialog(int index) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete List'),
        content: const Text('Are you sure you want to delete this list?'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _deleteList(index);
            },
            child: const Text('Yes'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
            },
            child: const Text('No'),
          ),
        ],
      ),
    );
  }
}
