
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
                var ul = $("#dynamic-list");
                ul.html("");
                js.users.forEach(user => {
                    var li = document.createElement("li");
                    li.setAttribute('id', user.nickname);
                    var div = document.createElement("div");
                    div.className = "chip";
                    div.innerHTML = "<i class='material-icons'>face</i>" + user.nickname;

                    li.appendChild(div);
                    ul.append(li);
                });
                if (js.couldStart === true) {
                    $('#startButton').removeAttr("disabled");
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
                var categoryDiv = document.getElementById("categories");
                js.settings.categories.forEach(category => {
                    var div = document.createElement("div");
                    div.className = "row";
                    var insideDiv = document.createElement("div");
                    insideDiv.className = "input-field col s12";

                    var label = document.createElement("label");
                    label.htmlFor = category;
                    label.innerText = category;

                    var inputElement = document.createElement("input");
                    inputElement.type = "text";
                    inputElement.id= category;
                    inputElement.name = category;

                    insideDiv.append(inputElement);
                    insideDiv.append(label);
                    div.append(insideDiv);
                    categoryDiv.append(div);
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
    M.AutoInit();
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
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    ul.appendChild(li);
//     $("#dynamic-list").add("li").attr("id", name);
//     $('#'+name).text(name);
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
    var span = $('#usersWords')[0];
    var rowDiv = document.createElement("div");
    rowDiv.className = "row"
    js.usersWords.forEach(userWords => {
        var relatedUser = userWords["userID"];
        var form = document.createElement("form");
        form.className ="col s12";
        form.id = relatedUser;
        for(var key in userWords) {
            var internalRowDiv = document.createElement("div");
            internalRowDiv.className = "row"
            if (key === "userID") {
                var userChip = document.createElement("div");
                userChip.className = "chip";
                userChip.innerHTML = "<i class='material-icons'>face</i>" + relatedUser;

                internalRowDiv.append(userChip);
            } else {
                var inputFieldDiv = document.createElement("div");
                inputFieldDiv.className = "input col s6";

                var inputElement = document.createElement("input");
                inputElement.id =  key + " - " + relatedUser;
                inputElement.type = "text";
                inputElement.name = key;
                inputElement.value = userWords[key];
                inputElement.readonly = "true";

                var label = document.createElement("label");
                label.for =  key + " - " + relatedUser;
                label.innerText = key;

                inputFieldDiv.append(label);
                inputFieldDiv.append(inputElement);

                var radioDiv = document.createElement("div");
                radioDiv.className = "col s6";

                var labelOk = document.createElement("label");

                var radioOk = document.createElement("input");
                radioOk.type = "radio";
                radioOk.id = "ok" + relatedUser;
                radioOk.name =  key;
                radioOk.value = "ok";
                radioOk.checked ="true";

                var spanOk = document.createElement("span");
                spanOk.innerText = "ok";
                labelOk.append(radioOk);
                labelOk.append(spanOk);


                var labelNo = document.createElement("label");

                var radioNo = document.createElement("input");
                radioNo.type = "radio";
                radioNo.id = "no" + relatedUser;
                radioNo.name =  key;
                radioNo.value = "no";

                var spanNo = document.createElement("span");
                spanNo.innerText = "no";

                labelNo.append(radioNo);
                labelNo.append(spanNo);

                radioDiv.append(labelOk);
                radioDiv.append(labelNo);

                internalRowDiv.append(inputFieldDiv);
                internalRowDiv.append(radioDiv);
            }
            form.append(internalRowDiv);
        }
        rowDiv.append(form);
        span.appendChild(rowDiv);
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

function  loadScores(){
    //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
    //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
    let tableDiv = document.getElementById("tableDiv");

    let table = document.createElement("table");

    let thead = document.createElement("thead");

    let tr = document.createElement("tr");

    let categories = ["nomi", "cose", "città"];

    //creo l'header
    userIDHead = document.createElement("th");
    userIDHead.innerText = "userID"
    tr.append(userIDHead);
    categories.forEach(category => {
        categoryHead = document.createElement("th");
        categoryHead.innerText = category;
        tr.append(categoryHead);
    });

    thead.append(tr);
    table.append(thead);


    let tbody = document.createElement("tbody");

    usersScores.forEach(usersScores =>{

       userScoreRow = document.createElement("tr");

       userIDCell = document.createElement("td");
       userIDCell.innerText = usersScores.userID;

       userScoreRow.append(userIDCell);

       usersScores.ScoreForCategories.forEach(category => {
           wordCell = document.createElement("td");
           wordCell.innerText = category.word + " " +  category.score;
           userScoreRow.append(wordCell);
       });

       tbody.append(userScoreRow);
    });

    tableDiv.append(table);
}
