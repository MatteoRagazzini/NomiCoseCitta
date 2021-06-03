
var host = "http://localhost:8080";

function  registerHandlerForUpdateGame(name, gameID) {
    var eventBus = new EventBus( host + '/eventbus');
    eventBus.onopen = function () {
        eventBus.registerHandler('game.' + gameID, function (error, jsonResponse) {
            if (jsonResponse != null) {
                console.log(jsonResponse.body);
                var js = JSON.parse(jsonResponse.body);
                var ul = document.getElementById("dynamic-list");
                ul.innerHTML = '';
                js.users.forEach(user => {
                    var li = document.createElement("li");
                    li.setAttribute('id', user);
                    li.appendChild(document.createTextNode(user));
                    ul.appendChild(li);
                });
                if (js.couldStart === true){
                    document.getElementById("startButton").disabled = false;
                }
            }

        });

        joinRequest(name, game_id);
    }


}

function init(){
    var url = new URL(document.URL);
    var name = url.searchParams.get("name");
    console.log(name);
    addItem(name);
    var gameID = url.searchParams.get("gameID");
    var gameIdParagraph = document.getElementById("gameID").textContent;
    document.getElementById("gameID").innerHTML = gameIdParagraph + gameID;
    registerHandlerForUpdateGame(name, gameID);
}

function joinRequest(name, gameID){
    var req = {};
    req.userID = name;
    req.gameID = gameID;
    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function() {
    if (this.readyState === 4 && this.status === 200) {
        if(xmlhttp.responseText === "null") {
            alert("You cannot join this game!");
            window.location.href = "index.html";
        }
    }
    };
    xmlhttp.open("POST", host + "/api/game/join/" + gameID);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    console.log("in join");
    // la waiting room deve aggiornarsi nel momento in cui entrano altri utenti.
    xmlhttp.send(JSON.stringify(req));
}

function addItem(name){
    var ul = document.getElementById("dynamic-list");
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    ul.appendChild(li);
}
