
var host = "http://localhost:8080";
var gameID = "";
var eventbus_mio;

function  registerHandlerForUpdateGame(name, gameID) {
    eventbus_mio = new EventBus(host + '/eventbus');
    eventbus_mio.onopen = function () {
        eventbus_mio.registerHandler('game.' + gameID, function (error, jsonResponse) {
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
                if (js.couldStart === true) {
                    document.getElementById("startButton").disabled = false;
                }
            }

        });

        eventbus_mio.registerHandler('game.' + gameID + '/start', function (error, jsonResponse) {
            console.log("inside start eventbus handler");
            if (jsonResponse != null) {
                console.log(jsonResponse.body);
                var js = JSON.parse(jsonResponse.body);
                document.getElementById("waiting").style.visibility = "hidden" ;
                document.getElementById("game").style.visibility = "visible" ;
            }
        });

        joinRequest(name, gameID);
    }

    eventbus_mio.onclose = function (){
        var obj = new Object();
        obj.gameID = gameID;
        obj.user  = name;
        var req= JSON.stringify(obj);
        console.log(name + "si è disconnesso");
        var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
        xmlhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
               console.log("disconnected successfully")
            }
        };
        xmlhttp.open("POST", host + "/api/game/disconnect/" + gameID);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        console.log("in leave");
        xmlhttp.send(JSON.stringify(req));
    }
}

function init(){
    var url = new URL(document.URL);
    var name = url.searchParams.get("name");
    console.log(name);
    addItem(name);
    gameID = url.searchParams.get("gameID");
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

function startGame() {
    var req = {};
    req.gameID = gameID;
    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.open("POST", host + "/api/game/start/" + gameID);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    console.log("in start");
    xmlhttp.send(JSON.stringify(req));
}

function  close1() {
    eventbus_mio.close();
}


