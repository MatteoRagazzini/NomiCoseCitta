var createdGames = 0;
function  registerHandlerForUpdateGame(game_id) {
    var eventBus = new EventBus('http://localhost:8080/eventbus');
    eventBus.onopen = function () {
        eventBus.registerHandler('game.' + game_id);
        console.log("game id created")
    }
}

function init() {
   var url = new URL(document.URL);
   var name = url.searchParams.get("name");
   document.getElementById("userID").value = name;
   console.log(name);
   var form = document.querySelector('form');
   form.addEventListener('submit', handleSubmit);
}

function create() {

}

function handleSubmit(event) {
   event.preventDefault();
   const data = new FormData(event.target);
   const value = Object.fromEntries(data.entries());
   console.log({ value });
   createdGames=createdGames+1;
   registerHandlerForUpdateGame(createdGames);
   var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
   xmlhttp.open("POST", "http://localhost:8080/api/game/" + createdGames);
   xmlhttp.setRequestHeader("Content-Type", "application/json");
   console.log("in create");
   xmlhttp.send(JSON.stringify(value));
}

