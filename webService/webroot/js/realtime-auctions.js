var auction_id = 1;

function init() {
    loadCurrentPrice();
    registerHandlerForUpdateCurrentPriceAndFeed();
};

function updateAuction(){
  auction_id = document.getElementById('my_auction').value;
  init();
}

function loadCurrentPrice() {
    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 200) {
                document.getElementById('current_price').innerHTML = 'EUR ' + JSON.parse(xmlhttp.responseText).price.toFixed(2);
            } else {
                document.getElementById('current_price').innerHTML = 'EUR 0.00';
            }
        }
    };
    console.log("in loadCurrentPrice auction " + auction_id);
    xmlhttp.open("GET", "http://localhost:8080/api/auctions/" + auction_id);
    xmlhttp.send();
};

function registerHandlerForUpdateCurrentPriceAndFeed() {
    var eventBus = new EventBus('http://localhost:8080/eventbus');
    console.log("register acution." + auction_id);
    eventBus.onopen = function () {
        eventBus.registerHandler('auction.' + auction_id, function (error, message) {
            document.getElementById('current_price').innerHTML = 'EUR ' + JSON.parse(message.body).price;
            document.getElementById('feed').value += 'New offer: EUR ' + JSON.parse(message.body).price + '\n';
        });
    }
};

function bid() {
    var newPrice = parseFloat(Math.round(document.getElementById('my_bid_value').value.replace(',','.') * 100) / 100).toFixed(2);

    var xmlhttp = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 200) {
                console.log("updated auction" + auction_id + "with price" + newPrice);
                document.getElementById('error_message').innerHTML = '';
            } else {
                document.getElementById('error_message').innerHTML = 'Invalid price!';
            }
        }
    };
    console.log("acution." + auction_id);
    xmlhttp.open("PATCH", "http://localhost:8080/api/auctions/" + auction_id);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(JSON.stringify({price: newPrice}));
};
