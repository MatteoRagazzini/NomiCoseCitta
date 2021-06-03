var name = "";

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
 window.location.href = "game.html?name=" + name + "&gameID=" + gameID;

}
