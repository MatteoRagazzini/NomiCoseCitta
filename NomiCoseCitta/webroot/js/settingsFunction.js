var gameID = 0;
var name = "";
var host = "http://localhost:8080";


function init() {
   var url = new URL(document.URL);
   name = url.searchParams.get("name");
   document.getElementById("userID").placeholder = name;
}

function addItem(){
    var span = document.getElementById("categoriesSpan");
    var candidate = document.getElementById("candidate");
    var p = document.createElement("p");
    var checboxSpan = document.createElement("span");
    checboxSpan.innerText = candidate.value;
    var label = document.createElement("label");
    var checkbox = document.createElement("input");
    checkbox.setAttribute("type","checkbox");
    checkbox.setAttribute("name","categories");
    checkbox.setAttribute("id",candidate.value);
    checkbox.setAttribute("value",candidate.value);
    checkbox.setAttribute("checked", "true");
    label.append(checkbox);
    label.append(checboxSpan);
    p.append(label);
    span.appendChild(p);

    document.getElementById("candidate").value = "";

}

function create() {
       const data = new FormData(document.getElementById("settingsForm"));
       const value = Object.fromEntries(data.entries());
       value.categories = data.getAll("categories");
       console.log({value});
       var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
        xmlhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                gameID = xmlhttp.responseText;
                console.log("inside creation callback");
                window.location.href = "game.html?name=" + name + "&gameID=" + gameID;
            }
        };
       xmlhttp.open("POST", host + "/api/game/create");
       xmlhttp.setRequestHeader("Content-Type", "application/json");
       console.log("in create");
       xmlhttp.send(JSON.stringify(value));
}

