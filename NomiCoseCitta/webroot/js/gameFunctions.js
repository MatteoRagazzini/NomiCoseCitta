
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
                $("#roundNumber").text("Round " + (js.playedRounds + 1));
                $("#letter").text("Play with letter " + js.settings.roundsLetters[js.playedRounds]);
                if(js.settings.roundsType === "stop"){
                    $("#stopButton").css("display", "inline") ;
                }
                $("#categories").html("");
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
                    $("#categories").append(div);
                });

                roundStarted = true;
                $("#waiting").hide();
                $("#scores").hide();
                $("#game").show();

            }
        });

        eventbus_mio.registerHandler('game.' + gameID +"/stop", function (error, jsonResponse) {
            if (jsonResponse !== "null") {
                roundStarted = false;
                sendWord($("#gameForm")[0]);
            }
        });

        eventbus_mio.registerHandler('game.' + gameID +"/evaluate", function (error, jsonResponse) {
            if (jsonResponse !== "null" && !evaluationStarted) {
                evaluationStarted = true;
                console.log("Evaluated started")
                $("#game").hide();
                $("#waiting").hide();
                $("#evaluation").show();
                showEvaluationForm(JSON.parse(jsonResponse.body));
            }
        });


         eventbus_mio.registerHandler('game.' + gameID +"/scores", function (error, jsonResponse) {
             if (jsonResponse !== "null") {
                 evaluationStarted = false;
                 $("#evaluation").hide();
                 $("#circularLoader").hide();
                 $("#scores").show();
                 loadScores(JSON.parse(jsonResponse.body));
             }
         });

         eventbus_mio.registerHandler('game.' + gameID +"/finish", function (error, jsonResponse) {
                      if (jsonResponse !== "null") {
                           console.log("In finish");
                          console.log(JSON.parse(jsonResponse.body));
                          $("#waiting").hide();
                          $("#scores").hide();
                          $("#circularLoader").hide();
                          $("#game").hide();
                          $("#finalScores").show();
                          loadFinalScores(JSON.parse(jsonResponse.body));
                      }
                  });

        joinRequest(name,getSocketUri(eventbus_mio.sockJSConn._transport.url), gameID);
    }
}

function init(){
    M.AutoInit();
    var url = new URL(document.URL);
    userID = url.searchParams.get("name");
    addItem(userID);
    gameID = url.searchParams.get("gameID");
    $("#gameID").html($("#gameID").text() + gameID);
    registerHandlerForUpdateGame(userID, gameID);
}

function joinRequest(name, address, gameID){
    var req = {};
    req.userID = name;
    req.gameID = gameID;
    req.userAddress = address;
    $.post(host + "/api/game/join/" + gameID, JSON.stringify(req), (data, status) => {
        console.log(status)
        if (data === "null") {
            alert("You cannot join this game!");
            $(window).attr('location', "index.html");
        }
    } )
}

function addItem(name){
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    $("#dynamic-list").append(li);
}

function startGame() {
    var req = {};
    req.gameID = gameID;
    $.post(host + "/api/game/start/" + gameID, JSON.stringify(req), () => {})
}

function sendWord(form){
    const data = new FormData(form);
    const value = Object.fromEntries(data.entries());
    console.log({value});
    value.gameID = gameID;
    value.userID = userID;
    console.log("with jquery")
    $.post(host + "/api/game/words/" + gameID, JSON.stringify(value),  () => {})
}

function stopRound() {
   eventbus_mio.publish('game.' + gameID +"/stop", "STOP");
}

function  showEvaluationForm(js){
    //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
    //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
    $('#usersWords').html("");
    js.usersWords.forEach(userWords => {
        var relatedUser = userWords["userID"];
        var li = document.createElement("li");

        var headerDiv = document.createElement("div");
        headerDiv.className = "collapsible-header relatedUser";
        //headerDiv.innerHTML = "<i class='material-icons'>face</i>" + relatedUser;
        headerDiv.innerText = relatedUser;
        li.append(headerDiv);

        var bodyDiv = document.createElement("div");
        bodyDiv.className = "collapsible-body";

        var form = document.createElement("form");
        form.className ="col s12 votes";
        form.id = relatedUser;

        for(var key in userWords) {
            if(key !== "userID"){
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


                form.append(inputFieldDiv);
                form.append(radioDiv);
                bodyDiv.append(form)
                li.append(bodyDiv);
            }
        }
        $('#usersWords').append(li);
    });
}

