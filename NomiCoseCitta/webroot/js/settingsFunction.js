var createdGames = 0;
var name = "";
function  registerHandlerForUpdateGame(game_id) {
    var eventBus = new EventBus('http://localhost:8080/eventbus');
    eventBus.onopen = function () {
        eventBus.registerHandler('game.' + game_id);
        console.log("game id created")
    }
}

function init() {
   var url = new URL(document.URL);
   name = url.searchParams.get("name");
   document.getElementById("userID").value = name;
   createdGames=createdGames+1;
   registerHandlerForUpdateGame(createdGames);
    document.getElementById("gameID").value = createdGames;
   var form = document.querySelector('form');
   form.addEventListener('submit', handleSubmit);
}

function addItem(){
    var ul = document.getElementById("dynamic-list");
    var candidate = document.getElementById("candidate");
    var li = document.createElement("li");
    li.setAttribute('id',candidate.value);
    li.setAttribute("name", "categories");
    li.appendChild(document.createTextNode(candidate.value));
    ul.appendChild(li);
    document.getElementById("candidate").value = "";

}

function removeItem(){
    var ul = document.getElementById("dynamic-list");
    var candidate = document.getElementById("candidate");
    var item = document.getElementById(candidate.value);
    ul.removeChild(item);
}

function handleSubmit(event) {
    // il problema è che quando io vorrei aggiungere le categorie, per qualche motivo scatta questo handler.
    //Bisognerebbe riuscire a differenziare gli eventi.
   event.preventDefault();
       const data = new FormData(event.target);
       const value = Object.fromEntries(data.entries());
       value.categories = data.getAll("categories");
       console.log({value});
       var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
       xmlhttp.open("POST", "http://localhost:8080/api/game/" + createdGames);
       xmlhttp.setRequestHeader("Content-Type", "application/json");
       console.log("in create");
       window.location.href = "waitingRoom.html?name=" + name + "&gameID=" + createdGames;
       xmlhttp.send(JSON.stringify(value));
}

