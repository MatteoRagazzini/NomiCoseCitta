function init(){
    var url = new URL(document.URL);
    var name = url.searchParams.get("name");
    console.log(name);
    var gameID = url.searchParams.get("gameID");
    addItem(name);
}

function addItem(name){
    var ul = document.getElementById("dynamic-list");
    var li = document.createElement("li");
    li.setAttribute('id',name);
    li.appendChild(document.createTextNode(name));
    ul.appendChild(li);
}
