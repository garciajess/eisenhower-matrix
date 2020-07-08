

// check login status on load
document.addEventListener("DOMContentLoaded", function() {

    // every time there is a submission, fetch tasks
    getTasks();
    
    // todo: implement formal function to check login status
    const loggedIn = Number(window.localStorage.getItem("loggedin"));
 
    if (!loggedIn) window.localStorage.setItem("loggedin", 2);
    if (loggedIn === 1) {
        loginStatus(true)
    }
   
    if (loggedIn === 2) {
        loginStatus(false)
    }
    
})



// fetch login link 
async function login() {
    window.localStorage.setItem("loggedin", 1);
    const response = await fetch('/authenticate');
    const text = await response.text();
    window.location.href = text;
}

// fetch logout link 
async function logout() {
    window.localStorage.setItem("loggedin", 2);
    const response = await fetch('/authenticate');
    const text = await response.text();
    window.location.href = text;
}

// hide login or logout button + form based on login status
function loginStatus(status) {
    const loginButton = document.getElementById("login");
    const logoutButton = document.getElementById("logout")
    const form = document.getElementById("taskForm")
    console.log('status', status)
    console.log('login', loginButton.classList)
    console.log('logout', logoutButton.classList)
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

// add each posted task to DOM; append it to #fetched-content div
async function getTasks() {
    let task_div = document.createElement("div");

    const response = await fetch('/add-task');
    const text = await response.text();

    var all_tasks = text.split("\n");
    all_tasks = all_tasks.filter(x => x.length > 0);

    all_tasks.forEach(function (val, idx, arr) {
        arr[idx] = JSON.parse(val)
    })


    for (let task of all_tasks) {
        let curr_task = document.createElement("div");
        curr_task.classList += "task";

        let name = document.createElement("span");
        name.innerText = task.name;
        name.classList += " task-name";

        let importance = document.createElement("span");
        importance.appendChild(document.createTextNode("(Importance: "));
        importance.appendChild(document.createTextNode(task.category));
        importance.appendChild(document.createTextNode(") - "));
        importance.classList += " task-importance";

        let date = document.createElement("span");
        date.appendChild(document.createTextNode("Date: "));
        date.appendChild(document.createTextNode(task.dueDate));
        date.classList += " task-date";

        let duration = document.createElement("div");
        duration.appendChild(document.createTextNode("Duration: "));
        duration.appendChild(document.createTextNode(task.duration));
        duration.classList += " task-duration";

        let deleteButton = document.createElement("button");
        deleteButton.innerText = " X";
        deleteButton.style.color = "red";
        deleteButton.addEventListener('click', function() {
            deleteComment(task.id);
            curr_task.style.display = "none";
            // todo: actually find a way to delete from database
        })

        curr_task.appendChild(name);
        curr_task.appendChild(document.createTextNode(": "));
        curr_task.appendChild(importance);
        curr_task.appendChild(date);
        curr_task.appendChild(duration);
        curr_task.appendChild(deleteButton);
        task_div.appendChild(curr_task);

    }

    if (document.getElementById('fetched-content')) {
        document.getElementById('fetched-content').appendChild(task_div);
    }

}

// function to create the entire calander
function createCal() {
    console.log('creating calander...')
}

function deleteComment(id) {
    const form = document.createElement('form');
    form.method = "POST";
    form.action = "/add-task";

     const idField = document.createElement('input');
     idField.name = 'id';
     idField.value = id;

     form.appendChild(idField);
    console.log(form)
    form.submit();
}