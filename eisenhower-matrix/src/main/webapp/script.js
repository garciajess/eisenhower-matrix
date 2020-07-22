// check login status on load
document.addEventListener("DOMContentLoaded", function () {
  // every time there is a submission, fetch tasks
  getTasks();

});

// checks if task if empty or only includes whitespace
function checkTitle() {
  let title = document.getElementById("taskName");
  title.value = title.value.replace(/^\s+/, "").replace(/\s+$/, "");

  if (title.value === "") {
    /* title is empty or only whitespace */
    document.getElementById("taskName").style.borderColor = "red";
  } else {
    /* title is valid */
    getTasks();
  }
}

// add each posted task to DOM; append it to #fetched-content div
async function getTasks() {
  let task_div = document.createElement("div");
  let spinner = document.getElementById("spinner");

  const response = await fetch("/add-task");
  const text = await response.text();

  var all_tasks = text.split("\n");
  all_tasks = all_tasks.filter((x) => x.length > 0);

  all_tasks.forEach(function (val, idx, arr) {
    arr[idx] = JSON.parse(val);
  });

  for (let task of all_tasks) {
    let curr_task = document.createElement("div");
    curr_task.classList += "task justify-content-between";

    let name = document.createElement("span");
    name.innerText = task.name;
    name.classList += " task-name";

    let importance = document.createElement("span");
    importance.appendChild(document.createTextNode("[Lvl "));
    importance.appendChild(document.createTextNode(task.category));
    importance.appendChild(document.createTextNode("]"));
    importance.classList += " task-importance";

    let date = document.createElement("span");
    date.appendChild(document.createTextNode("Date: "));
    date.appendChild(document.createTextNode(task.dueDate));
    date.classList += " task-date";

    let duration = document.createElement("span");
    duration.appendChild(document.createTextNode("Duration: "));
    duration.appendChild(document.createTextNode(task.duration));
    duration.appendChild(document.createTextNode(" mins"));
    duration.classList += " task-duration";

    let deleteButton = document.createElement("button");
    deleteButton.innerText = "X";
    deleteButton.style.color = "red";
    deleteButton.addEventListener("click", function (e) {
      deleteComment(task.id);
      e.preventDefault();
      curr_task.style.display = "none";
    });

    curr_task.appendChild(name);
    curr_task.appendChild(importance);
    curr_task.appendChild(date);
    curr_task.appendChild(duration);
    curr_task.appendChild(deleteButton);
    task_div.appendChild(curr_task);
  }

  if (document.getElementById("task-list")) {
    spinner.style.display = "none";
    document.getElementById("task-list").appendChild(task_div);
  }
}

// function to create the entire calander
async function createCal() {
  console.log("creating calander...");
  var tasks = await fetch("/calendar").then((res) => res.json());
  console.log(tasks);
  let spinner = document.getElementById("spinner");
  let taskView = document.getElementById("task-view");
  taskView.style.display = "none";
  spinner.style.display = "flex";
  

  var CLIENT_ID =
    "470404283189-q3gbv28dhmra1bg82g1evcn4c6gt3d2k.apps.googleusercontent.com";
  var API_KEY = "AIzaSyBtsuyHcg_Ei9wf2bdx7IZ-DdY56CnY3jU";

  // Array of API discovery doc URLs for APIs used by the quickstart
  var DISCOVERY_DOCS = [
    "https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest",
  ];

  // Authorization scopes required by the API; multiple scopes can be
  // included, separated by spaces.
  var SCOPES = "https://www.googleapis.com/auth/calendar";
  fetch("https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest") // Call the fetch function passing the url of the API as a parameter
    .then(function () {
      gapi.load("client:auth2", initClient);

      function initClient() {
        console.log("fetched google calendar api");

        gapi.client
          .init({
            apiKey: API_KEY,
            clientId: CLIENT_ID,
            discoveryDocs: DISCOVERY_DOCS,
            scope: SCOPES,
          })
          .then(function () {
            // Listen for sign-in state changes.'
            spinner.style.display = "none";
            console.log(gapi.auth2.getAuthInstance().isSignedIn.get());
            var newCal = gapi.client.calendar.calendars
              .insert({
                summary: "Task Schedule",
                description: "Your tasks, scheduled by the Eisenhower Matrix.",
              })
              .then(function (response) {});
          });
      }
    })
    .catch(function () {
    spinner.style.display = "none";
      console.error();
    });

    spinner.style.display = "flex";
}

