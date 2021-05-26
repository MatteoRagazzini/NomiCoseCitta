
var host = "http://localhost:8080";

function  registerHandlerForUpdateGame(game_id) {
    var eventBus = new EventBus( host + '/eventbus');

    eventBus.onopen = function () {
        eventBus.registerHandler('game.' + game_id, function(error, message){
            var ul = document.getElementById("dynamic-list");
            var li = document.createElement("li");
            li.setAttribute('id',message);
            li.appendChild(document.createTextNode(message));
            ul.appendChild(li);
        });
    }
}

function init(){
    var url = new URL(document.URL);
    var name = url.searchParams.get("name");
    console.log(name);
    var gameID = url.searchParams.get("gameID");
    document.getElementById("gameID").value = gameID;
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
