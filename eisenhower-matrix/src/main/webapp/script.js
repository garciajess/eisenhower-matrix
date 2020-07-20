// fetch login link
async function login() {
    const response = await fetch("/authenticate");
    const text = await response.text();
    window.location.href = text;
}

// fetch logout link
async function logout() {
    const response = await fetch("/authenticate");
    const text = await response.text();
    window.location.href = text;
}

// hide login or logout button + form based on login status
function loginStatus(status) {
    const loginButton = document.getElementById("login");
    const logoutButton = document.getElementById("logout");
    const form = document.getElementById("taskForm");

    if (status) {
        loginButton.classList.add("hide");
        logoutButton.classList.remove("hide");
        form.classList.remove("hide");
    } else {
        loginButton.classList.remove("hide");
        logoutButton.classList.add("hide");
        form.classList.add("hide");
    }
}

// checks if task if empty or only includes whitespace
function checkTitle() {
    let title = document.getElementById("taskName");
    title.value = title.value.replace(/^\s+/, '').replace(/\s+$/, '');

    if (title.value === '') {
        /* title is empty or only whitespace */
        document.getElementById('taskName').style.borderColor = "red";
    } else {
        /* title is valid */
        getTasks();
    }
}

// add each posted task to DOM; append it to #fetched-content div
async function getTasks() {
    let task_div = document.createElement("div");

    const response = await fetch("/add-task");
    const text = await response.text();

    var all_tasks = text.split("\n");
    all_tasks = all_tasks.filter((x) => x.length > 0);

    all_tasks.forEach(function(val, idx, arr) {
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
        deleteButton.addEventListener("click", function(e) {
            deleteComment(task.id);
            e.preventDefault();
            curr_task.style.display = "none";
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
            deleteButton.addEventListener("click", function() {
                deleteComment(task.id);
                curr_task.style.display = "none";
            });

            curr_task.appendChild(name);
            curr_task.appendChild(importance);
            curr_task.appendChild(date);
            curr_task.appendChild(duration);
            curr_task.appendChild(deleteButton);
            task_div.appendChild(curr_task);
        }

        if (document.getElementById("fetched-content")) {
            document.getElementById("fetched-content").appendChild(task_div);
        }
    }

    // function to create the entire calander
    async function createCal() {
        console.log("creating calander...");
        // const response = await fetch("/calendar");
        // const text = await response.json().then(data =>{
        // console.log(data);
        // });
        var tasks = await fetch('/calendar').then(res => res.json());
        console.log(tasks);

        var CLIENT_ID = '470404283189-q3gbv28dhmra1bg82g1evcn4c6gt3d2k.apps.googleusercontent.com';
        var API_KEY = 'AIzaSyBtsuyHcg_Ei9wf2bdx7IZ-DdY56CnY3jU';

        // Array of API discovery doc URLs for APIs used by the quickstart
        var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest"];

        // Authorization scopes required by the API; multiple scopes can be
        // included, separated by spaces.
        var SCOPES = "https://www.googleapis.com/auth/calendar";
        fetch("https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest") // Call the fetch function passing the url of the API as a parameter
            .then(function() {
                gapi.load("client:auth2", initClient);

                function initClient() {
                    console.log("fetched google calendar api");

                    gapi.client.init({
                        apiKey: API_KEY,
                        clientId: CLIENT_ID,
                        discoveryDocs: DISCOVERY_DOCS,
                        scope: SCOPES
                    }).then(function() {
                        // Listen for sign-in state changes.
                        console.log(gapi.auth2.getAuthInstance().isSignedIn.get());
                        var newCal = gapi.client.calendar.calendars.insert({
                            "summary": "Task Schedule",
                            "description": "Your tasks, scheduled by the Eisenhower Matrix."
                        }).then(function(response) {});
                    });
                }

            }).catch(function() {
                console.log("o no");
                console.error();
            });
    }

    function deleteComment(id) {
        const form = document.getElementById("delete-task-form");
        const idField = document.getElementById("delete-task-param");
        idField.value = id;
        form.addEventListener('submit', function(e) {
            e.preventDefault();
        });
        form.submit();
    }

    async function userLoggedIn() {
        const response = await fetch("/authenticate");
        const text = await response.text();
        return text.indexOf("login") !== -1;
    }