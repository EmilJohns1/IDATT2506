import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/item_model.dart';
import '../models/list_model.dart';

class ListTab extends StatefulWidget {
  final ShoppingList list;
  final Function(ShoppingList) onSave;
  final VoidCallback onDelete;

  const ListTab({super.key, required this.list, required this.onSave, required this.onDelete});

  @override
  _ListTabState createState() => _ListTabState();
}

class _ListTabState extends State<ListTab> {
  final TextEditingController _controller = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  final ValueNotifier<bool> _isTextNotEmpty = ValueNotifier(false);

  @override
  void initState() {
    super.initState();
    _controller.addListener(() {
      _isTextNotEmpty.value = _controller.text.trim().isNotEmpty;
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    _isTextNotEmpty.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _addItem(String name) {
    setState(() {
      widget.list.items = List.from(widget.list.items);
      widget.list.items.insert(0, Item(name: name));
    });
    widget.onSave(widget.list);
  }

  void _toggleItem(int index) {
    setState(() {
      final item = widget.list.items[index];
      item.isPurchased = !item.isPurchased;
      widget.list.items.sort((a, b) => a.isPurchased ? 1 : -1);
    });
    widget.onSave(widget.list);
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Column(
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
              child: Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _controller,
                      focusNode: _focusNode,  // Assign focus node
                      decoration: const InputDecoration(hintText: 'Add item'),
                      textInputAction: TextInputAction.done,
                      onTap: () {
                        // Ensure focus is requested when the field is tapped
                        FocusScope.of(context).requestFocus(_focusNode);
                      },
                      onFieldSubmitted: (value) {
                        if (value.trim().isNotEmpty) {
                          _addItem(value.trim());
                          _controller.clear();
                          FocusScope.of(context).requestFocus(_focusNode);  // Keep focus after submission
                        }
                      },
                    ),
                  ),
                  ValueListenableBuilder<bool>(
                    valueListenable: _isTextNotEmpty,
                    builder: (context, isNotEmpty, child) {
                      return Visibility(
                        visible: isNotEmpty,
                        child: IconButton(
                          icon: const Icon(Icons.add),
                          onPressed: () {
                            final value = _controller.text.trim();
                            if (value.isNotEmpty) {
                              _addItem(value);
                              _controller.clear();
                              FocusScope.of(context).requestFocus(_focusNode);
                            }
                          },
                        ),
                      );
                    },
                  ),
                ],
              ),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: widget.list.items.length,
                itemBuilder: (context, index) {
                  final item = widget.list.items[index];
                  return ListTile(
                    title: Text(
                      item.name,
                      style: TextStyle(
                        decoration: item.isPurchased ? TextDecoration.lineThrough : null,
                      ),
                    ),
                    onTap: () => _toggleItem(index),
                  );
                },
              ),
            ),
          ],
        ),
        Positioned(
          bottom: 12,
          left: 12,
          child: GestureDetector(
            onTap: widget.onDelete,
            child: Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(10),
                color: Colors.red,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.5),
                    blurRadius: 4,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: const Icon(
                Icons.delete,
                color: Colors.white,
                size: 32,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
