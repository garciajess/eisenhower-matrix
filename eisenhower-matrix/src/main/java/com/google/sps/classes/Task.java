package main.java.com.google.sps.classes;

import java.util.Date;
import java.util.Comparator;

/**
* This class describes a user's task in the Eisenhower Matrix
*/

public class Task {
    /**
    * A comparator for sorting tasks by their higher priority categories 
    * (smaller numbers have higher priority)
    */
    public static final Comparator<Task> ORDER_BY_CATEGORY = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            return Integer.compare(a.category, b.category);
        }
    };

    /**
    * A comparator for sorting tasks by their duration
    */
    public static final Comparator<Task> ORDER_BY_DURATION = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            return Integer.compare(a.duration, b.duration);
        }
    };

    /**
    * A comparator for sorting tasks by their due date
    */
    public static final Comparator<Task> ORDER_BY_DUEDATE = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            return a.getDueDate().compareTo(b.getDueDate());
        }
    };

    private String name;
    private Date dueDate;
    private int category;
    private int duration;

    /**
    * Creates an instance of a task 
    * @param name The name of the task
    * @param dueDate The date that the task needs to be completed
    * @param category The task's category in the Eisenhower matrix, from 1 to 4
    * @param duration How long the task will take to complete in minutes
    */
    public Task(String name, Date dueDate, int category, int duration) {
        this.name = name;
        this.dueDate = dueDate;
        if (category < 1 || category > 4) {
            throw new IllegalArgumentException("Task category must be between 1 and 4 inclusive");
        } else {
            this.category = category;
        }
        if(duration <= 0) {
            throw new IllegalArgumentException("Task duration must be greater than 0 minutes"); 
        } else {
            this.duration = duration;
        }
    }

    /**
    * Returns task name
    */
    public String getName() {
        return this.name;
    } 

    /**
    * Returns the task due date
    */
    public Date getDueDate() {
        return this.dueDate;
    }

    /**
    * Returns task category, from 1 to 4
    */
    public int getCategory() {
        return this.category;
    }

    /**
    * Returns task duration in minutes
    */
    public int getDuration() {
        return this.duration;
    }
    /**
    * Sets the category of the task. If an invalid argument is passed then 
    * an exception is thrown
    * @param category the numerical category; 
    * 1 == Important and Urgent, 2 == Important and Not Urgent,
    * 3 = Not Important and Urgent, 4 == Not important and Not Urgent
    */
    public void setCategory(int category) {
        if (category < 1 || category > 4) {
            throw new IllegalArgumentException("Task category must be between 1 and 4 inclusive");
        } else {
            this.category = category;
        }
    }


}