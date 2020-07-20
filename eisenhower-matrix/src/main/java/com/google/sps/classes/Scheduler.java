package com.google.sps.classes;



import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


public class Scheduler {
    
    public static Collection<TimeBlock> schedule(List<Task> tasks) {
        //sort tasks based on category, then by closest due date
        Collections.sort(tasks, Task.ORDER_BY_CATEGORY.thenComparing(Task.ORDER_BY_DUEDATE));

        LocalDateTime blockStartTime = LocalDateTime.now();
        //Sets the calendar instance to 8AM in the next day
        blockStartTime = blockStartTime.plusDays(1);
        blockStartTime = blockStartTime.withHour(8);
        blockStartTime = blockStartTime.withMinute(0);
        
        
        Collection<TimeBlock> taskSchedule = new ArrayList<TimeBlock> ();

        // Schedules each task for the next day starting from 8AM and ending at 8PM, rolling
        // over to additional days if needed
        for (Task t : tasks) {
            //Checks if we need to schedule the rest of the tasks today
            LocalDateTime blockEndTime = blockStartTime.plusMinutes(t.getDuration());

            //Prevents scheduling tasks past 8PM unless the task itself is longer 
            //than 12 hours.
            if (blockEndTime.getHour() > 20 && t.getDuration() < 720) {
                blockStartTime = blockStartTime.plusDays(1);
                blockStartTime = blockStartTime.withHour(8);
                blockStartTime = blockStartTime.withMinute(0);
            } 

            //Creates and adds a new time block for the task 

            Date timeBlockDate = Date.from(blockStartTime.atZone(ZoneId.systemDefault()).toInstant());
            TimeBlock newBlock = new TimeBlock(t,timeBlockDate, null);
            taskSchedule.add(newBlock);

            //Updates the starting date for the next time block
            blockStartTime = blockEndTime.plusMinutes(1);
        }

        return taskSchedule;
    }        
}
