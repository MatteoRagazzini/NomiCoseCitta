var name = "";
var host = "http://localhost:8080";

function init(){
 var form = document.querySelector('form');
 form.addEventListener('submit', join);
}


function newGame(){

 var name = document.getElementById('userID').value;
 window.location.href = "settings.html?name="+name;
}

function join(event){
 var name = document.getElementById('userID').value;
 var gameID = document.getElementById('gameID').value;
 event.preventDefault();
 const data = new FormData(event.target);
 const value = Object.fromEntries(data.entries());
 console.log({value});
 var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
 xmlhttp.onreadystatechange = function() {
  if (this.readyState === 4 && this.status === 200) {
   if(xmlhttp.responseText !== "null"){
    window.location.href = "waitingRoom.html?name=" + name + "&gameID=" + gameID;
   }else{
    alert("You cannot join this game!");
   }
  }
 };
 xmlhttp.open("POST", host + "/api/game/join/" + gameID);
 xmlhttp.setRequestHeader("Content-Type", "application/json");
 console.log("in join");
 // la waiting room deve aggiornarsi nel momento in cui entrano altri utenti.
 xmlhttp.send(JSON.stringify(value));

}
