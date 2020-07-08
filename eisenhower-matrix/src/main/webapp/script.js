

// check login status on load
document.addEventListener("DOMContentLoaded", function() {
    
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