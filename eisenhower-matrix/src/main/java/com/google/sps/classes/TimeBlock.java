// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package main.java.com.google.sps.classes;

import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;

/**
 * Class representing a time block in a calendar for a specific task. 
 * providing methods to make ranges easier to work with (e.g. {@code overlaps}).
 */
public final class TimeBlock {
  
    private final Task task;
    private final Date start;
    private final Date end;

  public TimeBlock(Task task, Date start, Date end) {
    this.task = task;
    this.start = start;
    if (end == null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.MINUTE, task.getDuration());
        this.end = cal.getTime();
    } else {
        this.end = end;
    }
    
  }
   /**
   * Returns the task to be done within timeblock.
   */
  public Task getTask() {
    return task;
  }


  /**
   * Returns the start date/time.
   */
  public Date getStart() {
    return start;
  }

  /**
   * Returns the end date/time.
   */
  public Date getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return String.format("%s: [%t, %t)",task.getName(), start, end);
  }
  
}
