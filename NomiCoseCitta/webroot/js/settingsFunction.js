var gameID = 0;
var name = "";
var host = "http://192.168.28.100:8080";


function init() {
   var url = new URL(document.URL);
   name = url.searchParams.get("name");
   document.getElementById("userID").value = name;
   var form = document.querySelector('form');
   form.addEventListener('submit', handleSubmit);
}

function addItem(){
    var span = document.getElementById("categoriesSpan");
    var candidate = document.getElementById("candidate");
    var checkbox = document.createElement("input");
    checkbox.setAttribute("type","checkbox");
    checkbox.setAttribute("name","categories");
    checkbox.setAttribute("id",candidate.value);
    checkbox.setAttribute("value",candidate.value);
    checkbox.checked = true;

    var label = document.createElement("label");
    label.setAttribute("for",candidate.value);
    label.innerText = candidate.value;
    span.appendChild(checkbox);
    span.appendChild(label);

    document.getElementById("candidate").value = "";

}

function removeItem(){
    var ul = document.getElementById("dynamic-list");
    var candidate = document.getElementById("candidate");
    var item = document.getElementById(candidate.value);
    ul.removeChild(item);
}

function handleSubmit(event) {
       event.preventDefault();
       const data = new FormData(event.target);
       const value = Object.fromEntries(data.entries());
       value.categories = data.getAll("categories");
       console.log({value});
       var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
        xmlhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                gameID = xmlhttp.responseText;
                console.log("inside creation callback");
                window.location.href = "waitingRoom.html?name=" + name + "&gameID=" + gameID;
            }
        };
       xmlhttp.open("POST", host + "/api/game/create");
       xmlhttp.setRequestHeader("Content-Type", "application/json");
       console.log("in create");
       xmlhttp.send(JSON.stringify(value));
}

