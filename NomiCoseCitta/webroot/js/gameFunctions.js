
var host = "http://localhost:8080";
var gameID = "";
var userID = "";
var roundStarted = false;
var evaluationStarted = false;
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
                    var div = document.createElement("div");
                    div.className = "chip";
                    div.innerHTML = "<i class='material-icons'>face</i>" + user.nickname;

                    li.appendChild(div);
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
                document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
                document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
                if(js.settings.roundsType === "stop"){
                    document.getElementById("stopButton").style.display = "inline" ;
                }
                var span = document.getElementById("categories");
                js.settings.categories.forEach(category => {
                    // <div className="row">
                    //     <div className="input-field col s12">
                    //         <input type="text" id="userID" name="userID" className="validate">
                    //             <label htmlFor="userID">User ID</label>
                    //     </div>
                    // </div>
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

        eventbus_mio.registerHandler('game.' + gameID +"/evaluate", function (error, jsonResponse) {
            if (jsonResponse !== "null" && !evaluationStarted) {
                evaluationStarted = true;
                document.getElementById("game").style.display = "none";
                document.getElementById("waiting").style.display = "none";
                document.getElementById("evaluation").style.display = "inline";
                var js = JSON.parse(jsonResponse.body);
                loadWords(js);
            }
        });

        // eventbus_mio.registerHandler('game.' + gameID +"/scores", function (error, jsonResponse) {
        //     if (jsonResponse !== "null") {
        //         document.getElementById("evaluation").style.display = "none";
        //         document.getElementById("scores").style.display = "inline";
        //         var js = JSON.parse(jsonResponse.body);
        //         loadScores(js);
        //     }
        // });



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
    xmlhttp.open("POST", host + "/api/game/words/" + gameID);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(JSON.stringify(value));
}

function stopRound() {
   eventbus_mio.publish('game.' + gameID +"/stop", "STOP");
}

function  loadWords(js){
    //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
    //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
    var span = document.getElementById("usersWords");
    js.usersWords.forEach(userWords => {
        var relatedUser = userWords["userID"];
        form = document.createElement("form");
        form.setAttribute("class", "votes");
        form.setAttribute("id", relatedUser)
        for(var key in userWords) {
            if (key === "userID") {
                var br = document.createElement("br");
                var br4 = document.createElement("br");

                var label = document.createElement("label");
                label.setAttribute("for", userWords[key]);
                label.innerHTML = "parole di: ";

                var playerName = document.createElement("input");
                playerName.setAttribute("id", userWords[key]);
                playerName.setAttribute("type", "text");
                playerName.setAttribute("name", "userID");
                playerName.setAttribute("readonly", true);
                playerName.value = userWords[key];

                form.appendChild(br)
                form.appendChild(label);
                form.appendChild(playerName);
                form.appendChild(br4);
            } else {
                var br1 = document.createElement("br");
                var br2 = document.createElement("br");
                var br3 = document.createElement("br");
                var label = document.createElement("label");
                label.setAttribute("for", key + " - " + relatedUser);
                label.innerHTML = key;

                var inputElement = document.createElement("input");
                inputElement.setAttribute("id", key + " - " + relatedUser);
                inputElement.setAttribute("type", "text");
                inputElement.setAttribute("name", key);
                inputElement.value = userWords[key];
                inputElement.setAttribute("readonly", "true");

                var radioOk = document.createElement("input");
                radioOk.setAttribute("type", "radio");
                radioOk.setAttribute("id", "ok" + relatedUser);
                radioOk.setAttribute("name", key);
                radioOk.setAttribute("value", "ok");
                radioOk.setAttribute("checked", "true");
                var labelOk = document.createElement("label");
                labelOk.setAttribute("for", key);
                labelOk.innerHTML = "OK";
                var radioNo = document.createElement("input");
                radioNo.setAttribute("type", "radio");
                radioNo.setAttribute("id", "no" + relatedUser);
                radioNo.setAttribute("name", key);
                radioNo.setAttribute("value", "no");
                var labelNo = document.createElement("label");
                labelNo.setAttribute("for", key);
                labelNo.innerHTML = "NO";

                form.appendChild(label);
                form.appendChild(br2);
                form.appendChild(inputElement);
                form.appendChild(radioOk);
                form.appendChild(labelOk);
                form.append(radioNo);
                form.append(labelNo);
                form.appendChild(br3);
            }
        }
        span.appendChild(form);
    });
}

function sendEvaluation() {
    var json = [];
    var forms = document.getElementsByClassName("votes");
    for(var i=0;i<forms.length;i++){
        const data = new FormData(forms.item(i));
        const value = Object.fromEntries(data.entries());
        json[i] = value;
    }
    var finalJson = {};
    finalJson.votes = json;
    finalJson.voterID = userID;
    finalJson.gameID = gameID;
    console.log({finalJson});
    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
        }
    };
    xmlhttp.open("POST", host + "/api/game/votes/" + gameID);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(JSON.stringify(finalJson));
    // document.getElementById("evaluation").style.display = "none";
    // document.getElementById("scores").style.display = "inline";
    // loadScores();

}
//
// function  loadScores(){
//     //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
//     //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
//     let tableDiv = document.getElementById("tableDiv");
//
//     let table = document.createElement("table");
//
//     let thead = document.createElement("thead");
//
//     let tr = document.createElement("tr");
//
//     let categories = ["nomi", "cose", "città"];
//
//     //creo l'header
//     userIDHead = document.createElement("th");
//     userIDHead.innerText = "userID"
//     tr.append(userIDHead);
//     categories.forEach(category => {
//         categoryHead = document.createElement("th");
//         categoryHead.innerText = category;
//         tr.append(categoryHead);
//     });
//
//     thead.append(tr);
//     table.append(thead);
//
//
//     let tbody = document.createElement("tbody");
//
//     usersScores.forEach(usersScores =>{
//
//        userScoreRow = document.createElement("tr");
//
//        userIDCell = document.createElement("td");
//        userIDCell.innerText = usersScores.userID;
//
//        userScoreRow.append(userIDCell);
//
//        usersScores.ScoreForCategories.forEach(category => {
//            wordCell = document.createElement("td");
//            wordCell.innerText = ScoreForCategories.category.word + " " +  ScoreForCategories.category.score;
//            userScoreRow.append(wordCell);
//        });
//
//        tbody.append(userScoreRow);
//     });
//
//     tableDiv.append(table);
// }
