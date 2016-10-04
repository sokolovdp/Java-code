package com.sokolovdp.todolist;

import com.sokolovdp.todolist.datamodel.ToDoItem;
import com.sokolovdp.todolist.datamodel.TodoData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class Controller {
    private List<ToDoItem> toDoItems;

    @FXML
    public ListView<ToDoItem> todoListView;

    @FXML
    public TextArea itemDetailsTextArea;

    @FXML
    public Label deadLineLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem>  wantAllItems;
    private Predicate<ToDoItem>   wantTodayItems;

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue) {
                if (newValue != null) {
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    deadLineLabel.setText(df.format(item.getDeadLine()));
                }
            }
        });

        wantAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return true;
            }
        };

        wantTodayItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return (toDoItem.getDeadLine().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<ToDoItem>(TodoData.getInstance().getTodoitems(),
                wantAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList,
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        return o1.getDeadLine().compareTo(o2.getDeadLine());
                    }
                });

        //todoListView.setItems(TodoData.getInstance().getTodoitems()); // initialize from file
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setText(null);
                        else {
                            setText(item.getShortdescription());
                            if (item.getDeadLine().equals(LocalDate.now())) {
                                setTextFill(Color.RED);
                            } else if (item.getDeadLine().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BLUE);
                            } else if (item.getDeadLine().isBefore(LocalDate.now())) {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New ToDo Item");
        dialog.setHeaderText("Use this dialog to create new ToDo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            // System.out.println("Dialog OK pressed");

            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);

        } else {
            // System.out.println("Dialog Cancel pressed");
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyevent) {
        ToDoItem selecteditem = todoListView.getSelectionModel().getSelectedItem();
        if (selecteditem != null) {
            if (keyevent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selecteditem);
            }

        }
    }


    @FXML
    public void handleClickView() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        deadLineLabel.setText(item.getDeadLine().toString());
    }

    public void deleteItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDo Item");
        alert.setHeaderText("Delete Item: " + item.getShortdescription());
        alert.setContentText("Are you sure? Press OK to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleFilterButton() {
        ToDoItem selecteditem = todoListView.getSelectionModel().getSelectedItem();


        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodayItems);
            // todoListView.getSelectionModel().selectFirst();
            if (filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
            } if (filteredList.contains(selecteditem)) {
                todoListView.getSelectionModel().select(selecteditem);
            } else
                todoListView.getSelectionModel().selectFirst();
        } else {
            filteredList.setPredicate(wantAllItems);
            // todoListView.getSelectionModel().selectFirst();
            if (filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
            } if (filteredList.contains(selecteditem)) {
                todoListView.getSelectionModel().select(selecteditem);
            } else
                todoListView.getSelectionModel().selectFirst();
        }

    }

    @FXML
    public void handleExit() {
        Platform.exit();

    }
}
