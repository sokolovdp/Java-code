package com.sokolovdp.todolist;

import com.sokolovdp.todolist.datamodel.ToDoItem;
import com.sokolovdp.todolist.datamodel.TodoData;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by sokol on 05-Sep-16.
 */
public class DialogController {

    @FXML
    private TextField shortX;

    @FXML
    private TextArea detailsX;

    @FXML
    private DatePicker dateX;


    public void initialize() { // to change date picker default format
        String pattern = "yyyy-MM-dd";

        dateX.setPromptText(pattern.toLowerCase());

        dateX.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }



    public ToDoItem processResults() {

        String shortDescription = shortX.getText(); // .trim();
        String detailedDescription = detailsX.getText(); // .trim();
        LocalDate deadlineValue = dateX.getValue();

        ToDoItem nItem = new ToDoItem(shortDescription,
                detailedDescription, deadlineValue);

        TodoData.getInstance().addTodoItem(nItem);

        return nItem;

    }

}