function sendEvaluation() {
    var json = [];
    var forms = document.getElementsByClassName("votes");
    var relatedUsers = document.getElementsByClassName("relatedUser");
    console.log(relatedUsers)
    for(var i=0;i<forms.length;i++){
        const data = new FormData(forms.item(i));
        json[i] = Object.fromEntries(data.entries());
        json[i].userID = relatedUsers.item(i).innerText;
    }
    var finalJson = {};
    finalJson.votes = json;
    finalJson.voterID = userID;
    finalJson.gameID = gameID;
    console.log({finalJson});
    $.post(host + "/api/game/votes/" + gameID, JSON.stringify(finalJson), ()=>{});
    $("#circularLoader").show();
}

 function  loadScores(js){
     //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
     //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
     $("#tableDiv").html("");
     let table = document.createElement("table");
     let thead = document.createElement("thead");
     let tr = document.createElement("tr");
     
     //creo l'header
     let userIDHead = document.createElement("th");
     userIDHead.innerText = "User"
     tr.append(userIDHead);
     js.categories.forEach(category => {
         let categoryHead = document.createElement("th");
         categoryHead.innerText = category;
         tr.append(categoryHead);
     });

     let totalHeader = document.createElement("th");
     totalHeader.innerText = "Total"
     tr.append(totalHeader)

     thead.append(tr);
     table.append(thead);


     let tbody = document.createElement("tbody");

     js.usersScores.forEach(us =>{

        let userScoreRow = document.createElement("tr");

        let userIDCell = document.createElement("td");
        userIDCell.innerText = us.userID;
        userIDCell.style = "font-weight: bold";
        userScoreRow.append(userIDCell);

        us.wordsScores.forEach(ws => {
            let wordCell = document.createElement("td");
            var span = document.createElement("span");
            span.className = "teal-text text-darken-4";
            span.style = "font-weight: bold";
            span.innerText = ws.score + " ";
            wordCell.innerText = ws.word ;
            wordCell.append(span);
            userScoreRow.append(wordCell);
        });

         let totalCell = document.createElement("td");
         totalCell.className = "teal-text text-darken-4";
         totalCell.style = "font-weight: bold";
         totalCell.innerText = us.total;
         userScoreRow.append(totalCell);

        tbody.append(userScoreRow);
     });

     table.append(tbody);

     $("#tableDiv").append(table);
 }

function  loadFinalScores(js){
    //document.getElementById("roundNumber").innerText = "Round " + (js.playedRounds + 1);
    //document.getElementById("letter").innerText = "Play with letter " + js.settings.roundsLetters[js.playedRounds];
    $("#finalTableDiv").html("");
    let table = document.createElement("table");
    let thead = document.createElement("thead");
    let tr = document.createElement("tr");

    let UserTh = document.createElement("th");
    UserTh.innerText = "User";
    tr.append(UserTh);

    for(let i = 0; i < js.totalRoundsNumber; i++) {
        let roundTh = document.createElement("th");
        roundTh.innerText = "Round " + i;
        tr.append(roundTh);
    }

    let totalTh = document.createElement("th");
    totalTh.innerText = "Total";
    tr.append(totalTh);

    thead.append(tr);
    table.append(thead);

    let tbody = document.createElement("tbody");

    js.usersScores.forEach(rs =>{
        let roundScoreRow = document.createElement("tr");
        let userCell = document.createElement("td");
        userCell.innerText = rs.userID;
        roundScoreRow.append(userCell);
       rs.roundsScores.forEach(us => {
           let userRoundCell = document.createElement("td");
           userRoundCell.innerText = us;
           userRoundCell.style = "font-weight: bold";
           roundScoreRow.append(userRoundCell);
       })
        let totalUserCell = document.createElement("td");
        totalUserCell.innerText = rs.total;
        roundScoreRow.append(totalUserCell);
       tbody.append(roundScoreRow);
    });
    table.append(tbody);
    $("#finalTableDiv").append(table);
    $("#winner").text(js.winner);
}
