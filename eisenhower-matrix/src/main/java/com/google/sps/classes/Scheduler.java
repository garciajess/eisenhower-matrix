package com.google.sps.classes;



import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


public class Scheduler {
    
    public Collection<TimeBlock> schedule(List<Task> tasks) {
        //sort tasks based on category, then by closest due date
        Collections.sort(tasks, Task.ORDER_BY_CATEGORY.thenComparing(Task.ORDER_BY_DUEDATE));

        // Starting Date to create time blocks
        // We are scheduling for the next day starting from 8AM and ending at 8PM
        Date currStartingDate;

        LocalDateTime currStartingTime = LocalDateTime.now();
        //Sets the calendar instance to 8AM in the next day
        currStartingTime = currStartingTime.plusDays(1);
        currStartingTime = currStartingTime.withHour(8);
        currStartingTime = currStartingTime.withMinute(0);
        
        
        Collection<TimeBlock> taskSchedule = new ArrayList<TimeBlock> ();

        for (Task t : tasks) {
            //Checks if we need to schedule the rest of the tasks today
            LocalDateTime endingTime = currStartingTime.plusMinutes(t.getDuration());

            //Prevents scheduling tasks past 8PM unless the task itself is longer 
            //than 12 hours.
            if (endingTime.getHour() < 20 && t.getDuration() < 720) {
                currStartingTime = currStartingTime.plusDays(1);
                currStartingTime = currStartingTime.withHour(8);
                currStartingTime = currStartingTime.withMinute(0);
            } 

            //Creates and adds a new time block for the task 
            currStartingDate = Date.from(currStartingTime.atZone(ZoneId.systemDefault()).toInstant());
            TimeBlock newBlock = new TimeBlock(t, currStartingDate, null);
            taskSchedule.add(newBlock);

            //Updates the starting date for the next time block
            currStartingTime = currStartingTime.plusMinutes(1);
        }

        return taskSchedule;
    }        
}