function deleteComment(id) {
  const form = document.getElementById("delete-task-form");
  const idField = document.getElementById("delete-task-param");
  idField.value = id;
  form.addEventListener("submit", function (e) {
    e.preventDefault();
  });
  form.submit();
}

async function userLoggedIn() {
  const response = await fetch("/authenticate");
  const text = await response.text();
  return text.indexOf("login") !== -1;
}

// fetch calendar data
async function calendarGetData() {
  var CLIENT_ID =
    "470404283189-q3gbv28dhmra1bg82g1evcn4c6gt3d2k.apps.googleusercontent.com";
  var API_KEY = "AIzaSyBtsuyHcg_Ei9wf2bdx7IZ-DdY56CnY3jU";

  // Array of API discovery doc URLs for APIs used by the quickstart
  var DISCOVERY_DOCS = [
    "https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest",
  ];

  // Authorization scopes required by the API; multiple scopes can be
  // included, separated by spaces.
  var SCOPES = "https://www.googleapis.com/auth/calendar";

  var authorizeButton = document.getElementById("login");
  var signoutButton = document.getElementById("logout");
  fetch("https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest") // Call the fetch function passing the url of the API as a parameter
    .then(function () {
      // Your code for handling the data you get from the API

      // On load, called to load the auth2 library and API client library.
      gapi.load("client:auth2", initClient);

      // Initializes the API client library and sets up sign-in state
      // listeners.

      function initClient() {
        gapi.client
          .init({
            apiKey: API_KEY,
            clientId: CLIENT_ID,
            discoveryDocs: DISCOVERY_DOCS,
            scope: SCOPES,
          })
          .then(
            function () {
              // Listen for sign-in state changes.
              gapi.auth2
                .getAuthInstance()
                .isSignedIn.listen(updateSigninStatus);

              // Handle the initial sign-in state.
              updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
              authorizeButton.onclick = handleAuthClick;
              signoutButton.onclick = handleSignoutClick;
            },
            function (error) {
              appendPre(JSON.stringify(error, null, 2));
            }
          );
      }

      // /**
      // *  Called when the signed in status changes, to update the UI
      // *  appropriately. After a sign-in, the API is called.
      // */
      function updateSigninStatus(isSignedIn) {
        if (isSignedIn) {
          getTasks();
          authorizeButton.style.display = "none";
          signoutButton.style.display = "block";
          document.getElementById("sample-events").classList.remove("hide");
          document.getElementById("fetched-content").classList.remove("hide");
          listUpcomingEvents();
        } else {
          authorizeButton.style.display = "block";
          signoutButton.style.display = "none";
          document.getElementById("sample-events").classList.add("hide");
          document.getElementById("fetched-content").classList.add("hide");
        }
      }

      // /**
      // *  Sign in the user upon button click.
      // */
      function handleAuthClick(event) {
        gapi.auth2.getAuthInstance().signIn();
        updateSigninStatus(true);
      }

      // /**
      // *  Sign out the user upon button click.
      // */
      function handleSignoutClick(event) {
        gapi.auth2.getAuthInstance().signOut();
        updateSigninStatus(false);
      }

      // /**
      // * Append a pre element to the body containing the given message
      // * as its text node. Used to display the results of the API call.
      // *
      // * @param {string} message Text to be placed in pre element.
      // */
      function appendPre(message) {
        var pre = document.getElementById("sample-events");
        var textContent = document.createTextNode(message + "\n");
        pre.appendChild(textContent);
      }

      // /**
      // * Print the summary and start datetime/date of the next ten events in
      // * the authorized user's calendar. If no events are found an
      // * appropriate message is printed.
      // */
      function listUpcomingEvents() {
        if (document.getElementById("sample-events").innerText == "") {
          gapi.client.calendar.events
            .list({
              calendarId: "primary",
              timeMin: new Date().toISOString(),
              showDeleted: false,
              singleEvents: true,
              maxResults: 10,
              orderBy: "startTime",
            })
            .then(function (response) {
              var events = response.result.items;
              appendPre("Upcoming events:\n");

              if (events.length > 0) {
                for (i = 0; i < events.length; i++) {
                  var event = events[i];
                  var when = event.start.dateTime;
                  if (!when) {
                    when = event.start.date;
                  }
                  appendPre(event.summary + " (" + when + ")\n");
                  // console.log(event);
                }
              } else {
                appendPre("No upcoming events found.");
              }
            });
        }
      }
    })
    .catch(function () {
      console.error();
    });
}
