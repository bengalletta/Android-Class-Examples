package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    private String category;

    public ToDoItem(String description, String dueDate, String category) {
        this.description = description;
        this.dueDate = dueDate;
        this.category = category; //added column to handle the spinner category selection
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    //added getter method for category
    public String getCategory(){
        return category;
    }

    //added setter method for category
    public void setCategory(String category){
        this.category = category;
    }
}