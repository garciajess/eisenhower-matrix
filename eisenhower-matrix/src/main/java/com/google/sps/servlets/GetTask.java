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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.google.sps.data.Task;
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
    private List<Integer> ids;

    public DataServlet() {
        tasks = new ArrayList<Task>();
        ids = new ArrayList<Integer>();
    }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query q = new Query("Task").addSort("StartTime", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery pq = datastore.prepare(q);
    int duration = 30;

        for (Entity task : pq.asIterable()) {
            addTask((String) task.getProperty("name"), (Date) task.getProperty("date"), (int) task.getProperty("importance"), duration, task.getKey().getId());
        }

      for (Task task : tasks) {
          if (!ids.contains(task.getId())) {
            ids.add(task.getId());
            String json = taskToJson(task);
            response.setContentType("application/json;");
            response.getWriter().println(json);
          } 
      }
    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    Date date = request.getParameter("date");
    int importance = request.getParameter("importance");
    int duration = 30;

    Entity taskEntity = new Entity("Task");
    UserService us = UserServiceFactory.getUserService();

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

    private void addTask(String name, Date time, int importance, int duration, int id) {
        Task t = new Task(name, time, importance, duration, id);
        tasks.add(t);
    }

    private void deleteTask(int id) {
        Key taskEntityKey = KeyFactory.createKey("Task", id);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.delete(taskEntityKey);
    }
 

  }

  
