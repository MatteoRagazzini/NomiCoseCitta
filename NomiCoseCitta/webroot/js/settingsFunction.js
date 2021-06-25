var gameID = 0;
var name = "";
var host = "http://localhost:8080";

function init() {
    M.AutoInit();
   var url = new URL(document.URL);
   name = url.searchParams.get("name");
   $('#userID').attr('placeholder', name);
}

function create() {
   const data = new FormData($('#settingsForm')[0]);
   const value = Object.fromEntries(data.entries());
   value.categories = [];
   var chipsTag = M.Chips.getInstance($('.chips')).chipsData;
   chipsTag.forEach(chips =>{
       value.categories.push(chips.tag);
   })

   $.post(host + "/api/game/create", JSON.stringify(value), function (data,status) {
       console.log(status);
       if (status === 'success') {
           gameID = data;
           console.log("inside creation callback");
           $(window).attr('location', "game.html?name=" + name + "&gameID=" + gameID);
       }}
   );
}

$(document).ready(function(){
    $('.chips-initial').chips({
        data: [{
            tag: 'Nomi',
        }, {
            tag: 'Cose',
        }, {
            tag: 'Citta',
        }],
    });
});



