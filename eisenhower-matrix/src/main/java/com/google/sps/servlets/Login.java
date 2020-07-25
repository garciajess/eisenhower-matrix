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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns login URL */
@WebServlet("/authenticate")
public class Login extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
      UserService us = UserServiceFactory.getUserService();
      
      if (us.isUserLoggedIn()) {  
          String redirectUrl = "/";
          String logout = us.createLogoutURL(redirectUrl);
          response.getWriter().println(logout);
      } else {
          String redirectUrl = "/";
          String login = us.createLoginURL(redirectUrl);
          response.getWriter().println(login);
      }
  }

  }

  
