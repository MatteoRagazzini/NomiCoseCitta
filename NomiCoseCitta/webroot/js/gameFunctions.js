
var host = "http://localhost:8080";
var gameID = "";
var userID = "";
var roundStarted = false;
var eventbus_mio;

function getSocketUri(url){
    var startIndex = url.indexOf("/eventbus");
    return url.slice(startIndex);
}

function  registerHandlerForUpdateGame(name, gameID) {
    eventbus_mio = new EventBus(host + '/eventbus');
    eventbus_mio.onopen = function () {
        eventbus_mio.registerHandler('game.' + gameID, function (error, jsonResponse) {
            if (jsonResponse !== "null") {
                console.log(jsonResponse.body);
                var js = JSON.parse(jsonResponse.body);
                var ul = document.getElementById("dynamic-list");
                ul.innerHTML = '';
                js.users.forEach(user => {
                    var li = document.createElement("li");
                    li.setAttribute('id', user.nickname);
                    li.appendChild(document.createTextNode(user.nickname));
                    ul.appendChild(li);
                });
                if (js.couldStart === true) {
                    document.getElementById("startButton").disabled = false;
                }
            }
        });

        eventbus_mio.registerHandler('game.' + gameID + '/start', function (error, jsonResponse) {
            if (jsonResponse != null && !roundStarted) {
                var js = JSON.parse(jsonResponse.body);
                document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
                var span = document.getElementById("categories");
                js.settings.categories.forEach(category => {
                    var label = document.createElement("label");
                    label.setAttribute("for", category);
                    label.appendChild(document.createTextNode(category));

                    var br = document.createElement("br");
                    var br1 = document.createElement("br");

                    var inputElement = document.createElement("input");
                    inputElement.setAttribute("id", category);
                    inputElement.setAttribute("type", "text");
                    inputElement.setAttribute("name", category);

                    span.appendChild(label);
                    span.appendChild(br);
                    span.appendChild(inputElement);
                    span.appendChild(br1);

                });
                roundStarted = true;
                document.getElementById("waiting").style.display = "none" ;
                document.getElementById("game").style.display = "inline" ;

            }
        });

        eventbus_mio.registerHandler('game.' + gameID +"/stop", function (error, jsonResponse) {
                    if (jsonResponse !== "null") {
                        roundStarted = false;
                        sendWord(document.getElementById("gameForm"));
                    }
                });

        joinRequest(name,getSocketUri(eventbus_mio.sockJSConn._transport.url), gameID);
    }
}

function init(){
    var url = new URL(document.URL);
    userID = url.searchParams.get("name");
    addItem(userID);
    gameID = url.searchParams.get("gameID");
    var gameIdParagraph = document.getElementById("gameID").textContent;
    document.getElementById("gameID").innerHTML = gameIdParagraph + gameID;
    registerHandlerForUpdateGame(userID, gameID);
}

function joinRequest(name, address, gameID){
    var req = {};
    req.userID = name;
    req.gameID = gameID;
    req.userAddress = address;
    console.log("In Join address: " + address);
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
    console.log("in join " + JSON.stringify(req));
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

function sendWord(form){
    const data = new FormData(form);
    const value = Object.fromEntries(data.entries());
    console.log({value});
    value.gameID = gameID;
    value.userID = userID;
    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function() {
         if (this.readyState === 4 && this.status === 200) {
         }
    };
    xmlhttp.open("POST", host + "/api/game/words");
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(JSON.stringify(value));
    document.getElementById("game").style.display = "none";

}

function stopRound() {
   eventbus_mio.publish('game.' + gameID +"/stop", "STOP");
}


