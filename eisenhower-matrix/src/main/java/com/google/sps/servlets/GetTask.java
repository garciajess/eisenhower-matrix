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

package com.google.sps.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import com.google.sps.classes.Task;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns handles posting of each task**/
@WebServlet("/add-task")
public class GetTask extends HttpServlet {

    private List<Task> tasks;
    private List<Long> ids;

    public GetTask() {
        tasks = new ArrayList<Task>();
        ids = new ArrayList<Long>();
    }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query q = new Query("Task").addSort("duration", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery pq = datastore.prepare(q);

    // String name = "";
    // Date date = new Date();
    // int importance = 1;

    // if (request.getParameter("id") != null) {
    //     deleteTask(new Long(request.getParameter("id")));
    //     response.sendRedirect("/");
    //     return;
    // } 

    // try {
    //     date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
    // } catch (NullPointerException e) {
    
    // } catch (ParseException e) {

    // }
    
    // try {
    //     importance = parseStringToInt(request.getParameter("importance"));
    // } catch (NullPointerException e) {};

    // name = request.getParameter("name");
    // int duration = 30;
    
    tasks.clear();
    for (Entity task : pq.asIterable()) {
          String name = (String) task.getProperty("name");
          Date date = (Date) task.getProperty("date");
          int importance = Math.toIntExact((Long) task.getProperty("importance"));
          int duration = Math.toIntExact((Long) task.getProperty("duration"));
           addTask(name, date, importance, duration, task.getKey().getId());
    }

      for (Task task : tasks) {
            String json = taskToJson(task);
            response.setContentType("application/json;");
            response.getWriter().println(json);
      }
    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = "";
    Date date = new Date();

    if (request.getParameter("id") != null) {
        deleteTask(new Long(request.getParameter("id")));
        response.sendRedirect("/");
        return;
    } 

    try {
        // int nameInt = parseStringToInt(request.getParameter("name"));
        deleteTask((Long) parseStringToLong(request.getParameter("id")));
    } catch (NullPointerException error) {
        name = request.getParameter("name");
        date = new Date();
    }

    try {
        date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
    } catch (ParseException e) {}
    
    int importance = parseStringToInt(request.getParameter("importance"));
    name = request.getParameter("name");
    int duration = 30;

    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("name", name);
    taskEntity.setProperty("date", date);
    taskEntity.setProperty("importance", importance);
    taskEntity.setProperty("duration", duration);
    taskEntity.setProperty("id", taskEntity.getKey().getId());

    addTask(name, date, importance, duration, taskEntity.getKey().getId());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.sendRedirect("/");
  
  }

    private String taskToJson(Task task) {
        Gson gson = new Gson();
        return gson.toJson(task);
  
    }

    private void addTask(String name, Date time, int importance, int duration, Long id) {
        Task t = new Task(name, time, importance, duration, id);
        tasks.add(t);
    }

    private void deleteTask(Long id) {
            Key taskEntityKey = KeyFactory.createKey("Task", id);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            tasks.removeIf(obj -> obj.getId() == id);
            datastore.delete(taskEntityKey);
    }
 
    private int parseStringToInt(String s) {
        if (s.matches("\\d+")) {
            return Integer.parseInt(s);
        } else {
            if (s.length() == 1) {
                return 4;
            } else {
                throw new NullPointerException("String is not a number");
            }
        }
    }

    private Long parseStringToLong(String s) {
        if (s.matches("\\d+")) {
            return Long.parseLong(s);
        } else {
            if (s.length() == 1) {
                return new Long(4);
            } else {
                throw new NullPointerException("String is not a number");
            }
        }
    }

  }

  
