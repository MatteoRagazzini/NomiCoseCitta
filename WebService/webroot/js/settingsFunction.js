var gameID = 0;
var name = "";
var ip = "95.232.110.117";
var local = "localhost"
var host = "http://"+local+":8080";

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
   chipsTag.forEach(chip =>{
       value.categories.push(chip.tag);
   })

   $.post(host + "/api/game/create", JSON.stringify(value), function (data,status) {
       if (status === 'success') {
           gameID = data;
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
            tag: "Citt\u00E0",
        }],
    });
});



