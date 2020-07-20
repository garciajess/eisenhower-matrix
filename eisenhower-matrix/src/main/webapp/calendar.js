var CLIENT_ID =
    "570542970803-pd81pe65703nfqrehjkd0t0qepa4cstg.apps.googleusercontent.com";
var API_KEY = "AIzaSyBcjgN4m44efL2NJHkOZucQ0Yplhm3K2JA";

// Array of API discovery doc URLs for APIs used by the quickstart
var DISCOVERY_DOCS = [
    "https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest",
];

// Authorization scopes required by the API; multiple scopes can be
// included, separated by spaces.
var SCOPES = "https://www.googleapis.com/auth/calendar";

// fetch calendar data
async function calendarGetData() {
    var authorizeButton = document.getElementById("login");
    var signoutButton = document.getElementById("logout");

    fetch("https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest") // Call the fetch function passing the url of the API as a parameter
        .then(function() {
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
                        function() {
                            // Listen for sign-in state changes.
                            gapi.auth2
                                .getAuthInstance()
                                .isSignedIn.listen(updateSigninStatus);

                            // Handle the initial sign-in state.
                            updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
                            authorizeButton.onclick = handleAuthClick;
                            signoutButton.onclick = handleSignoutClick;
                        },
                        function(error) {
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
                        .then(function(response) {
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

            console.log("trying to add an event");

            var event = {
                summary: "Google I/O 2015",
                location: "800 Howard St., San Francisco, CA 94103",
                description: "A chance to hear more about Google's developer products.",
                start: {
                    dateTime: "2020-07-20T09:00:00-07:00",
                    timeZone: "America/Los_Angeles",
                },
                end: {
                    dateTime: "2020-07-20T17:00:00-07:00",
                    timeZone: "America/Los_Angeles",
                },
                recurrence: ["RRULE:FREQ=DAILY;COUNT=2"],
                attendees: [
                    { email: "eduardoluissd@gmail.com" },
                    { email: "esantosdelgado1@sps-program.com" },
                ],
                reminders: {
                    useDefault: false,
                    overrides: [
                        { method: "email", minutes: 24 * 60 },
                        { method: "popup", minutes: 10 },
                    ],
                },
            };

            var request = gapi.client.calendar.events.insert({
                calendarId: "primary",
                resource: event,
            });

            request.execute(function(event) {
                console.log("terminee...", event);
                appendPre("Event created: " + event.htmlLink);
            });

            console.log("terminee...");
        })
        .catch(function() {
            console.error();
        });
}