var name = "";

function init(){
}


function newGame(){
 window.location.href = "settings.html?name="+$('#userID').val();
}

function join(){
 var name = $('#userID').val();
 var gameID = $('#gameID').val();
 var form = $('#initialForm')[0];
 const data = new FormData(form);
 const value = Object.fromEntries(data.entries());
 console.log({value});
 window.location.href = "game.html?name=" + name + "&gameID=" + gameID;

}
