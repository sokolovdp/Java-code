package com.sokolovdp.todolist.datamodel;

import java.time.LocalDate;

/**
 * Created by sokol on 01-Sep-16.
 */
public class ToDoItem {
    private String shortdescription;
    private String details;
    private LocalDate deadLine;

    public ToDoItem(String shortdescription, String details, LocalDate deadLine) {
        this.shortdescription = shortdescription;
        this.details = details;
        this.deadLine = deadLine;
    }

    public String getShortdescription() {
        return shortdescription;
    }

    public String getDetails() {
        return details;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public void setShortdescription(String shortdescription) {
        this.shortdescription = shortdescription;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

//    @Override
//    public String toString() {
//        return this.shortdescription;
//    }
}
