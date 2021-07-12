var name = "";

function init(){
 M.AutoInit();
}

function checkInput(elem){
    if($(elem).val() === ""){
        $(elem).addClass("invalid");
        return false;
    }
    return true;
}

function newGame(){
    if(checkInput('#userID')) {
        window.location.href = "settings.html?name=" + $('#userID').val();
    }
}

function join(){
    if($('#gameIDrow').is(":visible") && checkInput('#gameID')){
        var name = $('#userID').val();
        var gameID = $('#gameID').val();
        var form = $('#initialForm')[0];
        const data = new FormData(form);
        const value = Object.fromEntries(data.entries());
        window.location.href = "game.html?name=" + name + "&gameID=" + gameID;
    }
    $('#gameIDrow').show()
}
