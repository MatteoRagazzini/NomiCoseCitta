var name = "";

function init(){
 M.AutoInit();
}

function checkUserIDInput(){
    if($('#userID').val() === ""){
        $('#userID').addClass("invalid");
        return false;
    }
    return true;
}

function checkGameIDInput(){
    if($('#gameID').val() === ""){
        $('#gameID').addClass("invalid");
        return false;
    }
    return true;
}

function newGame(){
    if(checkUserIDInput()) {
        window.location.href = "settings.html?name=" + $('#userID').val();
    }
}

function join(){
    if(checkUserIDInput() && checkGameIDInput()){
        var name = $('#userID').val();
        var gameID = $('#gameID').val();
        var form = $('#initialForm')[0];
        const data = new FormData(form);
        const value = Object.fromEntries(data.entries());
        console.log({value});
        window.location.href = "game.html?name=" + name + "&gameID=" + gameID;
    }
}
