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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.classes.Task;
import com.google.sps.classes.Scheduler;
import com.google.sps.classes.TimeBlock;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.classes.CalendarHandler;


/** Servlet that returns login URL */
@WebServlet("/calendar")
public class Calendar extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query q = new Query("Task");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Task> tasks = new ArrayList<Task>();

        for (Entity t : pq.asIterable()) {
            Task task = entityToTask(t);
            tasks.add(task);
        }

        response.setContentType("application/json");
        Collection<TimeBlock> scheduledTasks = Scheduler.schedule(tasks);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

        response.getWriter().println(gson.toJson(scheduledTasks));
    }

    private Task entityToTask(Entity e) {
        String name = (String)e.getProperty("name");
        Date date = (Date)e.getProperty("date");
        Long importance = (Long)e.getProperty("importance");
        Long duration = (Long)e.getProperty("duration");
        Long id = (Long)e.getKey().getId();
        return new Task(name, date, importance.intValue(), duration.intValue(), id.longValue());
    }
}
