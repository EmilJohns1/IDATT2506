import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:shopping_list_app/main.dart';

void main() {
  testWidgets('Create and add items to a shopping list', (WidgetTester tester) async {
    await tester.pumpWidget(const ShoppingListApp());

    // Verify that the initial UI shows the app title.
    expect(find.text('Shopping Lists'), findsOneWidget);

    final fab = find.byType(FloatingActionButton);
    expect(fab, findsOneWidget);

    await tester.tap(fab);
    await tester.pumpAndSettle();

    expect(find.text('Add New List'), findsOneWidget);

    final textField = find.byType(TextField);
    await tester.enterText(textField, 'Groceries');
    await tester.pump();

    await tester.tap(find.text('Add'));
    await tester.pumpAndSettle();

    expect(find.text('Groceries'), findsOneWidget);

    await tester.tap(find.text('Groceries'));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField), 'Milk');
    await tester.testTextInput.receiveAction(TextInputAction.done);
    await tester.pump();

    expect(find.text('Milk'), findsOneWidget);

    await tester.tap(find.text('Milk'));
    await tester.pump();

    final milkText = find.text('Milk');
    final textWidget = tester.widget<Text>(milkText);
    expect(textWidget.style?.decoration, TextDecoration.lineThrough);
  });
}
