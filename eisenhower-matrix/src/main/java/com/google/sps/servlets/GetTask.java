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
import javax.servlet.ServletException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.InputStream;
import java.util.Scanner;
import com.google.sps.classes.Task;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


/** Servlet that returns handles posting of each task**/
@WebServlet("/add-task")
public class GetTask extends HttpServlet {

    private List<Task> tasks;
    private List<Long> ids;
    private static String email;

    public GetTask() {
        tasks = new ArrayList<Task>();
        ids = new ArrayList<Long>();
    }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FilterPredicate filter = new FilterPredicate("email",FilterOperator.EQUAL, email);
    
        Query q = new Query("Task").setFilter(filter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);
        Gson gson = new Gson();
        int duration = 30;
        for (Entity e : pq.asIterable()) {
            Task task = entityToTask(e);
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(task));

        }
    
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("id") != null) {
            deleteTask(new Long(request.getParameter("id")));
            response.sendRedirect("/");
            return;
        } 

        try {
            int name = parseStringToInt(request.getParameter("name"));
            deleteTask((int) parseStringToInt(request.getParameter("id")));
        } catch (NullPointerException error) {
            String name = request.getParameter("name");
            Date date = new Date();
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
            } catch (ParseException e) {}

            int importance = parseStringToInt(request.getParameter("importance"));
            int duration = 30;
            
            
            Entity taskEntity = new Entity("Task");
            UserService us = UserServiceFactory.getUserService();

            taskEntity.setProperty("name", name);
            taskEntity.setProperty("date", date);
            taskEntity.setProperty("importance", importance);
            taskEntity.setProperty("duration", duration);
            taskEntity.setProperty("id", taskEntity.getKey().getId());
            taskEntity.setProperty("email", email);

            addTask(name, date, importance, duration, taskEntity.getKey().getId());

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(taskEntity);
        }
        response.sendRedirect("/");
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        email = inputStreamToString(request.getInputStream());
        email = email.substring(1, email.length()-1);
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(email));
    }
    private static String inputStreamToString(InputStream inputStream) {
      Scanner scanner = new Scanner(inputStream, "UTF-8");
      return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
  }

    private String taskToJson(Task task) {
        Gson gson = new Gson();
        return gson.toJson(task);
  
    }

    private void addTask(String name, Date time, int importance, int duration, long id) {
        Task t = new Task(name, time, importance, duration, id);
        tasks.add(t);
    }

    private void deleteTask(long id) {
        System.out.println("Task with id " + id + " being deleted");
        Key taskEntityKey = KeyFactory.createKey("Task", id);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        tasks.removeIf(obj -> obj.getId() == id);
        datastore.delete(taskEntityKey);
    }

    private Task entityToTask(Entity e) {
        String name = (String)e.getProperty("name");
        Date date = (Date)e.getProperty("date");
        Long importance = (Long)e.getProperty("importance");
        Long duration = (Long)e.getProperty("duration");
        Long id = (Long)e.getKey().getId();
        return new Task(name, date, importance.intValue(), duration.intValue(), id.longValue());
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
    public static String getEmail() {
        return email;
    }

  }

  
