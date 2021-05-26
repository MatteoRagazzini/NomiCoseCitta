
var host = "http://localhost:8080";

function  registerHandlerForUpdateGame(game_id) {
    var eventBus = new EventBus( host + '/eventbus');

    eventBus.onopen = function () {
        eventBus.registerHandler('game.' + game_id, function(error, jsonResponse){
            if(jsonResponse!= null){
                console.log(jsonResponse.body);
                var js = JSON.parse(jsonResponse.body);
                var ul = document.getElementById("dynamic-list");
                ul.innerHTML= '';
                js.users.forEach(user =>{
                    console.log("adding users");
                    var li = document.createElement("li");
                    li.setAttribute('id',user);
                    li.appendChild(document.createTextNode(user));
                    ul.appendChild(li);
                });
            }

        });
    }
}

function init(){
    var url = new URL(document.URL);
    var name = url.searchParams.get("name");
    console.log(name);
    var gameID = url.searchParams.get("gameID");
    var gameIdParagraph = document.getElementById("gameID").textContent;
    document.getElementById("gameID").innerHTML = gameIdParagraph + gameID;
    registerHandlerForUpdateGame(gameID);
    addItem(name);
}

function addItem(name){
    var ul = document.getElementById("dynamic-list");
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    ul.appendChild(li);
}
