package main.java.com.google.sps.classes;

import java.util.Calendar;
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
        //We are scheduling for the next day starting from 8AM and ending at 8PM
        Calendar cal = Calendar.getInstance();
        //Sets the calendar instance to 8AM in the next day
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        Date currStartingDate = cal.getTime();
        
        Collection<TimeBlock> taskSchedule = new ArrayList<TimeBlock> ();

        for (Task t : tasks) {
            //Checks if we need to schedule the rest of the tasks today
            Calendar endingCal = (Calendar)cal.clone();
            endingCal.add(Calendar.MINUTE, t.getDuration());
            //Prevents scheduling tasks past 8PM unless the task itself is longer 
            //than 12 hours.
            if (endingCal.get(Calendar.HOUR_OF_DAY) < 20 && t.getDuration() < 720) {
                cal.add(Calendar.DATE, 1);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.MINUTE, 0);
                currStartingDate = cal.getTime();
            } 

            //Creates and adds a new time block for the task 
            TimeBlock newBlock = new TimeBlock(t, currStartingDate, null);
            taskSchedule.add(newBlock);

            //Updates the starting date for the next time block
            cal.setTime(newBlock.getEnd());
            cal.add(Calendar.MINUTE, 1);
            currStartingDate = cal.getTime();
        }

        return taskSchedule;
    }        
}
